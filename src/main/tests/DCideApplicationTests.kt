package com.dcide.dcide

import com.dcide.dcide.model.User
import com.dcide.dcide.model.UserRepository
import com.dcide.dcide.service.*
import org.junit.Assert.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DCideApplicationTests {

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var decisionService: DecisionService

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var ratedOptionService: RatedOptionService

    @Autowired
    lateinit var weightedCriteriaService: WeightedCriteriaService

    @Autowired
    lateinit var selectionCriteriaService: SelectionCriteriaService

    @Autowired
    lateinit var decisionOptionService: DecisionOptionService

    val username = "test@user.com"
    var user: User? = null
    var decisionId: Long = 0

    @BeforeAll
    internal fun initEach() {

        if (userRepository.findByUsername(username) == null)
            createTestUser(username)
        else {
            decisionService.deleteAllDecisions(username)

            createTestUser(username)
        }

        user = userRepository.findByUsername(username)!!

        decisionId = decisionService.getDecisionsByUser(username).toList()[0].id!!
    }

    @Test
    fun calculatesDecisionOptionScore() {

        weightCriteria()

		rateOptions()

		decisionOptionService.calculateDecisionOptionsScore(username, decisionId)

		val decisionOptions = decisionOptionService
				.getDecisionOptions(username, decisionId)

		decisionOptions.forEach { decisionOptionLocal ->

			if(decisionOptionLocal.name == "House 1")
				assertTrue("right score for House 1", decisionOptionLocal.score.toInt() == 10)

			if(decisionOptionLocal.name == "House 2")
				assertTrue("right score for House 2", decisionOptionLocal.score.toInt() == 5)

			if(decisionOptionLocal.name == "House 3")
				assertTrue("right score for House 3", decisionOptionLocal.score.toInt() == 0)

		}
    }

    fun createTestUser(username: String) {
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

    fun weightCriteria() {
        val weightedCriteria = weightedCriteriaService
				.getWeightedCriteria(username, decisionId)

        weightedCriteria!!.forEach { weightedCriteriaLocal ->

            var score = 0

            val selectionCriteria1 = selectionCriteriaService
					.getSelectionCriteriaById(username, decisionId, weightedCriteriaLocal.selectionCriteria1Id)
            val selectionCriteria2 = selectionCriteriaService
					.getSelectionCriteriaById(username, decisionId, weightedCriteriaLocal.selectionCriteria2Id)

            if (selectionCriteria1!!.name == "Size" && selectionCriteria2!!.name == "Garden")
                score = -100


            if (selectionCriteria1.name == "Size" && selectionCriteria2!!.name == "Kitchen")
                score = -100


            if (selectionCriteria1.name == "Size" && selectionCriteria2!!.name == "Neighborhood")
                score = -100



            if (selectionCriteria1.name == "Neighborhood" && selectionCriteria2!!.name == "Garden")
                score = -50


            if (selectionCriteria1.name == "Neighborhood" && selectionCriteria2!!.name == "Kitchen")
                score = -50



            if (selectionCriteria1.name == "Kitchen" && selectionCriteria2!!.name == "Garden")
                score = 25


            weightedCriteriaLocal.weight = score
            weightedCriteriaService.saveWeightedCriteria(username, decisionId, weightedCriteriaLocal)
        }

    }

	fun rateOptions() {
		val ratedOptions = ratedOptionService
				.getRatedOptions(username, decisionId)

		ratedOptions!!.forEach { ratedOptionLocal ->

			var score = 0

			val decisionOption = decisionOptionService
					.getDecisionOptionsById(username, decisionId, ratedOptionLocal.decisionOptionId)
			val selectionCriteria = selectionCriteriaService
					.getSelectionCriteriaById(username, decisionId, ratedOptionLocal.selectionCriteriaId)

			if (selectionCriteria!!.name == "Size")
				when (decisionOption!!.name) {
					"House 1" -> score = 100
					"House 2" -> score = 50
					"House 3" -> score = 0
				}

			if (selectionCriteria.name == "Neighborhood")
				when (decisionOption!!.name) {
					"House 1" -> score = 100
					"House 2" -> score = 50
					"House 3" -> score = 0
				}

			if (selectionCriteria.name == "Kitchen")
				when (decisionOption!!.name) {
					"House 1" -> score = 100
					"House 2" -> score = 50
					"House 3" -> score = 0
				}

			if (selectionCriteria.name == "Garden")
				when (decisionOption!!.name) {
					"House 1" -> score = 100
					"House 2" -> score = 50
					"House 3" -> score = 0
				}

			ratedOptionLocal.score = score
			ratedOptionService.saveRatedOption(username, decisionId, ratedOptionLocal)
		}

	}

}

