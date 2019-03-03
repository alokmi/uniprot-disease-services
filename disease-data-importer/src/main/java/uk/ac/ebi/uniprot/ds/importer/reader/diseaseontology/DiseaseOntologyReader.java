/*
 * Created by sahmad on 30/01/19 09:30
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.importer.reader.diseaseontology;

import org.springframework.batch.item.ItemReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;


public class DiseaseOntologyReader implements ItemReader<List<OBOTerm>> {
   // Constants
    private static final String TERM_STR = "[Term]";
    private static final String TYPEDEF_STR = "[Typedef]";
    private static final Pattern TERM_PATTERN = Pattern.compile("^\\s*$", Pattern.MULTILINE);
    private static final String NEW_LINE = "\n";
    private static final String COLON_SPACE = ": ";
    private static final String SPACE_EXCL = " !";
    private static final String ID = "id";
    private static final String  NAME = "name";
    private static final String SYNONYM = "synonym";
    private static final String  IS_A = "is_a";
    private static final String  DEF = "def";
    private static final String ALT_ID = "alt_id";
    private static final String XREF = "xref";
    private static final String  IS_OBSOLETE = "is_obsolete";

    private Scanner reader;
    private boolean termStarted;

    public DiseaseOntologyReader(String fileName) throws FileNotFoundException {
        this.reader = new Scanner(new File(fileName), StandardCharsets.UTF_8.name());
        this.termStarted = false;
    }

    public List<OBOTerm> read() {
        // skip the un-needed lines
        while (this.reader.hasNext() && !this.termStarted) {
            String lines = this.reader.nextLine();
            if (lines.trim().isEmpty()) {
                this.termStarted = true;
                this.reader.useDelimiter(TERM_PATTERN);
            }
        }
        // read all terms in a list
        List<OBOTerm> oboTerms = new ArrayList<>();
        OBOTerm oboTerm;
        while ((oboTerm = readNextTerm()) != null) {
            if (!oboTerm.isObsolete()) {
                oboTerms.add(oboTerm);
            }
        }

        return oboTerms.isEmpty() ? null : oboTerms;
    }

    private OBOTerm readNextTerm(){
        String termStr = null;

        if(this.reader.hasNext()) {
            termStr = this.reader.next();
        }

        if(termStr == null || termStr.trim().startsWith(TYPEDEF_STR)) {
            return null;
        }

        OBOTerm oboTerm = convertToOBOTerm(termStr);
        return oboTerm;
    }

    private OBOTerm convertToOBOTerm(String termStr) {

        OBOTerm.OBOTermBuilder builder = OBOTerm.builder();

        String[] lines = termStr.split(NEW_LINE);

        List<String> synonyms = new ArrayList<>();
        List<String> altIds = new ArrayList<>();
        List<String> xrefs = new ArrayList<>();
        List<String> parentIds = new ArrayList<>();

        for (String line : lines) {
            if (!(line.startsWith(TERM_STR) || line.trim().isEmpty())) {
                String[] lineTokens = line.split(COLON_SPACE);
                switch (lineTokens[0]) {
                    case ID:
                        builder.id(lineTokens[1]);
                        break;
                    case NAME:
                        builder.name(lineTokens[1]);
                        break;
                    case SYNONYM:
                        synonyms.add(lineTokens[1]);
                        break;
                    case IS_A:
                        parentIds.add(lineTokens[1].split(SPACE_EXCL)[0]);
                        break;
                    case DEF:
                        builder.definition(lineTokens[1]);
                        break;
                    case ALT_ID:
                        altIds.add(lineTokens[1]);
                        break;
                    case XREF:
                        xrefs.add(lineTokens[1]);
                        break;
                    case IS_OBSOLETE:
                        builder.isObsolete(Boolean.valueOf(lineTokens[1]));
                        break;
                    default:
                        // do nothing
                }
            }
        }

        builder.isAs(parentIds);
        builder.synonyms(synonyms);
        builder.altIds(altIds);
        builder.xrefs(xrefs);

        return builder.build();
    }


//    public static void main(String[] args) throws Exception {
//        List<OBOTerm> oboTerms = new ArrayList<>();
//        DiseaseOntologyReader reader = new DiseaseOntologyReader("/Users/sahmad/Documents/HumanDO.obo.txt");
//        OBOTerm oboTerm;
//        while ((oboTerm = reader.read()) != null) {
//            if (!oboTerm.isObsolete()) {
//                oboTerms.add(oboTerm);
//            }
//        }
//        AdjacencyList tree = new AdjacencyList();
//        Map<String, Node> adjList = tree.buildAdjacencyList(oboTerms);
//        List<OBOTerm> noOMIM = new ArrayList<>();
//        List<OBOTerm> withOMIM = new ArrayList<>();
//        StringBuilder in = new StringBuilder();
//
//        for(Map.Entry<String, Node> entry : adjList.entrySet()){
//            Node node = entry.getValue();
//            List<String> xrefs = node.getTerm().getXrefs();
//            String omim = getOMIM(xrefs);
//            if(omim == null){
//                noOMIM.add(node.getTerm());
//            } else {
//                withOMIM.add(node.getTerm());
//                in.append("'").append(omim).append("'").append(",");
//            }
//        }
//
//
//        System.out.println(adjList);
//    }
//
//    private static String getOMIM(List<String> xrefs){
//        if(xrefs == null || xrefs.isEmpty()){
//            return null;
//        }
//        for(String xref : xrefs){
//            if(xref.startsWith("OMIM")){
//                return xref.split(":")[1];
//            }
//        }
//
//        return null;
//    }

}