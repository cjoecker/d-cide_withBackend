package com.dcide.dcide.service


import com.dcide.dcide.model.RatedOption
import com.dcide.dcide.model.RatedOptionRepository
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
    

    fun saveRatedOption(username: String, decisionId: Long, ratedOption: RatedOption): RatedOption? {

        val decision = decisionService.getDecisionById(username, decisionId) ?: return null

        val ratedOptionLocal: RatedOption

        val selectionCriteria = selectionCriteriaService.getSelectionCriteriaById(username, decisionId, ratedOption.selectionCriteriaId)
        val decisionOption = decisionOptionService.getDecisionOptionsById(username, decisionId, ratedOption.decisionOptionId)

        val filteredRatedOption = ratedOptionRepository.findAll().filter {
            it.selectionCriteriaId == ratedOption.selectionCriteriaId &&
                    it.decisionOptionId == ratedOption.decisionOptionId
        }

        if (selectionCriteria != null && decisionOption != null) {

            if (filteredRatedOption.isNotEmpty()) {
                ratedOptionLocal = filteredRatedOption[0]
                ratedOptionLocal.score = ratedOption.score
                ratedOptionLocal.decision = decision

            } else {
                ratedOptionLocal = RatedOption(
                        0,
                        ratedOption.score,
                        decisionOption.id,
                        selectionCriteria.id,
                        decision
                )
            }

            return ratedOptionRepository.save(ratedOptionLocal)
        }

        return null
    }

    fun getRatedOptions(username: String, decisionId: Long): Iterable<RatedOption>? {

        createRatedOptions(username, decisionId)

        return ratedOptionRepository.findAll().filter {
            it.decision?.id == decisionId &&
                    it.decision?.user?.username == username
        }

    }

    fun createRatedOptions(username: String, decisionId: Long) {

        val decision = decisionService.getDecisionById(username, decisionId) ?: return

        
        val oldRatedOptionsList = ratedOptionRepository.findAll().filter {
            it.decision?.id == decision.id
        }

        val ratedOptionsListNew: MutableList<RatedOption> = mutableListOf()

        val decisionOptions = decisionOptionService
                .getDecisionOptions(username, decisionId)
        val selectionCriteria = selectionCriteriaService
                .getSelectionCriteria(username, decisionId)

        selectionCriteria.forEach { selectionCriteriaLocal ->

            decisionOptions.forEach { decisionOption ->

                var id: Long = 0
                var score = 50

                val oldRatedOption = oldRatedOptionsList.filter {
                    it.selectionCriteriaId == selectionCriteriaLocal.id &&
                            it.decisionOptionId == decisionOption.id

                }
                if (oldRatedOption.count() > 0) {
                    id = oldRatedOption[0].id
                    score = oldRatedOption[0].score
                }

                val ratedOption = RatedOption(
                        id,
                        score,
                        decisionOption.id,
                        selectionCriteriaLocal.id,
                        decision
                )

                ratedOptionsListNew.add(ratedOption)

            }
        }

        ratedOptionRepository.saveAll(ratedOptionsListNew)

    }

    fun deleteRateOptionsOrphans(username: String, decisionId: Long, selectionCriteriaId: Long, decisionOptionId: Long) {

        val weightedCriteria = ratedOptionRepository.findAll().filter {
            it.decision?.id == decisionId &&
                    (it.selectionCriteriaId == selectionCriteriaId ||
                            it.decisionOptionId == decisionOptionId)
        }

        ratedOptionRepository.deleteAll(weightedCriteria)

    }


}