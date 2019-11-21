package com.dcide.dcide.model

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*


@Entity
data class RatedOption(
        @Id @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long,
        var rating: Int = 50,


        //Relationships
        //Parent
        @ManyToOne(fetch = FetchType.EAGER)
        var decisionOption: DecisionOption,

        @ManyToOne(fetch = FetchType.EAGER)
        var selectionCriteria: SelectionCriteria

)