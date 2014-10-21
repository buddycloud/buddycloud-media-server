ALTER TABLE medias ALTER COLUMN fileExtension DROP NOT NULL;

-- Add a schema_version table!
CREATE TABLE schema_version (version INT NOT NULL PRIMARY KEY,
                             "when" TIMESTAMP,
                             description TEXT);
INSERT INTO schema_version (version, "when", description)
       VALUES (1, NOW(), 'DB schema versioning, drop not null from fileExtension column in medias table');