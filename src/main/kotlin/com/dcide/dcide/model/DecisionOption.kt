package com.dcide.dcide.model


import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
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
        var project : Project?

){
        //Relationships
        //Child
        @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.REFRESH], mappedBy = "decisionOption", orphanRemoval = true)
        @JsonIgnore
        val ratedOption: MutableSet<RatedOption> = mutableSetOf()
}