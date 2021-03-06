package uk.ac.ebi.uniprot.ds.graphql.model;

import graphql.annotations.annotationTypes.GraphQLName;
import lombok.Data;

import java.util.List;

@Data
@GraphQLName("Drug")
public class DrugType {
    private String name;
    private String sourceType;
    private String sourceId;
    private String moleculeType;
    private Integer clinicalTrialPhase;
    private String mechanismOfAction;
    private String clinicalTrialLink;
    private List<DrugEvidenceType> drugEvidences;
}
