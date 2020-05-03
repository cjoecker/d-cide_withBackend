package com.dcide.dcide.service


import com.dcide.dcide.model.WeightedCriteria
import com.dcide.dcide.model.WeightedCriteriaRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class WeightedCriteriaService(private val weightedCriteriaRepository: WeightedCriteriaRepository) {

    @Autowired
    lateinit var decisionService: DecisionService

    @Autowired
    lateinit var selectionCriteriaService: SelectionCriteriaService

    fun createWeightedCriteria(username: String, decisionId: Long) {

        val decision = decisionService.getDecisionById(username, decisionId) ?: return

        val weightedCriteriaListOld = weightedCriteriaRepository.findAll().filter {
            it.decision?.id == decision.id
        }

        val weightedCriteriaListNew: MutableList<WeightedCriteria> = mutableListOf()

        val selectionCriteriaList = selectionCriteriaService.getSelectionCriteria(username, decisionId).toList()

        val selectionCriteriaNum = selectionCriteriaList.count() - 1

        for (i in 0..selectionCriteriaNum) {

            for (j in i + 1..selectionCriteriaNum) {


                var id: Long = 0
                var weight = 0


                val filteredCriteria = weightedCriteriaListOld.filter {
                    it.selectionCriteria1Id == selectionCriteriaList[i].id &&
                            it.selectionCriteria2Id == selectionCriteriaList[j].id
                }


                if (filteredCriteria.count() > 0) {
                    id = filteredCriteria[0].id
                    weight = filteredCriteria[0].weight
                }

                val weightedCriteria = WeightedCriteria(
                        id,
                        weight,
                        selectionCriteriaList[i].id,
                        selectionCriteriaList[j].id,
                        decision
                )

                weightedCriteriaListNew.add(weightedCriteria)
            }
        }

        weightedCriteriaRepository.saveAll(weightedCriteriaListNew)

    }

    fun getWeightedCriteria(username: String, decisionId: Long): Iterable<WeightedCriteria>? {

        return weightedCriteriaRepository.findAll().filter {
            it.decision?.id == decisionId &&
                    it.decision?.user?.username == username
        }

    }


    fun getWeightedCriteriaById(username: String, decisionId: Long, criteriaId: Long): WeightedCriteria? {

        return weightedCriteriaRepository.findById(criteriaId).filter {
            it.decision?.user?.username == username &&
                    it.decision?.id == decisionId
        }.orElse(null)
    }


    fun saveWeightedCriteria(username: String, decisionId: Long, weightedCriteria: WeightedCriteria): WeightedCriteria? {

        val weightedCriteriaLocal = getWeightedCriteriaById(username, decisionId, weightedCriteria.id)

        if (weightedCriteriaLocal != null ) {
            weightedCriteriaLocal.weight = weightedCriteria.weight
            return weightedCriteriaRepository.save(weightedCriteriaLocal)
        }

        return null
    }

    fun deleteWeightedCriteriaOrphans(username: String, decisionId: Long, selectionCriteriaId: Long) {

        val decision = decisionService.getDecisionById(username, decisionId) ?: return

        val weightedCriteria = weightedCriteriaRepository.findAll().filter {
            it.decision?.id == decision.id &&
                    (it.selectionCriteria1Id == selectionCriteriaId ||
                            it.selectionCriteria2Id == selectionCriteriaId)
        }

        weightedCriteriaRepository.deleteAll(weightedCriteria)

    }


}