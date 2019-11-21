package com.dcide.dcide.model

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SelectionCriteriaRepository : CrudRepository<SelectionCriteria, Long> {

    fun getById(id: Long?): SelectionCriteria?

    override fun findAll(): Iterable<SelectionCriteria>
}
