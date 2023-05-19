--liquibase formatted sql
--changeset vaem:1
CREATE TABLE folders(
    id UUID NOT NULL DEFAULT gen_random_uuid(),
    name VARCHAR(256) NOT NULL,
    path VARCHAR(256),
    date_created TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY(id),
    UNIQUE(path)
);
CREATE TABLE files (
    id UUID NOT NULL DEFAULT gen_random_uuid(),
    name VARCHAR(256) NOT NULL,
    path VARCHAR(256),
    size BIGINT NOT NULL,
    date_created TIMESTAMP NOT NULL DEFAULT NOW(),
    date_updated TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY(id),
    UNIQUE(path)
);

--changeset vaem:2
CREATE TABLE folder_tracks (
    id UUID NOT NULL DEFAULT gen_random_uuid(),
    folder_id UUID NOT NULL,
    date TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY(id),
    FOREIGN KEY (folder_id) REFERENCES folders(id)
)