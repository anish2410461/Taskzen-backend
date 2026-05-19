package org.example.todo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.todo.dto.DashboardStatsResponse;
import org.example.todo.dto.TodoRequest;
import org.example.todo.dto.TodoResponse;
import org.example.todo.model.Todo;
import org.example.todo.service.TodoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:5174", "http://localhost:5175", "https://taskzen-frontend.onrender.com" })
public class TodoController {

    private final TodoService todoService;

    private TodoResponse toResponse(Todo todo) {
        TodoResponse response = new TodoResponse();
        response.setId(todo.getId());
        response.setTask(todo.getTask());
        response.setCompleted(todo.isCompleted());
        response.setPriority(todo.getPriority());
        response.setStatus(todo.getStatus());
        response.setDueDate(todo.getDueDate());
        response.setDueDateTime(todo.getDueDateTime());
        response.setCreatedAt(todo.getCreatedAt());
        return response;
    }

    private List<TodoResponse> toResponseList(List<Todo> todos) {
        return todos.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @GetMapping
    public List<TodoResponse> getAllTodos() {
        return toResponseList(todoService.getAllTodos());
    }

    @GetMapping("/completed")
    public List<TodoResponse> getCompletedTodos() {
        return toResponseList(todoService.getCompletedTodos());
    }

    @GetMapping("/pending")
    public List<TodoResponse> getPendingTodos() {
        return toResponseList(todoService.getPendingTodos());
    }

    @GetMapping("/search")
    public List<TodoResponse> searchTodos(@RequestParam String keyword) {
        return toResponseList(todoService.searchTodos(keyword));
    }

    @GetMapping("/priority/{priority}")
    public List<TodoResponse> getTodosByPriority(@PathVariable String priority) {
        return toResponseList(todoService.getTodosByPriority(priority));
    }

    @GetMapping("/stats")
    public DashboardStatsResponse getStats() {
        return todoService.getDashboardStats();
    }

    @PostMapping
    public ResponseEntity<TodoResponse> addTodo(@Valid @RequestBody TodoRequest request) {
        return ResponseEntity.ok(toResponse(todoService.addTodo(request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable String id) {
        todoService.deleteTodo(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<TodoResponse> updateTodo(
            @PathVariable String id,
            @Valid @RequestBody TodoRequest request) {
        return ResponseEntity.ok(toResponse(todoService.updateTodo(id, request)));
    }

    @PutMapping("/toggle/{id}")
    public ResponseEntity<TodoResponse> toggleComplete(@PathVariable String id) {
        return ResponseEntity.ok(toResponse(todoService.toggleComplete(id)));
    }

    @GetMapping("/status/{status}")
    public List<TodoResponse> getByStatus(@PathVariable String status) {
        return toResponseList(todoService.getByStatus(status));
    }
}
