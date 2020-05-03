package com.dcide.dcide

import com.dcide.dcide.model.Decision
import com.dcide.dcide.model.User
import com.dcide.dcide.model.UserRepository
import com.dcide.dcide.service.*
import org.junit.Assert.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.util.*


@SpringBootTest
@Transactional
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
        }

        user = userRepository.findByUsername(username)

        userService.createDecisionForUnregisteredUser(user!!)

        decisionId = decisionService.getDecisionsByUser(username).toList()[0].id!!
    }

    @Test
    fun calculatesDecisionOptionScore() {

        val decision = decisionService.getDecisionById(username, decisionId)

        weightCriteria()

        rateOptions(decision!!)

        decisionOptionService.calculateDecisionOptionsScore(username, decisionId)

        val decisionOptions = decisionOptionService
                .getDecisionOptions(username, decisionId)

        decisionOptions.forEach { decisionOptionLocal ->

            if (decisionOptionLocal.name == "House 1")
                assertTrue("right score for House 1", decisionOptionLocal.score == 10.0)

            if (decisionOptionLocal.name == "House 2")
                assertTrue("right score for House 2", decisionOptionLocal.score == 5.0)

            if (decisionOptionLocal.name == "House 3")
                assertTrue("right score for House 3", decisionOptionLocal.score == 0.0)

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

    }

    fun weightCriteria() {

        weightedCriteriaService.createWeightedCriteria(username, decisionId)

        val weightedCriteria = weightedCriteriaService
                .getWeightedCriteria(username, decisionId)

        weightedCriteria!!.forEach { weightedCriteriaLocal ->

            val selectionCriteria1 = selectionCriteriaService
                    .getSelectionCriteriaById(username, decisionId, weightedCriteriaLocal.selectionCriteria1Id)
            val selectionCriteria2 = selectionCriteriaService
                    .getSelectionCriteriaById(username, decisionId, weightedCriteriaLocal.selectionCriteria2Id)

            weightedCriteriaLocal.weight = getWeightedCriteriaWeights(selectionCriteria1!!.name, selectionCriteria2!!.name)

            weightedCriteriaService.saveWeightedCriteria(username, decisionId, weightedCriteriaLocal)
        }

    }

    fun getWeightedCriteriaWeights(selectionCriteria1: String, selectionCriteria2: String): Int {
        if (selectionCriteria1 == "Size" && selectionCriteria2 == "Garden")
            return -100

        if (selectionCriteria1 == "Size" && selectionCriteria2 == "Kitchen")
            return -100

        if (selectionCriteria1 == "Size" && selectionCriteria2 == "Neighborhood")
            return -100

        if (selectionCriteria1 == "Neighborhood" && selectionCriteria2 == "Garden")
            return -50

        if (selectionCriteria1 == "Neighborhood" && selectionCriteria2 == "Kitchen")
            return -50

        if (selectionCriteria1 == "Kitchen" && selectionCriteria2 == "Garden")
            return 25

        return 0
    }

    fun rateOptions(decision : Decision) {

        val ratedOptions = ratedOptionService.getRatedOptions(username, decisionId)

        ratedOptions!!.forEach{ratedOptionLocal ->

            val decisionOptionLocal = decisionOptionService
                    .getDecisionOptionsById(username, decisionId,ratedOptionLocal.decisionOptionId)
            val selectionCriteriaLocal = selectionCriteriaService
                    .getSelectionCriteriaById(username, decisionId, ratedOptionLocal.selectionCriteriaId)

            ratedOptionService.saveRatedOption(username, decisionId, ratedOptionLocal
                    .copy(score = getRatedOptionsScore(selectionCriteriaLocal!!.name, decisionOptionLocal!!.name)))

        }
    }
    
    fun getRatedOptionsScore(selectionCriteriaName :String, decisionOptionName : String) : Int{
        if (selectionCriteriaName == "Size")
            when (decisionOptionName) {
                "House 1" -> return 100
                "House 2" -> return 50
                "House 3" -> return 0
            }

        if (selectionCriteriaName == "Neighborhood")
            when (decisionOptionName) {
                "House 1" -> return 100
                "House 2" -> return 50
                "House 3" -> return 0
            }

        if (selectionCriteriaName == "Kitchen")
            when (decisionOptionName) {
                "House 1" -> return 100
                "House 2" -> return 50
                "House 3" -> return 0
            }

        if (selectionCriteriaName == "Garden")
            when (decisionOptionName) {
                "House 1" -> return 100
                "House 2" -> return 50
                "House 3" -> return 0
            }

        return 0
    }
}

