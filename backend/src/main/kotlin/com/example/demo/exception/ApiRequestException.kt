package com.example.demo.exception

import org.springframework.http.HttpStatus

class ApiRequestException(val status: HttpStatus, message: String) : java.lang.Exception(message)
