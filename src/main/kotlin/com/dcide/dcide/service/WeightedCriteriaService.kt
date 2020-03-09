package com.dcide.dcide.service


import com.dcide.dcide.model.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class WeightedCriteriaService(private val weightedCriteriaRepository: WeightedCriteriaRepository) {

    @Autowired
    lateinit var decisionService: DecisionService

    @Autowired
    lateinit var selectionCriteriaService: SelectionCriteriaService

    fun createWeightedCriteria(username: String, decisionId: Long) {

        //Authenticate decision
        val decision = decisionService.getDecisionById(username, decisionId) ?: return


        //get old weighted criteria
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

                //Get old values
                val filteredCriteria = weightedCriteriaListOld.filter {
                    it.selectionCriteria1Id == selectionCriteriaList[i].id &&
                            it.selectionCriteria2Id == selectionCriteriaList[j].id
                }


                if (filteredCriteria.count() > 0) {
                    id = filteredCriteria[0].id
                    weight = filteredCriteria[0].weight
                }

                //Create new weighted criteria
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

        //Update Data
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


    fun saveWeightedCriteria(username: String, decisionId: Long, weightedCriteria: WeightedCriteria) {

        val weightedCriteriaLocal = getWeightedCriteriaById(username, decisionId, weightedCriteria.id)

        if (weightedCriteriaLocal != null ) {
            weightedCriteriaLocal.weight = weightedCriteria.weight
            weightedCriteriaRepository.save(weightedCriteriaLocal)
        }

    }

    fun deleteWeightedCriteriaOrphans(username: String, decisionId: Long, selectionCriteriaId: Long) {

        //Authenticate decision
        val decision = decisionService.getDecisionById(username, decisionId) ?: return

        val weightedCriteria = weightedCriteriaRepository.findAll().filter {
            it.decision?.id == decision.id &&
                    (it.selectionCriteria1Id == selectionCriteriaId ||
                            it.selectionCriteria2Id == selectionCriteriaId)
        }

        weightedCriteriaRepository.deleteAll(weightedCriteria)

    }


}