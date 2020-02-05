package com.dcide.dcide.model

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
data class SelectionCriteria(
        @Id @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long,
        val name: String,
        val score: Double = 0.0,

        //Relationships
        //Parent
        @ManyToOne(fetch = FetchType.EAGER)
        @JsonIgnore
        var decision: Decision?


) {
}