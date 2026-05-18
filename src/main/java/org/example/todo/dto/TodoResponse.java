package org.example.todo.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TodoResponse {

    private String id;

    private String task;

    private boolean completed;

    private String priority;

    private String status;

    private LocalDate dueDate;

    private LocalDateTime dueDateTime;

    private LocalDateTime createdAt;
}
