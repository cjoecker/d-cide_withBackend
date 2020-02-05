package com.dcide.dcide.control


import com.dcide.dcide.model.*
import com.dcide.dcide.service.RatedOptionService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

import javax.validation.Valid
import java.security.Principal


@RestController
@RequestMapping("/api/decisions/{decisionId}/ratedOptions")
internal class RatedOptionController(private val ratedOptionRepository: RatedOptionRepository) {

    @Autowired
    lateinit var ratedOptionService: RatedOptionService

    @GetMapping("")
    fun getRatedOptions(@PathVariable decisionId: Long, principal: Principal): ResponseEntity<*> {

        return ResponseEntity<Any>(ratedOptionService.getRatedOptions(principal.name, decisionId), HttpStatus.OK)
    }

    @PutMapping("")
    fun saveAllWeightedCriteria(@PathVariable decisionId: Long, @Valid @RequestBody ratedOptionList: List<RatedOption>,
                                principal: Principal): ResponseEntity<*> {


        ratedOptionService.saveRatedOption(principal.name, decisionId, ratedOptionList)

        return ResponseEntity<Any>(null, HttpStatus.OK)

    }


//    @GetMapping("/every_ratedOption/{decision_id}")
//    fun getEveryRatedOption(@PathVariable decision_id: Long, principal: Principal): ResponseEntity<*> {
//
//
//        val ratedOptions = ratedOptionRepository.findAll().filter {
//            it.decisionOption.decision!!.user!!.username == principal.name &&
//                    it.selectionCriteria.decision!!.user!!.username == principal.name &&
//
//                    it.decisionOption.decision!!.id == decision_id &&
//                    it.selectionCriteria.decision!!.id == decision_id
//        }
//
//        return ResponseEntity<Any>(ratedOptions, HttpStatus.OK)
//
//    }
//
//    @PostMapping("/ratedOption/{criteria_id}/{option_id}/{rating}")
//    fun createRatedOption(@PathVariable criteria_id: Long,
//                          @PathVariable option_id: Long,
//                          @PathVariable rating: Int,
//                          principal: Principal): ResponseEntity<*> {
//
//        val ratedOption: RatedOption
//
//        //Get related objects
//        val selectionCriteria = selectionCriteriaRepository.findByIdOrNull(criteria_id)
//        val decisionOption = decisionOptionRepository.findByIdOrNull(option_id)
//
//        //Check if no parent objects where found
//        if (selectionCriteria == null || decisionOption == null)
//            return ResponseEntity<Any>(null, HttpStatus.BAD_REQUEST)
//
//        //Check if parent objects are from other user
//        if (selectionCriteria.decision!!.user!!.username != principal.name ||
//                decisionOption.decision!!.user!!.username != principal.name) {
//            return ResponseEntity<Any>(null, HttpStatus.BAD_REQUEST)
//
//        }
//
//        //search for existing ratedOptions
//        val filteredRatedOption = ratedOptionRepository.findAll().filter {
//            it.selectionCriteria.id == criteria_id &&
//                    it.decisionOption.id == option_id
//        }
//
//        //Update ratedOption rating if found
//        if (filteredRatedOption.isNotEmpty()) {
//            ratedOption = filteredRatedOption[0]
//            ratedOption.rating = rating
//
//        } else {
//            //Create ratedOption if not found
//            ratedOption = RatedOption(
//                    id = 0,
//                    decisionOption = decisionOption,
//                    selectionCriteria = selectionCriteria,
//                    rating = rating
//            )
//        }
//
//        //save ratedOption
//        ratedOptionRepository.save(ratedOption)
//
//        return ResponseEntity<Any>(null, HttpStatus.OK)
//
//    }
//
//    @PostMapping("/every_ratedOption")
//    fun postEveryWeightedCriteria(@Valid @RequestBody ratedOptionList: List<RatedOption>,
//                                  principal: Principal): ResponseEntity<*> {
//
//        var result = true
//
//        ratedOptionList.forEach { ratedOption ->
//
//            val ratedOptionLocal: RatedOption
//
//            //Get related objects
//            val selectionCriteria = selectionCriteriaRepository.findByIdOrNull(ratedOption.selectionCriteria.id)
//            val decisionOption = decisionOptionRepository.findByIdOrNull(ratedOption.decisionOption.id)
//
//            //Check if no parent objects where found
//            if (selectionCriteria == null || decisionOption == null)
//                return ResponseEntity<Any>(null, HttpStatus.BAD_REQUEST)
//
//            //Check if parent objects are from other user
//            if (selectionCriteria.decision!!.user!!.username != principal.name ||
//                    decisionOption.decision!!.user!!.username != principal.name) {
//                return ResponseEntity<Any>(null, HttpStatus.BAD_REQUEST)
//            }
//
//            //search for existing ratedOptions
//            val filteredRatedOption = ratedOptionRepository.findAll().filter {
//                it.selectionCriteria.id == ratedOption.selectionCriteria.id &&
//                        it.decisionOption.id == ratedOption.decisionOption.id
//            }
//
//            //Update ratedOption rating if found
//            if (filteredRatedOption.isNotEmpty()) {
//                ratedOptionLocal = filteredRatedOption[0]
//                ratedOptionLocal.rating = ratedOption.rating
//
//            } else {
//                //Create ratedOption if not found
//                ratedOptionLocal = RatedOption(
//                        id = 0,
//                        decisionOption = decisionOption,
//                        selectionCriteria = selectionCriteria,
//                        rating = ratedOption.rating
//                )
//            }
//
//            //save ratedOption
//            ratedOptionRepository.save(ratedOptionLocal)
//        }
//
//        return ResponseEntity<Any>(null, HttpStatus.CREATED)
//
//    }


}
