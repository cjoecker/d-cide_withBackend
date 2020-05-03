package com.dcide.dcide.service


import com.dcide.dcide.model.SelectionCriteria
import com.dcide.dcide.model.SelectionCriteriaRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import kotlin.math.min


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


    fun getSelectionCriteriaById(username: String, decisionId: Long, criteriaId: Long): SelectionCriteria? {

        return selectionCriteriaRepository.findById(criteriaId).filter {
            it.decision?.user?.username == username &&
                    it.decision?.id == decisionId
        }.orElse(null)
    }

    fun saveSelectionCriteria(username: String, decisionId: Long, selectionCriteria: SelectionCriteria): SelectionCriteria? {

        val decisionLocal = decisionService.getDecisionById(username, decisionId) ?: return null

        val selectionCriteriaLocal = selectionCriteriaRepository.findById(selectionCriteria.id).orElse(null)

        if(selectionCriteriaLocal != null && selectionCriteriaLocal.decision!!.user!!.username != username)
            return null

        selectionCriteria.decision = decisionLocal

        return selectionCriteriaRepository.save(selectionCriteria)

    }

    fun deleteSelectionCriteria(username: String, decisionId: Long, optionId: Long): Boolean {

        val selectionCriteria = getSelectionCriteriaById(username, decisionId, optionId)

        return if (selectionCriteria != null) {
            weightedCriteriaService.deleteWeightedCriteriaOrphans(username, decisionId, optionId)
            selectionCriteriaRepository.deleteById(optionId)
            true
        } else {
            false
        }
    }

    fun weightSelectionCriteria(username: String, decisionId: Long) {

        val decisionLocal = decisionService.getDecisionById(username, decisionId) ?: return

        val weightedCriteria = decisionLocal.weightedCriteria
        val selectionCriteria = decisionLocal.selectionCriteria

        val weightSum = weightedCriteria.sumBy { Math.abs(it.weight) }


        selectionCriteria.forEach { selectionCriteria ->

            var score = weightedCriteria.filter {
                (it.selectionCriteria1Id == selectionCriteria.id && it.weight <= 0) ||
                        (it.selectionCriteria2Id == selectionCriteria.id && it.weight > 0)
            }.sumBy { Math.abs(it.weight) }.toDouble()

            score = min( Math.round(score / weightSum * 100) / 10.0 ,10.0)

            selectionCriteriaRepository.save(selectionCriteria.copy(score = score))
        }

    }

}