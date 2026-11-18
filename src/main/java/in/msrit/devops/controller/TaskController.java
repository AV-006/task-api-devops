package in.msrit.devops.controller;

import in.msrit.devops.model.CreateTaskRequest;
import in.msrit.devops.model.Task;
import in.msrit.devops.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Map;

@RestController
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/")
    public Map<String, String> index(
            @org.springframework.beans.factory.annotation.Value("${app.name:Task API}") String appName,
            @org.springframework.beans.factory.annotation.Value("${app.version:1.0.0}") String version,
            @org.springframework.beans.factory.annotation.Value("${app.environment:local}") String environment) {
        return Map.of(
            "application", appName,
            "version", version,
            "environment", environment,
            "instance", System.getenv().getOrDefault("HOSTNAME", "local")
        );
    }

    @GetMapping("/tasks")
    public List<Task> getTasks() {
        return taskService.findAll();
    }

    @GetMapping("/tasks/{id}")
    public Task getTask(@PathVariable long id) {
        Task task = taskService.findById(id);
        if (task == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found");
        }
        return task;
    }

    @PostMapping("/tasks")
    public ResponseEntity<Task> createTask(@Valid @RequestBody CreateTaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.create(request.title()));
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }
}
