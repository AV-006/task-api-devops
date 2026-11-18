package in.msrit.devops.service;

import in.msrit.devops.model.Task;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TaskService {
    private final AtomicLong sequence = new AtomicLong(2);
    private final List<Task> tasks = new CopyOnWriteArrayList<>(List.of(
        new Task(1L, "Create the Git repository", false),
        new Task(2L, "Configure the CI pipeline", false)
    ));

    public List<Task> findAll() {
        return List.copyOf(tasks);
    }

    public Task findById(long id) {
        return tasks.stream().filter(task -> task.id() == id).findFirst().orElse(null);
    }

    public Task create(String title) {
        Task task = new Task(sequence.incrementAndGet(), title.trim(), false);
        tasks.add(task);
        return task;
    }
}
