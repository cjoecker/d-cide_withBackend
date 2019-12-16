package com.dcide.dcide.model

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface DecisionRepository : CrudRepository<Decision, Long> {

    fun getById(id: Long?): Decision?

    override fun findAll(): Iterable<Decision>
}