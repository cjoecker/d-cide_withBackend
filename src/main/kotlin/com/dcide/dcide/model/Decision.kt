package com.dcide.dcide.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*
import javax.persistence.*


@Entity
data class Decision(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long?,

        var name: String = "",

        @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss")
        @Column(updatable = false)
        @JsonIgnore
        var created_At: Date = Date(),

        @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss")
        @JsonIgnore
        var updated_At: Date = Date(),


        //Relationships
        //Parent
        @ManyToOne(fetch = FetchType.EAGER)
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        var user : User?
) {

    //Relationships
    //Child
    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.REFRESH], mappedBy = "decision", orphanRemoval = true)
    @JsonIgnore
    val decisionOption: MutableSet<DecisionOption> = mutableSetOf()

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.REFRESH], mappedBy = "decision", orphanRemoval = true)
    @JsonIgnore
    val selectionCriteria: MutableSet<SelectionCriteria> = mutableSetOf()

    @PrePersist
    fun onCreate() {
        this.created_At = Date()
    }

    @PreUpdate
    fun onUpdate() {
        this.updated_At = Date()
    }
}