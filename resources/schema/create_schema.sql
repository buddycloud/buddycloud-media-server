CREATE TABLE media(
	media_id UUID,
	media_path TEXT NOT NULL,
	media_name TEXT NOT NULL,
	media_type TEXT NOT NULL,
	upload_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY(media_id)
);

-- file UUID
-- original filename
-- date taken (video and photo)
-- file size
-- checksum
-- file resolution (video and photo)
-- length in seconds (for videos)
-- tags
-- upload time
-- uploader's jid
-- lat/long
-- license
-- mime/type (image,video,word-doc etc)
-- path
