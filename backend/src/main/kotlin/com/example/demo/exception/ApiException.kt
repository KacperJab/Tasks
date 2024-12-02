package com.example.demo.exception

import org.springframework.http.HttpStatus
import java.time.Instant

data class ApiException(
    val message: String?,
    val status: HttpStatus,
    val timestamp: Instant = Instant.now()
)
