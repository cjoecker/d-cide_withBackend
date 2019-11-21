package com.dcide.dcide.security


import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository


@Repository
interface UserRepository : CrudRepository<User, Long> {

    fun findByUsername(username: String): User?
    fun getById(id: Long?): User?
}