package com.dcide.dcide


import com.dcide.dcide.model.User
import com.dcide.dcide.model.UserRepository
import com.dcide.dcide.service.UserService
import org.junit.Assert.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class DCideApplicationTests {

	@Autowired
	lateinit var userService: UserService

	@Autowired
	lateinit var userRepository: UserRepository

	val username = "test@user.com"
	var user: User? = null

	@BeforeAll
	internal fun initEach() {
		if(userRepository.findByUsername(username) == null)
			createTestUser(username)

		user = userRepository.findByUsername(username)
	}

	fun createTestUser(username: String){
		val newUser = User(
				0,
				true,
				username,
				"Test User",
				"TestUser123",
				"TestUser123",
				Date(),
				Date()
		)
		userService.saveUser(newUser)
		userService.createDecisionForUnregisteredUser(newUser)
	}

	@Test
	fun calculatesDecisionOptionScore() {
		assertTrue("test user found",user != null)
	}

}

