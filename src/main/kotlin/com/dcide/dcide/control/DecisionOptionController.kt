package com.dcide.dcide.control


import com.dcide.dcide.model.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull


import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

import javax.validation.Valid
import java.net.URISyntaxException


import org.springframework.web.bind.annotation.GetMapping
import java.lang.Math.abs
import java.security.Principal


@RestController
@RequestMapping("/api")
@CrossOrigin
internal class DecisionOptionController(private val decisionOptionRepository: DecisionOptionRepository) {


    @Autowired
    lateinit var projectRepository: ProjectRepository

    @Autowired
    lateinit var weightedCriteriaRepository: WeightedCriteriaRepository

    @Autowired
    lateinit var ratedOptionRepository: RatedOptionRepository

    @Autowired
    lateinit var selectionCriteriaRepository: SelectionCriteriaRepository

    @Autowired
    lateinit var selectionCriteriaController: SelectionCriteriaController


    @GetMapping("/every_decisionOption/{project_id}")
    fun everyDecisionOption(@PathVariable project_id : Long , principal : Principal): ResponseEntity<*>  {

        var result: MutableSet<DecisionOption>? = null

        val project = projectRepository.findByIdOrNull(project_id)

        if (project != null && project.user!!.username == principal.name) {
            result = project.decisionOption.sortedByDescending{ it.id }.toMutableSet()
        }

        return if (result != null) {
            ResponseEntity<Any>(result, HttpStatus.OK)
        } else {
            ResponseEntity<Any>(null, HttpStatus.NOT_FOUND)
        }
    }

    @GetMapping("/decisionOption/{option_id}")
    fun getDecisionOption(@PathVariable option_id: Long, principal : Principal): ResponseEntity<*> {

        val decisionOption = decisionOptionRepository.findById(option_id).filter{
            it.project!!.user!!.username == principal.name
        }

        return decisionOption.map { response -> ResponseEntity.ok().body(response) }
                .orElse(ResponseEntity(HttpStatus.NOT_FOUND))
    }


    @PostMapping("/decisionOption/{project_id}")
    fun createDecisionOption(@PathVariable project_id: Long, @Valid @RequestBody decisionOption: DecisionOption,
                             principal: Principal): ResponseEntity<*> {

        var result: DecisionOption? = null

        val project = projectRepository.findByIdOrNull(project_id)

        if (project != null && project.user!!.username == principal.name) {
            decisionOption.project = project
            result = decisionOptionRepository.save(decisionOption)
        }
        return if (result != null) {
            ResponseEntity<Any>(result, HttpStatus.CREATED)
        } else {
            ResponseEntity<Any>(null, HttpStatus.BAD_REQUEST)
        }

    }

    @DeleteMapping("/decisionOption/{option_id}")
    fun deleteDecisionOption(@PathVariable option_id: Long, principal : Principal): ResponseEntity<*> {

        val decisionOption = decisionOptionRepository.findById(option_id).filter{
            it.project!!.user!!.username == principal.name
        }.orElse(null)

        return if(decisionOption != null){
            decisionOptionRepository.deleteById(option_id)
            ResponseEntity.ok().build<Any>()
        }else{
            ResponseEntity.notFound().build<Any>()
        }

    }

    @GetMapping("/result/decisionOption/{project_id}")
    fun decisionOptionSorted(@PathVariable project_id: Long, principal: Principal): Collection<DecisionOption> {

        //Sum weightedCriteria if it's not already summed
        selectionCriteriaController.weightCriteria(project_id,principal)

        //Get total sum
        val weightSum =  weightedCriteriaRepository.findAll().filter {
            it.selectedCriteria.project!!.id == project_id &&
                    it.selectedCriteria.project!!.user!!.username == principal.name
        }.sumBy {abs(it.weight)}


        val decisionOptionRepositoryFiltered =  decisionOptionRepository.findAll().filter {
                    it.project!!.user!!.username == principal.name &&
                            it.project!!.id == project_id
        }

        val ratedOptionRepositoryFiltered = ratedOptionRepository.findAll().filter {
            it.decisionOption.project!!.user!!.username == principal.name &&
                    it.selectionCriteria.project!!.user!!.username == principal.name &&

                    it.decisionOption.project!!.id == project_id &&
                    it.selectionCriteria.project!!.id == project_id
        }




        decisionOptionRepositoryFiltered.forEach { decisionOption ->

            var score = 0.0

            ratedOptionRepositoryFiltered.filter { it.decisionOption.id == decisionOption.id
            }.forEach { ratedOption ->
                val selectionCriteria = selectionCriteriaRepository.findById(ratedOption.selectionCriteria.id).get()
                score += ratedOption.rating * selectionCriteria.score
            }

            //Round to one decimal
            score = if(weightSum == 0) 0.0 else (score/10).toInt().toDouble()/10

            decisionOption.score = score

            decisionOptionRepository.save(decisionOption)

        }

        return decisionOptionRepository.findAll().filter{
            it.project!!.user!!.username == principal.name &&
            it.project!!.id == project_id
        }.sortedWith(compareBy({ it.score }, { it.name})).reversed()
    }

}