package com.dcide.dcide.model


import com.dcide.dcide.model.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository


@Repository
interface UserRepository : CrudRepository<User, Long> {

    fun findByUsername(username: String): User?
    fun getById(id: Long?): User?
}