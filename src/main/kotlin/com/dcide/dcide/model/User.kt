package com.dcide.dcide.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*
import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty

//Data Class cannot be used because getters and setter from UserDetails cannot be overwritten
@Entity
data class User(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var  id: Long  = 0,

        @JsonIgnore
        var registeredUser: Boolean = true,

        @field:NotEmpty (message = "Valid email is required")
        @field:Email(message = "Valid email is required")
        @Column(unique = true)
        internal var username: String = "",

        @field:NotBlank(message = "Full name is required")
        var  fullName: String = "",

        @field:NotBlank(message = "Password is required")
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        private var password: String = "",

        @Transient
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        var confirmPassword: String = "",

        @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss")
        @Column(updatable = false)
        @JsonIgnore
        var  created_At: Date = Date(),

        @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss")
        @JsonIgnore
        var  updated_At: Date = Date()


):UserDetails {

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.REFRESH], mappedBy = "user", orphanRemoval = true)
    @JsonIgnore
    val decision: MutableSet<Decision> = mutableSetOf()

    override fun getUsername(): String {
        return username
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