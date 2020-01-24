package com.dcide.dcide.model

import com.fasterxml.jackson.annotation.JsonIgnore
import lombok.Data
import lombok.NoArgsConstructor
import lombok.RequiredArgsConstructor
import org.apache.catalina.User
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import javax.persistence.*


@Entity
data class WeightedCriteria(
        @Id @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long,

        var weight: Int = 50,

        var selectionCriteria1Id: Long,
        var selectionCriteria2Id: Long,

        var selectedCriteriaId: Long,


        @ManyToOne(fetch = FetchType.EAGER)
        @JsonIgnore
        var decision: Decision?

)