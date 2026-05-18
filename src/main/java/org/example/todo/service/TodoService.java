package org.example.todo.service;

import org.example.todo.dto.DashboardStatsResponse;
import org.example.todo.dto.TodoRequest;
import org.example.todo.model.Todo;

import java.util.List;

public interface TodoService {

    List<Todo> getAllTodos();

    Todo addTodo(TodoRequest request);

    void deleteTodo(String id);

    Todo toggleComplete(String id);

    Todo updateTodo(String id, TodoRequest request);

    List<Todo> getCompletedTodos();

    List<Todo> getPendingTodos();

    List<Todo> searchTodos(String keyword);

    List<Todo> getTodosByPriority(String priority);

    List<Todo> getByStatus(String status);

    DashboardStatsResponse getDashboardStats();
}
