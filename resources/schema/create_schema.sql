--{
--  "id": string,
--  "uploader": string,
--  "title": string,
--  "mimeType": string,
--  "description": string,
--  "uploadedDate": datetime,
--  "lastViewedDate": datetime,
--  "fileExtension": string,
--  "md5Checksum": string,
--  "fileSize": long,
--  "length": long,
--  "height": int,
--  "width": int
--}

CREATE TABLE media(
	id TEXT,
	entityId TEXT REFERENCES entities(id),
	uploader TEXT NOT NULL,
	title TEXT NOT NULL,
	mimeType TEXT NOT NULL,
	uploadedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	lastViewedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	fileExtension TEXT,
	md5Checksum TEXT,
	fileSize BIGINT,
	length BIGINT,
	height INT,
	width INT,
	PRIMARY KEY(id)
);

-- {
--   "id": string,
--   "md5Checksum": string,
--   "mediaId": string,
--	 "fileSize": long,
--   "height": int,
--   "width": int
-- }

CREATE TABLE previews(
	id TEXT,
	md5Checksum TEXT,
	mediaId TEXT REFERENCES media(id),
	fileSize BIGINT,
	height INT,
	width INT,
	PRIMARY KEY(id)
);

CREATE TABLE avatar(
	id TEXT,
	mediaId TEXT REFERENCES media(id),
	entityId TEXT NOT NULL,
	PRIMARY KEY(id)
);
