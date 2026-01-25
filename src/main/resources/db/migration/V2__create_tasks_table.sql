CREATE TABLE tasks (
    id BIGSERIAL PRIMARY KEY,

    project_id BIGINT NOT NULL,

    title VARCHAR(200) NOT NULL,
    status VARCHAR(30) NOT NULL,
    due_date DATE,

    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT fk_tasks_project
        FOREIGN KEY (project_id)
        REFERENCES projects(id)
        ON DELETE CASCADE
);
