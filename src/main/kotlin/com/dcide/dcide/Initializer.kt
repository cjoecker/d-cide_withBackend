package com.dcide.dcide
import com.dcide.dcide.model.DecisionOptionRepository
import com.dcide.dcide.model.SelectionCriteriaRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
internal class Initializer(private val decisionOptionRepository: DecisionOptionRepository,
                           private val selectionCriteriaRepository: SelectionCriteriaRepository) : CommandLineRunner {

    override fun run(vararg strings: String) {

    }
}