/*
 * Created by sahmad on 23/01/19 09:16
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "ds_disease")
@Getter
@Setter
public class Disease extends BaseEntity {

    private static final long serialVersionUID = 924803633810006763L;

    @Column(name = "disease_id")
    private String diseaseId;

    @Column(name = "disease_name")
    private String name;

    @Column(name = "description")
    private String desc;

    @Column
    private String acronym;

    @ManyToMany(mappedBy = "diseases")
    Set<Protein> proteins;

    @Override
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        }

        if(obj == null || getClass() != obj.getClass()){
            return false;
        }

        Disease disease = (Disease) obj;
        return Objects.equals(getDiseaseId(), disease.getDiseaseId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDiseaseId());
    }
}
