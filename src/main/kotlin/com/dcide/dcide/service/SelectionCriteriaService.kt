package com.dcide.dcide.service


import com.dcide.dcide.model.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class SelectionCriteriaService(private val selectionCriteriaRepository: SelectionCriteriaRepository) {

    @Autowired
    lateinit var decisionService: DecisionService

    @Autowired
    lateinit var weightedCriteriaService: WeightedCriteriaService

    fun getSelectionCriteria(username: String, decisionId: Long): Iterable<SelectionCriteria> {

        val selectionCriteria = selectionCriteriaRepository.findAll().filter {
            it.decision?.user?.username == username &&
                    it.decision?.id == decisionId
        }

        return selectionCriteria.toList().sortedByDescending { it.id }
    }


    fun getSelectionCriteriaById(username: String, decisionId: Long, optionId: Long): SelectionCriteria? {

        return selectionCriteriaRepository.findById(optionId).filter {
            it.decision?.user?.username == username &&
                    it.decision?.id == decisionId
        }.orElse(null)
    }

    fun saveSelectionCriteria(username: String, decisionId: Long, selectionCriteria: SelectionCriteria): SelectionCriteria? {

        //Authenticate Decision
        val decisionLocal = decisionService.getDecisionById(username, decisionId) ?: return null

        //Authenticate Decision Option
        val selectionCriteriaLocal = selectionCriteriaRepository.findById(selectionCriteria.id).orElse(null)

        //Save Decision Option
        selectionCriteria.decision = decisionLocal

        return selectionCriteriaRepository.save(selectionCriteria)

    }

    fun deleteSelectionCriteria(username: String, decisionId: Long, optionId:Long): Boolean {

        val selectionCriteriaLocal = getSelectionCriteriaById(username, decisionId, optionId)

        weightedCriteriaService.deleteWeightedCriteriaOrphans(username, decisionId, optionId)

        return if (selectionCriteriaLocal != null){
            selectionCriteriaRepository.deleteById(optionId)
            true
        }else{
            false
        }


    }


}