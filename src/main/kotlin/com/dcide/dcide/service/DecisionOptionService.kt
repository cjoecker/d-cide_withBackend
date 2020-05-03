package com.dcide.dcide.service


import com.dcide.dcide.model.DecisionOption
import com.dcide.dcide.model.DecisionOptionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import kotlin.math.min


@Service
class DecisionOptionService(private val decisionOptionRepository: DecisionOptionRepository) {

    @Autowired
    lateinit var decisionService: DecisionService

    @Autowired
    lateinit var selectionCriteriaService: SelectionCriteriaService

    @Autowired
    lateinit var ratedOptionService: RatedOptionService

    fun getDecisionOptions(username: String, decisionId: Long): Iterable<DecisionOption> {

        return decisionOptionRepository.findAll().filter {
            it.decision?.user?.username == username &&
                    it.decision?.id == decisionId
        }
    }


    fun getDecisionOptionsById(username: String, decisionId: Long, optionId: Long): DecisionOption? {

        return decisionOptionRepository.findById(optionId).filter {
            it.decision?.user?.username == username &&
                    it.decision?.id == decisionId
        }.orElse(null)
    }

    fun saveDecisionOption(username: String, decisionId: Long, decisionOption: DecisionOption): DecisionOption? {

        val decisionLocal = decisionService.getDecisionById(username, decisionId) ?: return null


        val decisionOptionLocal = decisionOptionRepository.findById(decisionOption.id).orElse(null)

        if(decisionOptionLocal != null && decisionOptionLocal.decision!!.user!!.username != username)
            return null


        decisionOption.decision = decisionLocal

        return decisionOptionRepository.save(decisionOption)

    }

    fun deleteDecisionOption(username: String, decisionId: Long, optionId:Long): Boolean {

        val decisionOptionLocal = getDecisionOptionsById(username, decisionId, optionId)

        return if (decisionOptionLocal != null){
            ratedOptionService.deleteRateOptionsOrphans(username, decisionId, 0, optionId)
            decisionOptionRepository.deleteById(optionId)
            true
        }else{
            false
        }
    }

    fun calculateDecisionOptionsScore(username: String, decisionId: Long){

        val decisionLocal = decisionService.getDecisionById(username, decisionId) ?: return

        selectionCriteriaService.weightSelectionCriteria(username, decisionId)

        val decisionOptions = decisionLocal.decisionOption
        val weightedCriteria = decisionLocal.weightedCriteria
        val ratedOptions = decisionLocal.ratedOption
        val selectionCriteria = decisionLocal.selectionCriteria

        val weightSum = weightedCriteria.sumBy { Math.abs(it.weight) }

        decisionOptions.forEach { decisionOption ->

            var score = 0.0

            ratedOptions.filter {
                it.decisionOptionId == decisionOption.id
            }.forEach { ratedOption ->
                val selectionCriteriaLocal = selectionCriteria.filter{it.id == ratedOption.selectionCriteriaId}
                score += ratedOption.score * selectionCriteriaLocal[0].score
            }

            score = if (weightSum == 0) 0.0 else min((score / 10).toInt().toDouble() / 10,10.0)

            decisionOption.score = score

            decisionOptionRepository.save(decisionOption)

        }

    }


}