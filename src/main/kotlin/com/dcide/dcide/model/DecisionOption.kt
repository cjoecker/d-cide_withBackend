package com.dcide.dcide.model


import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
data class DecisionOption(
        @Id @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long,
        var name: String,
        var score: Double,

        //Relationships
        //Parent
        @ManyToOne(fetch = FetchType.EAGER)
        @JsonIgnore
        var decision : Decision?

){

}