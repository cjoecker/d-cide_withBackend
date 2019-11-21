package com.dcide.dcide.model

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface ProjectRepository : CrudRepository<Project, Long> {

    override fun findAll(): Iterable<Project>

}

