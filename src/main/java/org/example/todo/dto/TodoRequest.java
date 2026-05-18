package org.example.todo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TodoRequest {

    @NotBlank(message = "Task cannot be empty")
    private String task;

    private String priority;

    private LocalDate dueDate;

    private LocalDateTime dueDateTime;
}
