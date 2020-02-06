package com.dcide.dcide.model

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*


@Entity
data class WeightedCriteria(
        @Id @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long,

        var weight: Int = 50,

        var selectionCriteria1Id: Long,
        var selectionCriteria2Id: Long,


        @ManyToOne(fetch = FetchType.EAGER)
        @JsonIgnore
        var decision: Decision?

)