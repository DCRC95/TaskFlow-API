package com.taskflow.api;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;

import com.taskflow.api.domain.Project;
import com.taskflow.api.domain.User;
import com.taskflow.api.domain.UserRole;
import com.taskflow.api.repository.ProjectRepository;
import com.taskflow.api.repository.UserRepository;

class OptimisticLockingIT extends IntegrationTestBase {

    @Autowired
    UserRepository users;

    @Autowired
    ProjectRepository projects;

    @Test
    void concurrentUpdatesToSameProject_triggerOptimisticLockingException() {
        User owner = users.save(new User("lock@example.com", "hash", UserRole.USER, Instant.now()));

        Project created = projects.save(new Project(owner, "P1", "desc", Instant.now(), Instant.now()));

        Project p1 = projects.findById(created.getId()).orElseThrow();
        Project p2 = projects.findById(created.getId()).orElseThrow();

        p1.setDescription("first update");
        projects.saveAndFlush(p1);

        p2.setDescription("second update");

        assertThatThrownBy(() -> projects.saveAndFlush(p2))
                .isInstanceOf(OptimisticLockingFailureException.class);
    }
}


