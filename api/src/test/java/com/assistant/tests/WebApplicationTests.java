package com.assistant.tests;

import com.assistant.controllers.ConsultantController;
import com.assistant.controllers.ProjectController;
import com.assistant.controllers.SkillController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class WebApplicationTests {

    @Autowired
    ConsultantController consultantController;

    @Autowired
    ProjectController projectController;

    @Autowired
    SkillController skillController;

    @Test
    void contextLoads() throws Exception {
        assertThat(consultantController).isNotNull();
        assertThat(projectController).isNotNull();
        assertThat(skillController).isNotNull();
    }
}
