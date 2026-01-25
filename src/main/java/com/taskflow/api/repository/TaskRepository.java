package com.taskflow.api.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.taskflow.api.domain.Task;
import com.taskflow.api.domain.TaskStatus;




public interface TaskRepository extends JpaRepository<Task, Long> {

    // List tasks for a project (paged)
    Page<Task> findByProjectId(Long projectId, Pageable pageable);

    // Filter example: project + status
    Page<Task> findByProjectIdAndStatus(Long projectId, TaskStatus status, Pageable pageable);

    // Filter example: project + due before
    Page<Task> findByProjectIdAndDueDateBefore(Long projectId, LocalDate dueBefore, Pageable pageable);

    // Filter example: due before and not status
    List<Task> findByDueDateBeforeAndStatusNot(LocalDate date, TaskStatus status);

    // Filter example: project + status + due before
    Page<Task> findByProjectIdAndStatusAndDueDateBefore(Long projectId,
        TaskStatus status,
        java.time.LocalDate dueBefore,
        Pageable pageable);
        Page<Task> findByProjectIdAndProjectOwnerEmail(Long projectId, String ownerEmail, Pageable pageable);

    Page<Task> findByProjectIdAndProjectOwnerEmailAndStatus(Long projectId, String ownerEmail, TaskStatus status, Pageable pageable);

    Page<Task> findByProjectIdAndProjectOwnerEmailAndDueDateBefore(Long projectId, String ownerEmail, LocalDate dueBefore, Pageable pageable);

    Page<Task> findByProjectIdAndProjectOwnerEmailAndStatusAndDueDateBefore(Long projectId, String ownerEmail, TaskStatus status, LocalDate dueBefore, Pageable pageable);

    Optional<Task> findByIdAndProjectOwnerEmail(Long taskId, String ownerEmail);

    boolean existsByIdAndProjectOwnerEmail(Long taskId, String ownerEmail);


}

