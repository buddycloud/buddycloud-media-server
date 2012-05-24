--{
--  "id": string,
--  "uploader": string,
--  "title": string,
--  "mimeType": string,
--  "description": string,
--  "uploadedDate": datetime,
--  "lastViewedDate": datetime,
--  "downloadUrl": string,
--  "fileExtension": string,
--  "md5Checksum": string,
--  "fileSize": long,
--  "length": long,
--  "height": int,
--  "width": int
--}

CREATE TABLE media(
	id TEXT,
	uploader TEXT NOT NULL,
	title TEXT NOT NULL,
	mimeType TEXT NOT NULL,
	uploadedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	lastViewedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	downloadUrl TEXT,
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
--   "downloadUrl": string,
--   "md5Checksum": string,
--   "mediaId": string,
--	 "fileSize": long,
--   "height": int,
--   "width": int
-- }

CREATE TABLE previews(
	id TEXT,
	downloadUrl TEXT,
	md5Checksum TEXT,
	mediaId TEXT REFERENCES media(id),
	fileSize BIGINT,
	height INT,
	width INT,
	PRIMARY KEY(id)
);
