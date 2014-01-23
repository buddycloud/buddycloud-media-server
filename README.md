buddycloud-media-server
=======================

A media server for [buddycloud][bc] channels. It provides a simple 
REST-like HTTP interface where clients can do several operations like:
- **upload**: upload media to private/public channels, the permissions
are based on channels [pubsub subscriptions][pubsub];
- **download**: download/visualize media from channels that you have 
enough permissions;
- **delete**: you can delete medias that you've uploaded or if you are
a channel moderator/owner;
- **update**: update media metadata, with similar permissions as the
delete operation.
 
To authenticate HTTP requests, the media server uses [XEP-0070][xep],
this means that the client **must** have an XMPP client that "understands"
such protocol in order to do media requests. There is only one exception: 
download media from public channels - any client has access.

[bc]: http://buddycloud.com/
[xep]: http://xmpp.org/extensions/xep-0070.html
[pubsub]: https://buddycloud.org/wiki/XMPP_XEP#Follower_Management

Usage
-----

The API is described in detail [here](https://buddycloud.org/wiki/Buddycloud_HTTP_API#.2F.3Cchannel.3E.2Fmedia.2F.3Citem.3E).

Setup
-----

The server is written on top of Java using [RESTlet](http://www.restlet.org/).

It uses [Maven](http://maven.apache.org/) to build its packages. You can build
the package manually or download it from [here](https://github.com/downloads/buddycloud/buddycloud-media-server/buddycloud-media-server-0.1.zip).

After unpacking, you can then start it by invoking

    bash mediaserver

The server needs to be configured to point to a Buddycloud and XMPP server. 
See the *Configuration* section.

Configuration
-------------

You can configure the media server by copying `mediaserver.properties.example` to 
`mediaserver.properties` in the server's root directory, and then editing as 
required. This file has multiple properties definitions:

	# HTTP 
	http.port=8080
	https.port=8443
	https.enabled=true
	https.keystore.path=/$HOME/.jetty/jetty.jks
	https.keystore.type=JSK
	https.keystore.password=password
	https.key.password=password
	
	# XMPP
	xmpp.component.host=localhost
	xmpp.component.port=5275
	xmpp.component.subdomain=mediaserver.example.com
	xmpp.component.secretkey=secret
	
	xmpp.connection.username=mediaserver-test
	xmpp.connection.password=mediaserver-test
	xmpp.connection.host=localhost
	xmpp.connection.port=5222
	xmpp.connection.servicename=example.com
	
	# JDBC
	jdbc.db.url=jdbc:postgresql://localhost:5432/mediaserver?user=postgres&password=postgres
	jdbc.driver.class=org.postgresql.Driver

	# Max threshold beyond which files are written directly to disk, in bytes
	# Only used while uploading multipart form data files
	media.todisk.threshold=1048576
	
	# File System
	media.storage.root=/tmp
	media.sizelimit=1000240

The following configuration options are supported:

HTTP related configurations:

- **https.enabled** (Optional): if the HTTPS is enabled (default is **false**). If is set to **true**
you **must** provide the others *https* properties.
- **https.port**: the port where the server will listen for HTTPS requests.
- **https.keystore**: the HTTPS keystore location.
- **https.keystore.type**: the keystore type.
- **https.keystore.password**: the keystore password.
- **https.key.password**: the HTTPS key password.
- **http.port** (Optional): the HTTP port where the server will listen for HTTP requests (default is *8080*).

XMPP related:

- **xmpp.component.host** (Required): the XMPP server location where the media server's component will connect. 
- **xmpp.component.port** (Required): the XMPP server components connection listening port.
- **xmpp.component.subdomain** (Required): the *subdomain* that will be used by the component.
- **xmpp.component.secretkey** (Required): the *secretkey* defined at the XMPP server for components connections.
	
In addition of the component, the media server also have a simple client that handles pubsub queries: 

- **xmpp.connection.username** (Required): the *username* used by the cient's connection.
- **xmpp.connection.password** (Required): client's connection *password*.
- **xmpp.connection.host** (Required): XMPP server location.
- **xmpp.connection.port** (Required): XMPP server port for clients connections.
- **xmpp.connection.servicename** (Required): client's connection *servicename*.

Storage related:

- **jdbc.db.url** (Required): the server uses [PostgresSQL](http://www.postgresql.org) to store
media's metadata and uses [JDBC](http://www.oracle.com/technetwork/java/overview-141217.html) to access it.
- **jdbc.driver.class** (Optional): if someday the media server allow a different database, this
property will be used (default is *org.postgresql.Driver*).
- **media.storage.root** (Required): root path where the media server will store the media files.
- **media.sizelimit** (Optional): the tolerated file content size which the media server will store (default is *104857600* - 100 MB).
- **media.todisk.threshold** (Optional): the tolerated file size in bytes (default is *1048576* - 1 MB) which beyond are directly stored on disk.
