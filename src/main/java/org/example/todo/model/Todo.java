package org.example.todo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "todos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Todo {

    @Id
    private String id;

    private String userEmail;

    private String task;

    private boolean completed = false;

    private String priority;

    private String status = "TODO";

    private LocalDate dueDate;

    private LocalDateTime dueDateTime;

    private LocalDateTime createdAt;
}
