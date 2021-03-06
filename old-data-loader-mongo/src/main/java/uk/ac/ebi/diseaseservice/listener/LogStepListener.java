/*
 * Created by sahmad on 16/01/19 10:33
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.diseaseservice.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

public class LogStepListener implements StepExecutionListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogStepListener.class);

    @Override
    public void beforeStep(StepExecution stepExecution) {
        LOGGER.info("Disease import STEP '{}' starting.", stepExecution.getStepName());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        LOGGER.info("=====================================================");
        LOGGER.info("              Disease import Step Statistics                 ");
        LOGGER.info("Step name     : {}", stepExecution.getStepName());
        LOGGER.info("Exit status   : {}", stepExecution.getExitStatus().getExitCode());
        LOGGER.info("Read count    : {}", stepExecution.getReadCount());
        LOGGER.info("Write count   : {}", stepExecution.getWriteCount());
        LOGGER.info("Skip count    : {} ({} read / {} processing /{} write)", stepExecution.getSkipCount(),
                stepExecution.getReadSkipCount(), stepExecution.getProcessSkipCount(),
                stepExecution.getWriteSkipCount());
        LOGGER.info("=====================================================");
        return stepExecution.getExitStatus();
    }
}
