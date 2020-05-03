package com.dcide.dcide.control



import com.dcide.dcide.model.WeightedCriteria
import com.dcide.dcide.model.WeightedCriteriaRepository
import com.dcide.dcide.service.WeightedCriteriaService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal
import javax.validation.Valid


@RestController
@RequestMapping("/api/decisions/{decisionId}/weightedCriteria")
internal class WeightedCriteriaController(private val weightedCriteriaRepository: WeightedCriteriaRepository) {


    @Autowired
    lateinit var weightedCriteriaService: WeightedCriteriaService


    @GetMapping("")
    fun getWeightedCriteria(@PathVariable decisionId: Long, principal: Principal): ResponseEntity<*> {

        weightedCriteriaService.createWeightedCriteria(principal.name, decisionId)

        val weightedCriteria = weightedCriteriaService.getWeightedCriteria(principal.name, decisionId)

        return ResponseEntity<Any>(weightedCriteria?.shuffled(), HttpStatus.OK)
    }

    @PutMapping("/")
    fun saveAllWeightedCriteria(@PathVariable decisionId: Long, @Valid @RequestBody weightedCriteria: WeightedCriteria,
                                principal: Principal): ResponseEntity<*> {

        val newWeightedCriteria = weightedCriteriaService.saveWeightedCriteria(principal.name, decisionId, weightedCriteria)

        return if (newWeightedCriteria != null) {
            ResponseEntity<Any>(newWeightedCriteria, HttpStatus.OK)
        } else {
            ResponseEntity<Any>(null, HttpStatus.BAD_REQUEST)
        }

    }


}