package com.dcide.dcide.service


import com.dcide.dcide.model.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class DecisionOptionService(private val decisionOptionRepository: DecisionOptionRepository) {

    @Autowired
    lateinit var decisionService: DecisionService

    @Autowired
    lateinit var selectionCriteriaService: SelectionCriteriaService

    fun getDecisionOptions(username: String, decisionId: Long): Iterable<DecisionOption> {

        val decisionOptions = decisionOptionRepository.findAll().filter {
            it.decision?.user?.username == username &&
                    it.decision?.id == decisionId
        }

        return decisionOptions.toList().sortedByDescending { it.id }
    }


    fun getDecisionOptionsById(username: String, decisionId: Long, optionId: Long): DecisionOption? {

        return decisionOptionRepository.findById(optionId).filter {
            it.decision?.user?.username == username &&
                    it.decision?.id == decisionId
        }.orElse(null)
    }

    fun saveDecisionOption(username: String, decisionId: Long, decisionOption: DecisionOption): DecisionOption? {

        //Authenticate Decision
        val decisionLocal = decisionService.getDecisionById(username, decisionId) ?: return null

        //Authenticate Decision Option
        val decisionOptionLocal = decisionOptionRepository.findById(decisionOption.id).orElse(null)

        //Save Decision Option
        decisionOption.decision = decisionLocal

        return decisionOptionRepository.save(decisionOption)

    }

    fun deleteDecisionOption(username: String, decisionId: Long, optionId:Long): Boolean {

        val decisionOptionLocal = getDecisionOptionsById(username, decisionId, optionId)

        return if (decisionOptionLocal != null){
            decisionOptionRepository.deleteById(optionId)
            true
        }else{
            false
        }
    }

    fun rateDecisionOptions(username: String, decisionId: Long){

        val decisionLocal = decisionService.getDecisionById(username, decisionId) ?: return

        //weight selection criteria first
        selectionCriteriaService.weightSelectionCriteria(username, decisionId)

        val decisionOptions = decisionLocal.decisionOption
        val weightedCriteria = decisionLocal.weightedCriteria
        val ratedOptions = decisionLocal.ratedOption
        val selectionCriteria = decisionLocal.selectionCriteria

        //Get max sum of weighted criteria
        val weightSum = weightedCriteria.sumBy { Math.abs(it.weight) }


        decisionOptions.forEach { decisionOption ->

            var score = 0.0

            ratedOptions.filter {
                it.decisionOptionId == decisionOption.id
            }.forEach { ratedOption ->
                val selectionCriteriaLocal = selectionCriteria.filter{it.id == ratedOption.selectionCriteriaId}
                score += ratedOption.score * selectionCriteriaLocal[0].score
            }

            //Round to one decimal
            score = if (weightSum == 0) 0.0 else (score / 10).toInt().toDouble() / 10

            decisionOption.score = score

            decisionOptionRepository.save(decisionOption)

        }

    }


}