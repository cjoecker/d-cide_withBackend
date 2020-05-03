package com.dcide.dcide.control


import com.dcide.dcide.model.RatedOption
import com.dcide.dcide.model.RatedOptionRepository
import com.dcide.dcide.service.RatedOptionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal
import javax.validation.Valid


@RestController
@RequestMapping("/api/decisions/{decisionId}/ratedOptions")
internal class RatedOptionController(private val ratedOptionRepository: RatedOptionRepository) {

    @Autowired
    lateinit var ratedOptionService: RatedOptionService

    @GetMapping("")
    fun getRatedOptions(@PathVariable decisionId: Long, principal: Principal): ResponseEntity<*> {

        return ResponseEntity<Any>(ratedOptionService.getRatedOptions(principal.name, decisionId), HttpStatus.OK)

    }

    @PutMapping("")
    fun saveRatedOption(@PathVariable decisionId: Long, @Valid @RequestBody ratedOption: RatedOption,
                                principal: Principal): ResponseEntity<*> {

        val savedRatedOption = ratedOptionService.saveRatedOption(principal.name, decisionId, ratedOption)

        return if (savedRatedOption != null) {
            ResponseEntity<Any>(savedRatedOption, HttpStatus.OK)
        } else {
            ResponseEntity<Any>(null, HttpStatus.BAD_REQUEST)
        }
    }

}
