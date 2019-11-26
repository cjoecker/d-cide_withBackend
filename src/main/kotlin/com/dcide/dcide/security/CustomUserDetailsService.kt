@file:Suppress("SpringJavaAutowiredMembersInspection")

package com.dcide.dcide.security

import com.dcide.dcide.model.User
import com.dcide.dcide.model.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import javax.transaction.Transactional


@Service
class CustomUserDetailsService : UserDetailsService {

    @Autowired
    lateinit var userRepository: UserRepository

    override fun loadUserByUsername(username: String): UserDetails? {
        val user = userRepository.findByUsername(username)
        if (user == null) UsernameNotFoundException("User not found")
        return user
    }

    @Transactional
    fun loadUserById(id: Long?): User? {
        val user = userRepository.getById(id)
        if (user == null) UsernameNotFoundException("User not found")
        return user

    }
}