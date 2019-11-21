package com.dcide.dcide.model

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface DecisionOptionRepository : CrudRepository<DecisionOption, Long> {

    fun getById(id: Long?): DecisionOption?

    override fun findAll(): Iterable<DecisionOption>
}