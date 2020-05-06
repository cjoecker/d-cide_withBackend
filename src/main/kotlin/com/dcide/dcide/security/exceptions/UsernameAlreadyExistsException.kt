package com.dcide.dcide.security.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus



@ResponseStatus(HttpStatus.BAD_REQUEST)
class UsernameAlreadyExistsException(message: String?) : java.lang.RuntimeException(message)