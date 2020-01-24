package com.dcide.dcide.service


import com.dcide.dcide.model.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service



@Service
class WeightedCriteriaService(private val weightedCriteriaRepository: WeightedCriteriaRepository) {

    @Autowired
    lateinit var decisionService: DecisionService

    @Autowired
    lateinit var selectionCriteriaService: SelectionCriteriaService

    fun createWeightedCriteria (username: String, decisionId: Long){

        //Authenticate decision
        val decision = decisionService.getDecisionById(username, decisionId) ?: return


        //get old weighted criteria
        val weightedCriteriaOld = weightedCriteriaRepository.findAll().filter {
            it.decision!!.id == decision.id
        }

        //create new selection criteria
        val weightedCriteriaNew: MutableList<WeightedCriteria> = mutableListOf()

        val selectionCriteriaList = selectionCriteriaService.getSelectionCriteria(username, decisionId).toList()

        val selectionCriteriaNum = selectionCriteriaList.count() - 1

        for (i in 0..selectionCriteriaNum) {

            for (j in i + 1..selectionCriteriaNum) {


                var id: Long = 0
                var weight = 0
                var selectedCriteriaId: Long = selectionCriteriaList[i].id

                //Get old values
                val filteredCriteria = weightedCriteriaOld.filter {
                    it.selectionCriteria1Id == selectionCriteriaList[i].id &&
                            it.selectionCriteria2Id == selectionCriteriaList[j].id
                }


                if (filteredCriteria.count() > 0) {
                    id = filteredCriteria[0].id
                    weight = filteredCriteria[0].weight
                    selectedCriteriaId = filteredCriteria[0].selectedCriteriaId
                }

                //Create new weighted criteria
                val weightedCriteria = WeightedCriteria(
                        id,
                        weight,
                        selectionCriteriaList[i].id,
                        selectionCriteriaList[j].id,
                        selectedCriteriaId,
                        decision
                )

                weightedCriteriaNew.add(weightedCriteria)
            }
        }

        //Update Data
        weightedCriteriaRepository.saveAll(weightedCriteriaNew)

    }

    fun getWeightedCriteria (username: String, decisionId: Long): Iterable<WeightedCriteria>? {

        //Authenticate decision
        val decision = decisionService.getDecisionById(username, decisionId) ?: return null

        return weightedCriteriaRepository.findAll().filter {
            it.decision!!.id == decision.id
        }

    }

    fun deleteWeightedCriteriaOrphans (username: String, decisionId: Long, selectionCriteriaId: Long) {

        //Authenticate decision
        val decision = decisionService.getDecisionById(username, decisionId)

        val weightedCriteria =  weightedCriteriaRepository.findAll().filter {
            it.decision!!.id == decisionId &&
                    (it.selectionCriteria1Id == selectionCriteriaId ||
                            it.selectionCriteria2Id == selectionCriteriaId)
        }

        weightedCriteriaRepository.deleteAll(weightedCriteria)

    }


}