package com.dcide.dcide.control


import com.dcide.dcide.model.*
import com.dcide.dcide.security.JwtTokenProvider


import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

import javax.validation.Valid
import java.net.URI
import java.net.URISyntaxException


import org.springframework.web.bind.annotation.GetMapping
import java.security.Principal
import com.dcide.dcide.security.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import java.util.stream.Stream


@RestController
@RequestMapping("/api")
@CrossOrigin
internal class ProjectController(private val projectRepository: ProjectRepository) {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var decisionOptionRepository: DecisionOptionRepository

    @Autowired
    lateinit var selectionCriteriaRepository: SelectionCriteriaRepository

    @Autowired
    lateinit var weightedCriteriaRepository: WeightedCriteriaRepository

    @GetMapping("/every_project")
    fun everyProject(principal: Principal): Iterable<Project> {

        val projects = projectRepository.findAll().filter {
            it.user!!.username == principal.name
        }

        return projects.toList().sortedByDescending { it.id }
    }


    @GetMapping("/project/{id}")
    fun getProject(@PathVariable id: Long, principal: Principal): ResponseEntity<*> {

        val project = projectRepository.findById(id).filter {
            it.user!!.username == principal.name
        }


        return project.map { response -> ResponseEntity.ok().body(response) }
                .orElse(ResponseEntity(HttpStatus.NOT_FOUND))
    }


    @PostMapping("/project")
    @Throws(URISyntaxException::class)
    fun createProject(@Valid @RequestBody project: Project, principal: Principal): ResponseEntity<*> {

        var searchedProject: Project? = null
        var result: Project? = null

        //Search if project exist
        if (project.id != null)
            searchedProject = projectRepository.findByIdOrNull(project.id)

        //Project not found or project found for username
        if (searchedProject == null || searchedProject.user!!.username == principal.name) {
            val user = userRepository.findByUsername(principal.name)
            project.user = user
            result = projectRepository.save(project)
        }

        return if (result != null) {
            ResponseEntity<Any>(result, HttpStatus.CREATED)
        } else {
            ResponseEntity<Any>(null, HttpStatus.NOT_FOUND)
        }
    }


    @DeleteMapping("/project/{id}")
    fun deleteProject(@PathVariable id: Long, principal: Principal): ResponseEntity<*> {

        val project = projectRepository.findById(id).filter {
            it.user!!.username == principal.name
        }.orElse(null)

        return if (project != null) {

            //Delete Selection Criteria
            val weightedCriterias = weightedCriteriaRepository.findAll().filter {
                it.selectedCriteria.project!!.id == id &&
                        it.selectionCriteria1.project!!.id == id &&
                        it.selectionCriteria2.project!!.id == id
            }

            weightedCriteriaRepository.deleteAll(weightedCriterias)

            //Delete Project
            projectRepository.deleteById(id)
            ResponseEntity.ok().build<Any>()
        } else {
            ResponseEntity.notFound().build<Any>()
        }

    }

    @PostMapping("/createExampleData")
    fun createExampleData(principal: Principal): ResponseEntity<*> {

        val projects = projectRepository.findAll().filter {
            it.user!!.username == principal.name
        }

        if (projects.count() == 1) {

            //Create test decision options
            Stream.of("House in pleasant street", "House in seldom seen avenue", "House in yellowsnow road").forEach { decisionOption ->
                decisionOptionRepository.save(
                        DecisionOption(0, decisionOption, 0.0, projects[0])
                )
            }


            //Create test selection criteria
            Stream.of("Garden", "Kitchen", "Neighborhood", "Size").forEach { selectionCriteria ->
                selectionCriteriaRepository.save(
                        SelectionCriteria(0, selectionCriteria, 0.0, projects[0])
                )
            }

            return ResponseEntity<Any>(null, HttpStatus.CREATED)
        } else {
            return ResponseEntity<Any>(null, HttpStatus.METHOD_NOT_ALLOWED)
        }

    }

    @PutMapping("/transferProjectToUser")
    fun transferProject(@RequestBody username: String, principal: Principal): ResponseEntity<*> {

        var result: Project? = null

        //get unregistered user from token

        val project = projectRepository.findAll().filter {
            it.user!!.username == username
        }

        if (project.count() == 1) {
            val user = userRepository.findByUsername(principal.name)

            project[0].user = user

            result = projectRepository.save(project[0])
        }

        return if (result != null) {
            ResponseEntity<Any>(result, HttpStatus.CREATED)
        } else {
            ResponseEntity<Any>(null, HttpStatus.NOT_FOUND)
        }

    }


}