/*
 * Created by sahmad on 07/02/19 12:18
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.rest.controller;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ebi.uniprot.ds.common.model.Protein;
import uk.ac.ebi.uniprot.ds.common.model.ProteinCrossRef;
import uk.ac.ebi.uniprot.ds.rest.dto.ProteinCrossRefDTO;
import uk.ac.ebi.uniprot.ds.rest.dto.ProteinWithCrossRefsDTO;
import uk.ac.ebi.uniprot.ds.rest.filter.RequestCorrelation;
import uk.ac.ebi.uniprot.ds.rest.response.MultipleEntityResponse;
import uk.ac.ebi.uniprot.ds.rest.service.ProteinService;

import javax.validation.constraints.Size;
import java.util.List;

@RestController
@RequestMapping("/v1/ds")
@Validated
@Slf4j
public class ProteinCrossRefController {

    @Autowired
    private ProteinService proteinService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping(value={"/proteins/{accessions}/xrefs"}, name = "Get the cross refs for the given list of accession")
    public MultipleEntityResponse<ProteinWithCrossRefsDTO> getProteinsXRefs(
            @Size(min = 1, max = 200, message = "The total count of accessions passed must be between 1 and 200 both inclusive.")
            @PathVariable(name = "accessions")
                    List<String> accessions)
    {
        String requestId = RequestCorrelation.getCorrelationId();
        List<Protein> proteins = this.proteinService.getAllProteinsByAccessions(accessions);
        List<ProteinWithCrossRefsDTO> dtos = toProteinCrossRefsDTOList(proteins);
        MultipleEntityResponse<ProteinWithCrossRefsDTO> resp = new MultipleEntityResponse<>(requestId, dtos);
        return resp;
    }

    @GetMapping(value={"/protein/{accession}/xrefs"}, name = "Get the protein cross refs for a given accession")
    public MultipleEntityResponse<ProteinCrossRefDTO> getProteinXRefs(@PathVariable(name = "accession") String accession) {
        String requestId = RequestCorrelation.getCorrelationId();
        List<ProteinCrossRef> xrefs = this.proteinService.getProteinCrossRefsByAccession(accession);
        List<ProteinCrossRefDTO> dtoList = toProteinCrossRefDTOList(xrefs);
        return new MultipleEntityResponse<>(requestId, dtoList);
    }

    private List<ProteinCrossRefDTO> toProteinCrossRefDTOList(List<ProteinCrossRef> xrefs){
        List<ProteinCrossRefDTO> dtoList = modelMapper.map(xrefs,
                new TypeToken<List<ProteinCrossRefDTO>>(){}.getType());
        return dtoList;
    }

    private List<ProteinWithCrossRefsDTO> toProteinCrossRefsDTOList(List<Protein> from){
        return this.modelMapper.map(from, new TypeToken<List<ProteinWithCrossRefsDTO>>(){}.getType());
    }
}
