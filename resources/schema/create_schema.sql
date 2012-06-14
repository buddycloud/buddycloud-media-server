--{
--  "id": string,
--  "fileName": string,
--  "author": string,
--  "title": string,
--  "mimeType": string,
--  "description": string,
--  "uploadedDate": datetime,
--  "lastViewedDate": datetime,
--  "fileExtension": string,
--  "shaChecksum": string,
--  "fileSize": long,
--  "length": long,
--  "height": int,
--  "width": int
--}

CREATE TABLE medias(
	id TEXT,
	fileName TEXT NOT NULL,
	entityId TEXT NOT NULL,
	author TEXT NOT NULL,
	title TEXT NOT NULL,
	description TEXT NOT NULL,
	mimeType TEXT NOT NULL,
	uploadedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	lastViewedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	fileExtension TEXT NOT NULL,
	shaChecksum TEXT NOT NULL,
	fileSize BIGINT NOT NULL,
	length BIGINT,
	height INT,
	width INT,
	PRIMARY KEY(id)
);

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
	PRIMARY KEY(id)
);

CREATE SEQUENCE avatars_id_seq;
CREATE TABLE avatars(
	id TEXT PRIMARY KEY DEFAULT nextval('avatars_id_seq'),
	mediaId TEXT REFERENCES medias(id),
	entityId TEXT NOT NULL
);

CREATE UNIQUE INDEX entity_idx ON avatars (entityId);
