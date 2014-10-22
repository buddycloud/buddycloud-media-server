BEGIN TRANSACTION;

CREATE TABLE "configuration" ("key" TEXT NOT NULL,
            "value" TEXT NOT NULL,
            "updated" TIMESTAMP);

INSERT INTO schema_version (version, "when", description)
       VALUES (2, NOW(), 'Added configuration table');

COMMIT;
