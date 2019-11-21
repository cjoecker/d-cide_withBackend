package com.dcide.dcide.security


import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.validation.BindingResult

@Service
class MapValidationErrorService {

    fun MapValidationService(result: BindingResult): ResponseEntity<Any>? {
        if(result.hasErrors()){

            val errorMap = mutableMapOf<String,String?>()

            for (error in result.fieldErrors) {
                errorMap[error.field] = error.defaultMessage
            }

            return ResponseEntity(errorMap, HttpStatus.BAD_REQUEST)
        }

        return null

    }

}