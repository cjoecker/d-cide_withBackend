package com.dcide.dcide.model

import com.dcide.dcide.model.Decision
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.core.userdetails.UserDetails
import java.util.*
import javax.persistence.*
import javax.validation.constraints.*
import org.springframework.security.core.GrantedAuthority

//Data Class cannot be used because getters and setter from UserDetails cannot be overwritten
@Entity
data class User(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var  id: Long  = 0,

        var registeredUser: Boolean = false,

        @field:NotEmpty (message = "Valid email is required")
        @field:Email(message = "Valid email is required")
        @Column(unique = true)
        private var username: String = "",

        @field:NotBlank(message = "Full name is required")
        var  fullName: String = "",

        @field:NotBlank(message = "Password is required")
        private var password: String = "",

        @Transient
        var confirmPassword: String = "",

        @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss")
        @Column(updatable = false)
        @JsonIgnore
        var  created_At: Date = Date(),

        @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss")
        @JsonIgnore
        var  updated_At: Date = Date()





):UserDetails {

    //Relationships
    //Child
    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.REFRESH], mappedBy = "user", orphanRemoval = true)
    @JsonIgnore
    val decision: MutableSet<Decision> = mutableSetOf()

    override fun getUsername(): String {
        return username
    }

    fun setUsername(username: String){
        this.username = username
    }

    override fun getPassword(): String {
        return password
    }

    fun setPassword(password: String){
        this.password = password
    }

    @JsonIgnore
    override fun getAuthorities(): MutableCollection<out GrantedAuthority>? {
        return null
    }

    @JsonIgnore
    override fun isEnabled(): Boolean {
        return true //To change body of created functions use File | Settings | File Templates.
    }

    @JsonIgnore
    override fun isCredentialsNonExpired(): Boolean {
        return true //To change body of created functions use File | Settings | File Templates.
    }

    @JsonIgnore
    override fun isAccountNonExpired(): Boolean {
        return true //To change body of created functions use File | Settings | File Templates.
    }

    @JsonIgnore
    override fun isAccountNonLocked(): Boolean {
        return true//To change body of created functions use File | Settings | File Templates.
    }

    @PrePersist
    fun onCreate(){
        this.created_At = Date()
    }

    @PreUpdate
    fun onUpdate(){
        this.updated_At = Date()
    }

}