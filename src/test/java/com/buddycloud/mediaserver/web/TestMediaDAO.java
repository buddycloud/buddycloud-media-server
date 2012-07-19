package com.buddycloud.mediaserver.web;

import java.util.Properties;

import com.buddycloud.mediaserver.business.dao.MediaDAO;
import com.buddycloud.mediaserver.business.jdbc.MetaDataSource;
import com.buddycloud.mediaserver.xmpp.pubsub.PubSubController;
import com.google.gson.Gson;

public class TestMediaDAO extends MediaDAO {

	protected TestMediaDAO() {}
	
	protected TestMediaDAO(MetaDataSource dataSource, PubSubController pubsub, Properties configuration, Gson gson) {
		this.dataSource = dataSource;
		this.pubsub = pubsub;
		this.configuration = configuration;
		this.gson = gson;
	}
}
