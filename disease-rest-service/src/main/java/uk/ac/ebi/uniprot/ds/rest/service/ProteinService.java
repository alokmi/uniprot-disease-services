/*
 * Created by sahmad on 07/02/19 12:19
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.rest.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.uniprot.ds.common.dao.ProteinDAO;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.Interaction;
import uk.ac.ebi.uniprot.ds.common.model.Protein;
import uk.ac.ebi.uniprot.ds.common.model.ProteinCrossRef;
import uk.ac.ebi.uniprot.ds.rest.exception.AssetNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProteinService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProteinService.class);

    @Autowired
    private ProteinDAO proteinDAO;
    @Autowired
    private DiseaseService diseaseService;

    @Transactional
    public Protein createProtein(String proteinId, String proteinName, String accession, String gene, String description){
        LOGGER.info("Creating protein with protein id {}", proteinId);
        Protein.ProteinBuilder builder = Protein.builder();
        builder.proteinId(proteinId).name(proteinName);
        builder.accession(accession).gene(gene).desc(description);
        Protein protein = builder.build();
        this.proteinDAO.save(protein);
        LOGGER.info("The protein created with id {}", protein.getId());
        return protein;
    }

    public Optional<Protein> getProteinByProteinId(String proteinId){
        return this.proteinDAO.findByProteinId(proteinId);
    }

    public Optional<Protein> getProteinByAccession(String accession){
        Optional<Protein> optProtein = this.proteinDAO.findProteinByAccession(accession);
        if(!optProtein.isPresent()){
            throw new AssetNotFoundException("Unable to find the accession '" + accession + "'.");
        }
        return optProtein;
    }

    public List<Protein> getAllProteinsByAccessions(List<String> accessions){
        List<Protein> proteins = this.proteinDAO.getProteinsByAccessions(accessions);
        return proteins;
    }

    public List<ProteinCrossRef> getProteinCrossRefsByAccession(String accession){
        Optional<Protein> optProtein = this.proteinDAO.findProteinByAccession(accession);
        List<ProteinCrossRef> proteinCrossRefs = new ArrayList<>();
        if(optProtein.isPresent()){
            proteinCrossRefs = optProtein.get().getProteinCrossRefs();
        }
        return proteinCrossRefs;
    }

    public List<Interaction> getProteinInteractions(String accession){
        Optional<Protein> optProtein = getProteinByAccession(accession);
        List<Interaction> interactions = null;
        if(optProtein.isPresent()) {
            interactions = optProtein
                            .get()
                            .getInteractions()
                            .stream().filter(intrxn -> !intrxn.getType().equals("SELF"))
                            .collect(Collectors.toList());
        }
        return interactions;
    }

    public List<Protein> getProteinsByDiseaseId(String diseaseId) {
        Optional<Disease> optDisease = this.diseaseService.findByDiseaseId(diseaseId);

        List<Protein> proteins = null;

        if(optDisease.isPresent()) {
            proteins = optDisease.get().getProteins();
        }

        return proteins;
    }
}
