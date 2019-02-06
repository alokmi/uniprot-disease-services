/*
 * Created by sahmad on 06/02/19 12:16
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import uk.ac.ebi.uniprot.ds.EclipselinkSpringDataApplication;
import uk.ac.ebi.uniprot.ds.controller.mapper.EntityToDTOMapper;
import uk.ac.ebi.uniprot.ds.model.*;
import uk.ac.ebi.uniprot.ds.service.ProteinService;

import java.util.*;
import java.util.function.Predicate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@WebMvcTest(ProteinController.class)
@ContextConfiguration(classes={EclipselinkSpringDataApplication.class, EntityToDTOMapper.class})
public class ProteinControllerGetPathwaysIT {
    private String uuid = UUID.randomUUID().toString();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProteinService proteinService;

    @Test
    public void testNonExistentAccessions() throws Exception {
        String accessions = "acc1,acc2,acc3,acc4";
        Mockito.when(this.proteinService.getAllProteinsByAccessions(ArgumentMatchers.anyList())).thenReturn(new ArrayList<>());
        ResultActions res = this.mockMvc.perform
                (
                        MockMvcRequestBuilders.
                                get("/v1/ds/" + accessions +"/pathways").
                                param("accessions", accessions)
                );

        res.andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.requestId", notNullValue()))
                .andExpect(jsonPath("$.hasError", equalTo(false)))
                .andExpect(jsonPath("$.warnings", nullValue()))
                .andExpect(jsonPath("$.results", notNullValue()))
                .andExpect(jsonPath("$.results.length()", equalTo(0)));
    }

    public boolean hasAttribute(Predicate<Object> predicate, Object input){
        return predicate.test(input);
    }

    @Test
    public void testGetPathways() throws Exception {

        // create multiple proteins
        Protein p1 = ProteinTest.createProteinObject(uuid + 1);
        String a1 = "ACC1-"+ uuid;
        p1.setAccession(a1);
        Pathway pt1 = PathwayTest.createPathwayObject(uuid + 1);
        Pathway pt2 = PathwayTest.createPathwayObject(uuid + 2);
        Pathway pt3 = PathwayTest.createPathwayObject(uuid + 3);
        p1.setPathways(Arrays.asList(pt1, pt2, pt3));

        Protein p2 = ProteinTest.createProteinObject(uuid + 2);
        String a2 = "ACC2-"+ uuid;
        p2.setAccession(a2);
        Pathway pt4 = PathwayTest.createPathwayObject(uuid + 4);
        p2.setPathways(Arrays.asList(pt4));

        Protein p3 = ProteinTest.createProteinObject(uuid + 3);
        String a3 = "ACC3-"+ uuid;
        p3.setAccession(a3);

        List<Protein> proteins = Arrays.asList(p1, p2, p3);

        Mockito.when(this.proteinService.getAllProteinsByAccessions(Arrays.asList(a1, a2, a3))).thenReturn(proteins);
        String accessions = a1 + "," + a2 + "," + a3;

        ResultActions res = this.mockMvc.
                perform(MockMvcRequestBuilders.get("/v1/ds/" + accessions + "/pathways").param("accessions", accessions));

        res.andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.requestId", notNullValue()))
                .andExpect(jsonPath("$.hasError", equalTo(false)))
                .andExpect(jsonPath("$.warnings", nullValue()))
                .andExpect(jsonPath("$.results", notNullValue()))
                .andExpect(jsonPath("$.results.length()", equalTo(proteins.size())))
                .andExpect(jsonPath("$.results[*].accession", notNullValue()))
                .andExpect(jsonPath("$.results[*].proteinId", notNullValue()))
                .andExpect(jsonPath("$.results[*].proteinName", notNullValue()))
                .andExpect(jsonPath("$.results[*].gene", notNullValue()))
                .andExpect(jsonPath("$.results[0].pathways.length()", equalTo(p1.getPathways().size())))
                .andExpect(jsonPath("$.results[1].pathways.length()", equalTo(p2.getPathways().size())))
                .andExpect(jsonPath("$.results[2].pathways", nullValue()));
    }


    @Test
    public void testMoreThan20Accession() throws Exception {
        String accessions = "acc1,acc2,acc3,acc4,acc5,acc1,acc2,acc3,acc4,acc5,acc1,acc2,acc3,acc4,acc5,acc1,acc2,acc3,acc4,acc5,acc5";

        Mockito.when(this.proteinService.getAllProteinsByAccessions(ArgumentMatchers.anyList())).thenReturn(new ArrayList<>());
        ResultActions res = this.mockMvc.perform
                (
                        MockMvcRequestBuilders.
                                get("/v1/ds/" + accessions +"/pathways").
                                param("accessions", accessions)
                );

        res.andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.requestId", notNullValue()))
                .andExpect(jsonPath("$.hasError", equalTo(true)))
                .andExpect(jsonPath("$.warnings", nullValue()))
                .andExpect(jsonPath("$.errorCode", equalTo(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("$.errorMessage", equalTo("The total count of accessions passed must be between 1 and 20 both inclusive.")));
    }
}
