/*
 * Created by sahmad on 1/17/19 11:28 AM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.diseaseservice.listener;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.diseaseservice.common.StepScopeTestConfig;
import uk.ac.ebi.diseaseservice.config.TestConfiguration;

@SpringBootTest(classes = TestConfiguration.class)
@SpringBatchTest
@RunWith(SpringRunner.class)
@ContextConfiguration
public class LogStepListenerTest extends StepScopeTestConfig {
    @Autowired
    LogStepListener logStepListener;

    @Test
    public void testBeforeStep() throws Exception {
        logStepListener.beforeStep(getStepExecution());
    }

    @Test
    public void testAfterStep(){
        logStepListener.afterStep(getStepExecution());
    }

}
