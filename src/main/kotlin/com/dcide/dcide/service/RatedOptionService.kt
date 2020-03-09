package com.dcide.dcide.service


import com.dcide.dcide.model.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class RatedOptionService(private val ratedOptionRepository: RatedOptionRepository) {

    @Autowired
    lateinit var decisionService: DecisionService

    @Autowired
    lateinit var selectionCriteriaService: SelectionCriteriaService

    @Autowired
    lateinit var decisionOptionService: DecisionOptionService

    fun saveRatedOption(username: String, decisionId: Long, ratedOption: RatedOption) {

        //Authenticate decision
        val decision = decisionService.getDecisionById(username, decisionId) ?: return

        val ratedOptionLocal: RatedOption

        //Get related objects
        val selectionCriteria = selectionCriteriaService.getSelectionCriteriaById(username, decisionId, ratedOption.selectionCriteriaId)
        val decisionOption = decisionOptionService.getDecisionOptionsById(username, decisionId, ratedOption.decisionOptionId)

        //search for existing ratedOptions
        val filteredRatedOption = ratedOptionRepository.findAll().filter {
            it.selectionCriteriaId == ratedOption.selectionCriteriaId &&
                    it.decisionOptionId == ratedOption.decisionOptionId
        }

        if (selectionCriteria != null && decisionOption != null) {

            //Update ratedOption rating if found
            if (filteredRatedOption.isNotEmpty()) {
                ratedOptionLocal = filteredRatedOption[0]
                ratedOptionLocal.score = ratedOption.score
                ratedOptionLocal.decision = decision

            } else {
                //Create ratedOption if not found
                ratedOptionLocal = RatedOption(
                        0,
                        ratedOption.score,
                        decisionOption.id,
                        selectionCriteria.id,
                        decision
                )
            }

            //save ratedOption
            ratedOptionRepository.save(ratedOptionLocal)
        }
    }

    fun getRatedOptions(username: String, decisionId: Long): Iterable<RatedOption>? {

        return ratedOptionRepository.findAll().filter {
            it.decision?.id == decisionId &&
                    it.decision?.user?.username == username
        }

    }

}