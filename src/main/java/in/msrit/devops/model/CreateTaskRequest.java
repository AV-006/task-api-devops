package in.msrit.devops.model;

import jakarta.validation.constraints.NotBlank;

public record CreateTaskRequest(@NotBlank(message = "title must not be blank") String title) { }
