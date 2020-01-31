package com.dcide.dcide.model

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface WeightedCriteriaRepository : CrudRepository<WeightedCriteria, Long> {

    fun getById(id: Long?): WeightedCriteria?

    override fun findAll(): Iterable<WeightedCriteria>
}
