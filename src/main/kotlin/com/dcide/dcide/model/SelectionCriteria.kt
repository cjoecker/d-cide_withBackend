package com.dcide.dcide.model

import com.fasterxml.jackson.annotation.JsonIgnore
import lombok.Data
import lombok.NoArgsConstructor
import lombok.RequiredArgsConstructor
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
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
        var project: Project?


) {
    //Relationships
    //Child
//    @OneToOne(fetch = FetchType.EAGER, cascade = [CascadeType.REFRESH], mappedBy = "selectionCriteria1")
//    @JsonIgnore
//    val weightedCriteria1: WeightedCriteria? = null
//
//    @OneToOne(fetch = FetchType.EAGER, cascade = [CascadeType.REFRESH], mappedBy = "selectionCriteria2")
//    @JsonIgnore
//    val weightedCriteria2: WeightedCriteria? = null
//
//    @OneToOne(fetch = FetchType.EAGER, cascade = [CascadeType.REFRESH], mappedBy = "selectedCriteria")
//    @JsonIgnore
//    val weightedCriteria3: WeightedCriteria? = null


    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.REFRESH], mappedBy = "selectionCriteria", orphanRemoval = true)
    @JsonIgnore
    val ratedOption: MutableSet<RatedOption> = mutableSetOf()
}