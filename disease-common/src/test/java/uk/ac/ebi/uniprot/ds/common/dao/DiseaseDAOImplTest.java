/*
 * Created by sahmad on 07/02/19 10:56
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.common.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.DiseaseTest;
import uk.ac.ebi.uniprot.ds.common.model.Protein;
import uk.ac.ebi.uniprot.ds.common.model.ProteinTest;

import java.util.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class DiseaseDAOImplTest {

    @Autowired
    private DiseaseDAO diseaseDAO;

    private Disease disease;
    private List<Disease> diseases;

    @AfterEach
    void cleanUp(){
        if(this.disease != null){
            this.diseaseDAO.delete(this.disease);
            this.disease = null;
        }

        if(this.diseases != null && !this.diseases.isEmpty()){
            this.diseases.forEach(disease -> this.diseaseDAO.delete(disease));
            this.diseases = null;
        }
    }

    @Test
    void testCreateDisease(){
        this.disease = this.diseaseDAO.save(DiseaseTest.createDiseaseObject());
        assertNotNull(this.disease.getId(), "Unable to save the disease");
    }

    @Test
    void testCreateUpdateDisease(){
        String random = UUID.randomUUID().toString();
        this.disease = createDisease();
        Long id = disease.getId();
        // update the disease
        String dId = "UDID-" + random;
        String dn = "UDN-" + random;
        String desc = "UDESC-" + random;
        String acr = "UACRONYM-" + random;
        this.disease.setDiseaseId(dId);
        this.disease.setName(dn);
        this.disease.setDesc(desc);
        this.disease.setAcronym(acr);
        this.disease = this.diseaseDAO.save(this.disease);

        // get the disease and verify
        Optional<Disease> optDis = this.diseaseDAO.findById(id);
        assertTrue(optDis.isPresent(), "unable to find the disease with id " + id);
        Disease sDis = optDis.get();
        assertEquals(id, sDis.getId());
        assertEquals(dId, sDis.getDiseaseId());
        assertEquals(dn, sDis.getName());
        assertEquals(desc, sDis.getDesc());
        assertEquals(acr, sDis.getAcronym());
    }

    @Test
    void testDeleteDisease(){
        // create the disease
        this.disease = createDisease();

        // delete the disease now
        this.diseaseDAO.delete(this.disease);
        // try to get the disease now
        Optional<Disease> optDisease = this.diseaseDAO.findById(this.disease.getId());
        assertFalse(optDisease.isPresent(), "Unable to delete the disease");
       this.disease= null;
    }

    @Test
    void testDeleteDiseaseByDiseaseId(){
        // create the disease
        this.disease = createDisease();

        // delete the disease now
        this.diseaseDAO.deleteByDiseaseId(this.disease.getDiseaseId());
        // try to get the disease now
        Optional<Disease> optDisease = this.diseaseDAO.findById(this.disease.getId());
        assertFalse(optDisease.isPresent(), "Unable to delete the disease by disease id");
        this.disease= null;
    }

    @Test
    void testGetDisease(){
        // create the disease
       this.disease = createDisease();
        // get the disease and verify
        Optional<Disease> optStoredDisease = this.diseaseDAO.findById(this.disease.getId());
        assertTrue(optStoredDisease.isPresent(), "unable to get the disease");

        Disease storedDisease = optStoredDisease.get();

        assertAll("disease values",
                () -> assertEquals(this.disease.getId(), storedDisease.getId()),
                () -> assertEquals(this.disease.getDiseaseId(), storedDisease.getDiseaseId()),
                () -> assertEquals(this.disease.getName(), storedDisease.getName()),
                () -> assertEquals(this.disease.getDesc(), storedDisease.getDesc()),
                () -> assertEquals(this.disease.getAcronym(), storedDisease.getAcronym()),
                () -> assertEquals(this.disease.getCreatedAt(), storedDisease.getCreatedAt()),
                () -> assertEquals(this.disease.getUpdatedAt(), storedDisease.getUpdatedAt())
                );
    }

    @Test
    void testDeleteById(){
        // create the disease
       this.disease = createDisease();

        this.diseaseDAO.deleteById(this.disease.getId());
        // try to get the disease now
        Optional<Disease> optDisease = this.diseaseDAO.findById(this.disease.getId());
        assertFalse(optDisease.isPresent(), "Unable to delete the disease");
       this.disease= null;
    }

    @Test
    void testGetAll(){
        this.diseases = new ArrayList<>();
        // create 50 diseases
        IntStream.range(1, 51).forEach(i -> this.diseases.add(createDisease()));
        // get first 25 diseases
        List<Disease> first25 = this.diseaseDAO.findAll();
        assertTrue(first25.size() >= 50, "Unable to get first 25 records");
    }

    @Test
    void testDeleteNonExistentDisease(){
        final long id = new Random().nextLong();

        EmptyResultDataAccessException exception = assertThrows(EmptyResultDataAccessException.class,
                () -> this.diseaseDAO.deleteById(id > 0 ? id : -id));

        assertTrue(exception.getMessage().contains("No class uk.ac.ebi.uniprot.ds.common.model.Disease entity with id"));
    }

    @Test
    void testGetNonExistentDisease(){
        long randId = (long) (Math.random()*100000);
        Optional<Disease> optDisease = this.diseaseDAO.findById(randId);
        assertFalse(optDisease.isPresent(), "Disease is found!");
    }

    @Test
    void testGetDiseasesByProteins(){
        String uuid = UUID.randomUUID().toString();
        this.disease = DiseaseTest.createDiseaseObject(uuid);
        Protein protein = ProteinTest.createProteinObject(uuid);
        this.disease.setProteins(Arrays.asList(protein));
        this.diseaseDAO.save(this.disease);

        List<Disease> dbDisease = this.diseaseDAO.findAllByProteinsIs(protein);
        assertEquals(1, dbDisease.size());
    }

    @Test
    void testSearchDiseasesByKeyword(){
        // create 100s of diseases
        String keyword = "123syndrome123";
        this.diseases = new ArrayList<>();
        IntStream.range(0, 100).forEach(i -> this.diseases.add(createDisease(keyword)));

        // search by keyword
        int size = 20;
        Sort sortBy = Sort.by("id");
        List<Disease> batch1 = this.diseaseDAO.findAllByNameContaining(keyword, PageRequest.of(0, size, sortBy));
        assertEquals(size, batch1.size());
        List<Disease> batch2 = this.diseaseDAO.findAllByNameContaining(keyword, PageRequest.of(1, size, sortBy));
        assertEquals(size, batch2.size());
        List<Disease> batch3 = this.diseaseDAO.findAllByNameContaining(keyword, PageRequest.of(2, size, sortBy));
        assertEquals(size, batch3.size());
        List<Disease> batch4 = this.diseaseDAO.findAllByNameContaining(keyword, PageRequest.of(3, size, sortBy));
        assertEquals(size, batch4.size());
        List<Disease> batch5 = this.diseaseDAO.findAllByNameContaining(keyword, PageRequest.of(4, size, sortBy));
        assertEquals(size, batch5.size());
        List<Disease> batch6 = this.diseaseDAO.findAllByNameContaining(keyword, PageRequest.of(5, size, sortBy));
        assertEquals(0, batch6.size());
    }

    private Disease createDisease(String keyword) {
        String uuid = UUID.randomUUID().toString();
        Disease dis = uuid.indexOf(uuid.length()-1) % 2 == 0 ?
                DiseaseTest.createDiseaseObject(uuid+keyword):DiseaseTest.createDiseaseObject(keyword+uuid);
        dis = this.diseaseDAO.save(dis);
        assertNotNull(dis.getId(), "Unable to save the disease");
        return dis;
    }



    private Disease createDisease() {
        String uuid = UUID.randomUUID().toString();
        Disease dis = DiseaseTest.createDiseaseObject(uuid);
        dis = this.diseaseDAO.save(dis);
        assertNotNull(dis.getId(), "Unable to save the disease");
        return dis;
    }
}