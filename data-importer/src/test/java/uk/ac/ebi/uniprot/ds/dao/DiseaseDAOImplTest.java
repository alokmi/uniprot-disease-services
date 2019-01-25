/*
 * Created by sahmad on 1/24/19 9:52 AM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.dao;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import uk.ac.ebi.uniprot.ds.dao.impl.DiseaseDAOImpl;
import uk.ac.ebi.uniprot.ds.exception.AssetNotFoundException;
import uk.ac.ebi.uniprot.ds.model.BaseTest;
import uk.ac.ebi.uniprot.ds.model.Disease;
import uk.ac.ebi.uniprot.ds.model.DiseaseTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

public class DiseaseDAOImplTest extends BaseTest {

    private DiseaseDAO diseaseDAO = new DiseaseDAOImpl(BaseTest.em);

    private Disease disease;
    private List<Disease> diseases;

    @AfterEach
    void cleanUp(){
        if(this.disease != null){
            executeInsideTransaction(dao -> dao.delete(this.disease), this.diseaseDAO);
            this.disease = null;
        }

        if(this.diseases != null && !this.diseases.isEmpty()){
            this.diseases.forEach(disease -> executeInsideTransaction(dao -> dao.delete(disease), this.diseaseDAO));
            this.diseases = null;
        }
    }

    @Test
    void testCreateDisease(){
        this.disease = DiseaseTest.createDiseaseObject();
        executeInsideTransaction(dao -> dao.createOrUpdate(this.disease), this.diseaseDAO);
        assertNotNull(this.disease.getId(), "Unable to save the disease");
    }

    @Test
    void testCreateUpdateDisease(){
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
        executeInsideTransaction(dao -> dao.createOrUpdate(this.disease), this.diseaseDAO);

        // get the disease and verify
        Optional<Disease> optDis = this.diseaseDAO.get(id);
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
        executeInsideTransaction(dao -> dao.delete(this.disease), this.diseaseDAO);
        // try to get the disease now
        Optional<Disease> optDisease = this.diseaseDAO.get(this.disease.getId());
        assertFalse(optDisease.isPresent(), "Unable to delete the disease");
       this.disease= null;
    }

    @Test
    void testGetDisease(){
        // create the disease
       this.disease = createDisease();
        // get the disease and verify
        Optional<Disease> optStoredDisease = this.diseaseDAO.get(this.disease.getId());
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

        executeInsideTransaction(dao -> dao.deleteById(this.disease.getId()), this.diseaseDAO);
        // try to get the disease now
        Optional<Disease> optDisease = this.diseaseDAO.get(this.disease.getId());
        assertFalse(optDisease.isPresent(), "Unable to delete the disease");
       this.disease= null;
    }

    @Test
    void testGetAll(){
        this.diseases = new ArrayList<>();
        // create 50 diseases
        IntStream.range(1, 51).forEach(i -> this.diseases.add(createDisease()));
        // get first 25 diseases
        List<Disease> first25 = this.diseaseDAO.getAll(0, 25);
        assertEquals(25, first25.size(), "Unable to get first 25 records");
        // get last 25 diseases
        List<Disease> last25 = this.diseaseDAO.getAll(25, 25);
        assertEquals(25, last25.size(), "Unable to get last 25 records");
        // try to get again, it should return empty result
        List<Disease> nonExistent = this.diseaseDAO.getAll(50, 75);
        assertTrue(nonExistent.isEmpty(), "we should get empty list");
    }

    @Test
    void testDeleteNonExistentDisease(){
        AssetNotFoundException exception = assertThrows(AssetNotFoundException.class, () -> this.diseaseDAO.deleteById((long) random));
        assertEquals("Unable to find the asset with id " + random, exception.getMessage());
    }

    @Test
    void testGetNonExistentDisease(){
        long randId = (long) (Math.random()*100000);
        Optional<Disease> optDisease = this.diseaseDAO.get(randId);
        assertFalse(optDisease.isPresent(), "Disease is found!");
    }

    private Disease createDisease() {
        String uuid = UUID.randomUUID().toString();
        Disease dis = DiseaseTest.createDiseaseObject(uuid);
        executeInsideTransaction(dao -> dao.createOrUpdate(dis), this.diseaseDAO);
        assertNotNull(dis.getId(), "Unable to save the disease");
        return dis;
    }
}
