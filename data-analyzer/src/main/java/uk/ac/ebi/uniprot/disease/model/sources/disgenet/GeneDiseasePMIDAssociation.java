package uk.ac.ebi.uniprot.disease.model.sources.disgenet;

import lombok.*;

/**
 * @author sahmad
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class GeneDiseasePMIDAssociation {
    private Long geneId;
    private String geneSymbol;
    private String associationType;
    private String sentence;
    private String diseaseId;
    private String diseaseName;
    private String diseaseType;
    private Double score;
    private Long pmid;
    private String source;
}
