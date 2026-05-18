package org.example.todo.repository;

import org.example.todo.model.Todo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoRepository extends MongoRepository<Todo, String> {

    List<Todo> findByUserEmail(String userEmail);

    List<Todo> findByCompleted(boolean completed);

    List<Todo> findByUserEmailAndCompleted(String userEmail, boolean completed);

    List<Todo> findByUserEmailAndPriority(String userEmail, String priority);

    List<Todo> findByUserEmailAndStatus(String userEmail, String status);

    List<Todo> findByStatus(String status);

    List<Todo> findByUserEmailAndTaskContainingIgnoreCase(String userEmail, String task);

    long countByUserEmail(String userEmail);

    long countByUserEmailAndCompleted(String userEmail, boolean completed);
}
