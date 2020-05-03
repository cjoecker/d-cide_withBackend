package com.dcide.dcide.control


import com.dcide.dcide.model.SelectionCriteria
import com.dcide.dcide.model.SelectionCriteriaRepository
import com.dcide.dcide.service.SelectionCriteriaService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal
import javax.validation.Valid


@RestController
@RequestMapping("/api/decisions/{decisionId}/selectionCriteria")
internal class SelectionCriteriaController(private val selectionCriteriaRepository: SelectionCriteriaRepository) {

    @Autowired
    lateinit var selectionCriteriaService: SelectionCriteriaService


    @GetMapping("")
    fun everySelectionCriteria(@PathVariable decisionId: Long, @RequestParam(required = false) calculatedScore: Boolean, principal: Principal): ResponseEntity<*> {

        if (calculatedScore) selectionCriteriaService.weightSelectionCriteria(principal.name, decisionId)

        val criteria = selectionCriteriaService.getSelectionCriteria(principal.name, decisionId)

        return if (calculatedScore)
            ResponseEntity<Any>(criteria.sortedWith(compareBy ( {it.score }, { it.name })).reversed(), HttpStatus.OK)
        else
            ResponseEntity<Any>(criteria.sortedWith(compareBy ( {it.id }, { it.name })).reversed(), HttpStatus.OK)
    }

    @GetMapping("/{optionId}")
    fun getSelectionCriteria(@PathVariable decisionId: Long, @PathVariable optionId: Long, principal: Principal): ResponseEntity<*> {

        return ResponseEntity<Any>(selectionCriteriaService.getSelectionCriteriaById(principal.name, decisionId, optionId),
                HttpStatus.OK)

    }


    @PostMapping("/")
    fun createSelectionCriteria(@PathVariable decisionId: Long, @Valid @RequestBody selectionCriteria: SelectionCriteria,
                                principal: Principal): ResponseEntity<*> {

        val decision = selectionCriteriaService.saveSelectionCriteria(principal.name, decisionId, selectionCriteria)

        return if (decision != null) {
            ResponseEntity<Any>(decision, HttpStatus.CREATED)
        } else {
            ResponseEntity<Any>(null, HttpStatus.BAD_REQUEST)
        }

    }

    @PutMapping("/")
    fun editSelectionCriteria(@PathVariable decisionId: Long, @Valid @RequestBody selectionCriteria: SelectionCriteria,
                              principal: Principal): ResponseEntity<*> {

        val decision = selectionCriteriaService.saveSelectionCriteria(principal.name, decisionId, selectionCriteria)

        return if (decision != null) {
            ResponseEntity<Any>(decision, HttpStatus.OK)
        } else {
            ResponseEntity<Any>(null, HttpStatus.BAD_REQUEST)
        }

    }

    @DeleteMapping("/{optionId}")
    fun deleteSelectionCriteria(@PathVariable decisionId: Long, @PathVariable optionId: Long,
                                principal: Principal): ResponseEntity<*> {

        return if (selectionCriteriaService.deleteSelectionCriteria(principal.name, decisionId, optionId)) {
            ResponseEntity<Any>(null, HttpStatus.OK)
        } else {
            ResponseEntity<Any>(null, HttpStatus.NOT_FOUND)
        }

    }

}