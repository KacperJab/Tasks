package com.example.demo.mvc.dto.`in`

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class UpdateTaskDTO(
    @field: NotBlank(message = "Title cannot be blank")
    val title: String,

    val description: String?,

    @field: NotNull(message = "Title is mandatory")
    val important: Boolean,

    @field: NotNull(message = "Title is mandatory")
    val done: Boolean
)
