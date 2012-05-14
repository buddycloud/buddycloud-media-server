package com.buddycloud.mediaserver.business.db;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.SQLException;

import com.buddycloud.mediaserver.commons.ConfigurationUtils;

public class CreateSchema extends AbstractRunSchemaScript {

	private static final String SQL_CREATE_SCHEMA_FILE = "resources/schema/create_schema.sql";

	
	public static void main(String[] args) throws PropertyVetoException, IOException, SQLException {
		MetadataSource dataSource = new MetadataSource(
				ConfigurationUtils.loadConfiguration());

		runScript(dataSource, SQL_CREATE_SCHEMA_FILE);
	}
}
