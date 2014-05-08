/*
 * Copyright 2014 Buddycloud
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.buddycloud.mediaserver;

import com.buddycloud.mediaserver.delete.DeleteAvatarTest;
import com.buddycloud.mediaserver.delete.DeleteMediaTest;
import com.buddycloud.mediaserver.download.DownloadAvatarTest;
import com.buddycloud.mediaserver.download.DownloadImageTest;
import com.buddycloud.mediaserver.download.DownloadMediasInfoTest;
import com.buddycloud.mediaserver.download.DownloadVideoTest;
import com.buddycloud.mediaserver.update.UpdateAvatarTest;
import com.buddycloud.mediaserver.update.UpdateMediaTest;
import com.buddycloud.mediaserver.upload.UploadAvatarTest;
import com.buddycloud.mediaserver.upload.UploadMediaTest;
import com.buddycloud.mediaserver.xmpp.MediaServerComponentTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({
        // Upload
        UploadMediaTest.class,
        UploadAvatarTest.class,
        // Update
        UpdateMediaTest.class,
        UpdateAvatarTest.class,
        // Download
        DownloadImageTest.class,
        DownloadVideoTest.class,
        DownloadMediasInfoTest.class,
        DownloadAvatarTest.class,
        // Delete
        DeleteMediaTest.class,
        DeleteAvatarTest.class,
        // Disco
        MediaServerComponentTest.class
})
public class MediaServerTestSuite {}
