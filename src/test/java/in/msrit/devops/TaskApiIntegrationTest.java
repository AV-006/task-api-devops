package in.msrit.devops;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TaskApiIntegrationTest {
    @Autowired private MockMvc mvc;

    @Test
    void listTasksReturnsSeededTasks() throws Exception {
        mvc.perform(get("/tasks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(2)))
            .andExpect(jsonPath("$[0].title").value("Create the Git repository"));
    }

    @Test
    void createTaskReturns201() throws Exception {
        mvc.perform(post("/tasks").contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Write Ansible playbook\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title").value("Write Ansible playbook"))
            .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    void blankTitleReturns400() throws Exception {
        mvc.perform(post("/tasks").contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"   \"}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void unknownTaskReturns404() throws Exception {
        mvc.perform(get("/tasks/99999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void healthEndpointIsUp() throws Exception {
        mvc.perform(get("/actuator/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP"));
    }
}
