package com.dcide.dcide.model

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*


@Entity
data class RatedOption(
        @Id @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long,

        var score: Int = 50,

        var decisionOptionId: Long,
        var selectionCriteriaId: Long,


        @ManyToOne(fetch = FetchType.EAGER)
        @JsonIgnore
        var decision: Decision?

)