package com.dcide.dcide.model

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository


@Repository
interface DecisionsRepository : CrudRepository<Decision, Long> {

    override fun findAll(): Iterable<Decision>

}

