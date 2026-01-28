package com.taskflow.api.repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("""
            select count(t)
            from Task t
            where t.project.owner.email = :ownerEmail
              and t.createdAt >= :from
              and t.createdAt < :to
            """)
    long countCreatedByOwnerAndCreatedAtBetween(@Param("ownerEmail") String ownerEmail,
                                                @Param("from") Instant from,
                                                @Param("to") Instant to);

    @Query("""
            select count(t)
            from Task t
            where t.project.owner.email = :ownerEmail
              and t.status = com.taskflow.api.domain.TaskStatus.DONE
              and t.updatedAt >= :from
              and t.updatedAt < :to
            """)
    long countCompletedByOwnerAndUpdatedAtBetween(@Param("ownerEmail") String ownerEmail,
                                                  @Param("from") Instant from,
                                                  @Param("to") Instant to);

    @Query("""
            select count(t)
            from Task t
            where t.project.owner.email = :ownerEmail
              and t.status = com.taskflow.api.domain.TaskStatus.OVERDUE
              and t.updatedAt >= :from
              and t.updatedAt < :to
            """)
    long countOverdueByOwnerAndUpdatedAtBetween(@Param("ownerEmail") String ownerEmail,
                                                @Param("from") Instant from,
                                                @Param("to") Instant to);

    @Query("""
            select p.id as projectId, p.name as projectName, count(t) as openTasks
            from Task t
            join t.project p
            where p.owner.email = :ownerEmail
              and t.status in :openStatuses
            group by p.id, p.name
            order by openTasks desc
            """)
    List<TopProjectOpenTasksView> findTopProjectsByOwnerAndOpenStatuses(
            @Param("ownerEmail") String ownerEmail,
            @Param("openStatuses") List<TaskStatus> openStatuses,
            Pageable pageable
    );


}

