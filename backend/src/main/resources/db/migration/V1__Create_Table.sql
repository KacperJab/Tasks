
CREATE SCHEMA IF NOT EXISTS test;

DROP TABLE IF EXISTS test.tasks;

CREATE TABLE test.tasks (
                            id text PRIMARY KEY NOT NULL UNIQUE,
                            userid text,
                            title text,
                            description text,
                            important BOOLEAN DEFAULT FALSE,
                            done BOOLEAN DEFAULT FALSE,
                            created_on timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)