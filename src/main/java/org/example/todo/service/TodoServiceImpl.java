package org.example.todo.service;

import lombok.RequiredArgsConstructor;
import org.example.todo.dto.DashboardStatsResponse;
import org.example.todo.dto.TodoRequest;
import org.example.todo.model.Todo;
import org.example.todo.repository.TodoRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;

    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private String determineStatus(Todo todo) {
        if (todo.isCompleted()) {
            return "COMPLETED";
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime due = todo.getDueDateTime();
        if (due == null) {
            return "UPCOMING";
        }
        if (due.isBefore(now)) {
            return "OVERDUE";
        }
        if (due.isBefore(now.plusHours(1))) {
            return "DUE_SOON";
        }
        return "UPCOMING";
    }


    @Override
    public Todo addTodo(TodoRequest request) {
        Todo todo = new Todo();
        todo.setTask(request.getTask());
        todo.setPriority(request.getPriority());
        todo.setDueDate(request.getDueDate());
        todo.setDueDateTime(request.getDueDateTime());
        todo.setCreatedAt(LocalDateTime.now());
        todo.setCompleted(false);
        todo.setUserEmail(getCurrentUserEmail());
        todo.setStatus(determineStatus(todo));
        return todoRepository.save(todo);
    }

    @Override
    public void deleteTodo(String id) {
        Todo todo = todoRepository.findById(id).orElseThrow();
        if (todo.getUserEmail().equals(getCurrentUserEmail())) {
            todoRepository.deleteById(id);
        }
    }

    @Override
    public Todo toggleComplete(String id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found with id: " + id));
        if (!todo.getUserEmail().equals(getCurrentUserEmail())) throw new RuntimeException("Unauthorized");
        todo.setCompleted(!todo.isCompleted());
        todo.setStatus(determineStatus(todo));
        return todoRepository.save(todo);
    }

    @Override
    public Todo updateTodo(String id, TodoRequest request) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found with id: " + id));
        if (!todo.getUserEmail().equals(getCurrentUserEmail())) throw new RuntimeException("Unauthorized");
        todo.setTask(request.getTask());
        todo.setPriority(request.getPriority());
        todo.setDueDate(request.getDueDate());
        todo.setDueDateTime(request.getDueDateTime());
        todo.setStatus(determineStatus(todo));
        return todoRepository.save(todo);
    }

    private void updatePendingStatuses(String email) {
        List<Todo> pending = todoRepository.findByUserEmailAndCompleted(email, false);
        for (Todo todo : pending) {
            String newStatus = determineStatus(todo);
            if (!newStatus.equals(todo.getStatus())) {
                todo.setStatus(newStatus);
                todoRepository.save(todo);
            }
        }
    }

    @Override
    public List<Todo> getAllTodos() {
        String email = getCurrentUserEmail();
        updatePendingStatuses(email);
        return todoRepository.findByUserEmail(email);
    }

    @Override
    public List<Todo> getCompletedTodos() {
        return todoRepository.findByUserEmailAndCompleted(getCurrentUserEmail(), true);
    }

    @Override
    public List<Todo> getPendingTodos() {
        String email = getCurrentUserEmail();
        updatePendingStatuses(email);
        return todoRepository.findByUserEmailAndCompleted(email, false);
    }

    @Override
    public List<Todo> searchTodos(String keyword) {
        return todoRepository.findByUserEmailAndTaskContainingIgnoreCase(getCurrentUserEmail(), keyword);
    }

    @Override
    public List<Todo> getTodosByPriority(String priority) {
        return todoRepository.findByUserEmailAndPriority(getCurrentUserEmail(), priority);
    }

    @Override
    public List<Todo> getByStatus(String status) {
        String email = getCurrentUserEmail();
        updatePendingStatuses(email);
        return todoRepository.findByUserEmailAndStatus(email, status);
    }

    @Override
    public DashboardStatsResponse getDashboardStats() {
        String email = getCurrentUserEmail();
        updatePendingStatuses(email);
        
        long totalTasks = todoRepository.countByUserEmail(email);
        long completedTasks = todoRepository.countByUserEmailAndCompleted(email, true);
        long pendingTasks = todoRepository.countByUserEmailAndCompleted(email, false);
        double completionRate = totalTasks > 0 ? (completedTasks * 100.0 / totalTasks) : 0;

        long overdueTasks = todoRepository.findByUserEmailAndStatus(email, "OVERDUE").size();
        long upcomingTasks = todoRepository.findByUserEmailAndStatus(email, "UPCOMING").size() 
                           + todoRepository.findByUserEmailAndStatus(email, "DUE_SOON").size();

        DashboardStatsResponse stats = new DashboardStatsResponse();
        stats.setTotalTasks(totalTasks);
        stats.setCompletedTasks(completedTasks);
        stats.setPendingTasks(pendingTasks);
        stats.setCompletionRate(completionRate);
        stats.setOverdueTasks(overdueTasks);
        stats.setUpcomingTasks(upcomingTasks);
        return stats;
    }
}
