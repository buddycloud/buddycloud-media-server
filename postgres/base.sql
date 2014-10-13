--{
--  "id": string,
--  "fileName": string,
--  "author": string,
--  "title": string,
--  "mimeType": string,
--  "description": string,
--  "uploadedDate": datetime,
--  "lastUpdatedDate": datetime,
--  "fileExtension": string,
--  "shaChecksum": string,
--  "fileSize": long,
--  "length": long,
--  "height": int,
--  "width": int
--}

CREATE TABLE medias(
	id TEXT,
	fileName TEXT,
	entityId TEXT NOT NULL,
	author TEXT NOT NULL,
	title TEXT,
	description TEXT,
	mimeType TEXT NOT NULL,
	uploadedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	lastUpdatedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	fileExtension TEXT NOT NULL,
	shaChecksum TEXT NOT NULL,
	fileSize BIGINT NOT NULL,
	length BIGINT,
	height INT,
	width INT,
	PRIMARY KEY(id)
);

CREATE INDEX entityUpdated_idx ON medias (lastUpdatedDate, entityId);

-- {
--   "id": string,
--   "shaChecksum": string,
--   "mediaId": string,
--	 "fileSize": long,
--   "height": int,
--   "width": int
-- }

CREATE TABLE previews(
	id TEXT,
	shaChecksum TEXT,
	mediaId TEXT REFERENCES medias(id),
	fileSize BIGINT,
	height INT,
	width INT,
	mimeType TEXT NOT NULL,
	PRIMARY KEY(id)
);

CREATE TABLE avatars(
	id SERIAL PRIMARY KEY,
	mediaId TEXT REFERENCES medias(id),
	entityId TEXT NOT NULL
);

CREATE UNIQUE INDEX entity_idx ON avatars (entityId);
