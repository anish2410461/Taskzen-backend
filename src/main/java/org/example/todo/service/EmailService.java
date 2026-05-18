package org.example.todo.service;

import lombok.RequiredArgsConstructor;
import org.example.todo.model.Todo;
import org.example.todo.model.User;
import org.example.todo.repository.TodoRepository;
import org.example.todo.repository.UserRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    // Runs every hour
    @Scheduled(fixedRate = 360000)
    public void sendOverdueReminders() {
        System.out.println("Running scheduled task: Checking for overdue tasks...");

        List<Todo> pendingTodos = todoRepository.findByCompleted(false);
        LocalDate today = LocalDate.now();

        // Group pending tasks by userEmail
        Map<String, List<Todo>> tasksByUser = pendingTodos.stream()
                .filter(t -> t.getUserEmail() != null)
                .collect(Collectors.groupingBy(Todo::getUserEmail));

        for (Map.Entry<String, List<Todo>> entry : tasksByUser.entrySet()) {
            String email = entry.getKey();
            List<Todo> userTasks = entry.getValue();

            // Find how many are overdue (dueDate < today)
            List<Todo> overdueTasks = userTasks.stream()
                    .filter(t -> t.getDueDate() != null && t.getDueDate().isBefore(today))
                    .collect(Collectors.toList());

            if (!overdueTasks.isEmpty()) {
                // Find user to get their name
                userRepository.findByEmail(email).ifPresent(user -> {
                    sendEmail(user, overdueTasks);
                });
            }
        }
    }

    private void sendEmail(User user, List<Todo> overdueTasks) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("taskzen4@gmail.com");
        message.setTo(user.getEmail());
        message.setSubject("⚠️ Action Required: You have overdue tasks!");

        StringBuilder body = new StringBuilder();
        body.append("Hey ").append(user.getName()).append(",\n\n");
        body.append("You still have ").append(overdueTasks.size()).append(" overdue tasks:\n\n");

        for (Todo task : overdueTasks) {
            body.append("• ").append(task.getTask()).append(" (Due: ").append(task.getDueDate()).append(")\n");
        }

        body.append("\nStay productive \uD83D\uDE80\n");
        body.append("- TaskZen Team");

        message.setText(body.toString());

        try {
            mailSender.send(message);
            System.out.println("Sent reminder email to: " + user.getEmail());
        } catch (Exception e) {
            System.out.println("EMAIL ERROR:");
            e.printStackTrace();
        }
    }

    private final java.util.Set<String> notifiedPreAlertIds = new java.util.concurrent.ConcurrentHashMap<String, Boolean>()
            .keySet(Boolean.TRUE);

    @Scheduled(fixedRate = 60000)
    public void sendPreAlertReminders() {
        System.out.println("Running scheduled task: Checking for tasks due soon (pre-alert)...");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threshold = now.plusMinutes(30);

        List<Todo> pendingTodos = todoRepository.findByCompleted(false);

        for (Todo todo : pendingTodos) {
            LocalDateTime due = todo.getDueDateTime();
            if (due != null && due.isBefore(threshold) && due.isAfter(now)) {
                if (!notifiedPreAlertIds.contains(todo.getId())) {
                    userRepository.findByEmail(todo.getUserEmail()).ifPresent(user -> {
                        sendPreAlertEmail(user, todo);
                        notifiedPreAlertIds.add(todo.getId());
                    });
                }
            }
        }
    }

    private void sendPreAlertEmail(User user, Todo todo) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("taskzen4@gmail.com");
        message.setTo(user.getEmail());
        message.setSubject("⏰ Alert: Your task \"" + todo.getTask() + "\" is due soon!");

        StringBuilder body = new StringBuilder();
        body.append("Hey ").append(user.getName()).append(",\n\n");
        body.append("This is a friendly reminder that your task is due in less than 30 minutes:\n\n");
        body.append("• ").append(todo.getTask()).append(" (Due at: ").append(todo.getDueDateTime()).append(")\n\n");
        body.append("Get ready to cross it off! 🚀\n");
        body.append("- TaskZen Team");

        message.setText(body.toString());

        try {
            mailSender.send(message);
            System.out.println("Sent pre-alert reminder email to: " + user.getEmail() + " for task: " + todo.getTask());
        } catch (Exception e) {
            System.out.println("EMAIL PRE-ALERT ERROR:");
            e.printStackTrace();
        }
    }
}
