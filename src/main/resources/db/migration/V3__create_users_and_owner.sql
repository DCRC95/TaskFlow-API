CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL
);

ALTER TABLE projects
ADD COLUMN owner_id BIGINT NOT NULL;

ALTER TABLE projects
ADD CONSTRAINT fk_projects_owner
FOREIGN KEY (owner_id)
REFERENCES users(id)
ON DELETE RESTRICT;

CREATE INDEX idx_projects_owner_id ON projects(owner_id);
