package uk.ac.ebi.uniprot.disease.model.sources.disgenet;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
public class UniProtGene {
    private String uniProtId;
    private long geneId;
}
