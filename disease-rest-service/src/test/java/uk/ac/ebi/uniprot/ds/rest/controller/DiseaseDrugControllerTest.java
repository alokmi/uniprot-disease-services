/*
 * Created by sahmad on 07/02/19 15:02
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.rest.controller;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import uk.ac.ebi.uniprot.ds.common.model.*;
import uk.ac.ebi.uniprot.ds.rest.DataSourceTestConfig;
import uk.ac.ebi.uniprot.ds.rest.dto.DrugDTO;
import uk.ac.ebi.uniprot.ds.rest.service.DiseaseService;
import uk.ac.ebi.uniprot.ds.rest.service.DrugService;
import uk.ac.ebi.uniprot.ds.rest.service.ProteinService;
import uk.ac.ebi.uniprot.ds.rest.service.VariantService;
import uk.ac.ebi.uniprot.ds.rest.utils.ModelCreationUtils;

import java.util.*;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@WebMvcTest(DiseaseController.class)
@Import({DataSourceTestConfig.class})
//@ComponentScan(basePackages = {"uk.ac.ebi.uniprot.ds.rest"})
public class DiseaseDrugControllerTest {
    private String uuid = UUID.randomUUID().toString();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DiseaseService diseaseService;

    @MockBean
    private ProteinService proteinService;

    @MockBean
    private VariantService variantService;

    @MockBean
    private DrugService drugService;

    @Test
    public void testGetDrugsByDiseaseId() throws Exception {
        String diseaseId = "DISEASE_ID";

        Drug drug1 = ModelCreationUtils.createDrugObject(this.uuid + 1);
        Drug drug2 = ModelCreationUtils.createDrugObject(this.uuid + 2);
        Drug drug3 = ModelCreationUtils.createDrugObject(this.uuid + 3);

        Set<String> accessions = new HashSet<>();
        accessions.add("AC1");accessions.add("AC2");accessions.add("AC3");
        Set<String> diseases = new HashSet<>();
        diseases.add("DIS1"); diseases.add("DIS2"); diseases.add("DIS3");
        // set disease names and accessions to each drug object
        drug1.setDiseases(diseases);
        drug1.setProteins(accessions);
        drug2.setDiseases(diseases);
        drug2.setProteins(accessions);
        drug3.setDiseases(diseases);
        drug3.setProteins(accessions);
        List<Drug> drugs = Arrays.asList(drug1, drug2, drug3);
        List<DrugDTO> drugDTOs = new ArrayList<>();
        for(Drug drug : drugs){
            DrugDTO.DrugDTOBuilder bldr = DrugDTO.builder();
            bldr.name(drug.getName()).sourceType(drug.getSourceType());
            bldr.sourceId(drug.getSourceId()).moleculeType(drug.getMoleculeType());
            bldr.clinicalTrialLink(drug.getClinicalTrialLink());
            bldr.evidences(drug.getDrugEvidences().stream().map(e -> e.getRefUrl()).collect(Collectors.toSet()));
            bldr.proteins(drug.getProteins());
            Set<DrugDTO.BasicDiseaseDTO> bDiseases = drug.getDiseases().stream().map(d -> DrugDTO.BasicDiseaseDTO.builder().diseaseName(d).diseaseId(d).build()).collect(Collectors.toSet());
            bldr.diseases(bDiseases);
            drugDTOs.add(bldr.build());
        }


        Mockito.when(this.drugService.getDrugDTOsByDiseaseId(diseaseId)).thenReturn(drugDTOs);

        ResultActions res = this.mockMvc.perform
                (
                        MockMvcRequestBuilders.
                                get("/v1/ds/disease/" + diseaseId + "/drugs").
                                param("diseaseId", diseaseId)
                );

        res.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestId", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hasError", Matchers.equalTo(false)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.warnings", Matchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.results.length()", Matchers.equalTo(drugs.size())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.results[*].name", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.results[*].sourceType", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.results[*].sourceId", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.results[*].moleculeType", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.results[*].clinicalTrialPhase", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.results[*].mechanismOfAction", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.results[*].clinicalTrialLink", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.results[*].evidences", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.results[0].evidences.length()", Matchers.equalTo(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.results[*].diseases", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.results[*].proteins", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.results[0].diseases.length()", Matchers.equalTo(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.results[0].proteins.length()", Matchers.equalTo(3)));
    }
}
