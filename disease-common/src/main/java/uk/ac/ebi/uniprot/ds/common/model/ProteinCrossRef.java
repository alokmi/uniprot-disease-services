/*
 * Created by sahmad on 07/02/19 10:36
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.common.model;

import lombok.*;
import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "ds_protein_cross_ref")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProteinCrossRef extends BaseEntity {

    private static final long serialVersionUID = -5081945044878223589L;
    @Column(name = "primary_id")
    private String primaryId;

    @Column(name = "description")
    private String desc;

    @Column(name = "db_type")
    private String dbType;

    @Column(name = "isoform_id")
    private String isoformId;

    @Column
    private String third;

    @Column
    private String fourth;

    @ManyToOne
    @JoinColumn(name = "ds_protein_id")
    private Protein protein;

    @OneToMany(mappedBy = "proteinCrossRef", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Drug> drugs;
}
