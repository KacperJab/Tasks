package com.example.demo.mvc.dto.`in`

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class CreateTaskDTO(
    @field: NotBlank(message = "Title cannot be blank")
    val title: String,

    val description: String?,

    @field: NotNull(message = "Importance is mandatory")
    val important: Boolean,

    @field: NotNull(message = "Status is mandatory")
    val done: Boolean
)
