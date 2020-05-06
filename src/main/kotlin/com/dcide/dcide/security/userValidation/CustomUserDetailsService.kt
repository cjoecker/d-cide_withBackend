@file:Suppress("SpringJavaAutowiredMembersInspection")

package com.dcide.dcide.security.userValidation

import com.dcide.dcide.model.User
import com.dcide.dcide.model.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import javax.transaction.Transactional


@Service
class CustomUserDetailsService : UserDetailsService {
    @Autowired
    lateinit var userRepository: UserRepository

    override fun loadUserByUsername(username: String): UserDetails? {
        return userRepository.findByUsername(username)
    }

    @Transactional
    fun loadUserById(id: Long?): User? {
        return userRepository.getById(id)

    }
}