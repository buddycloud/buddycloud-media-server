package com.buddycloud.mediaserver.business.jdbc.schema;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.SQLException;

import com.buddycloud.mediaserver.business.jdbc.MetadataSource;
import com.buddycloud.mediaserver.commons.ConfigurationUtils;

public class DropSchema extends AbstractRunSchemaScript {

	private static final String SQL_DROP_SCHEMA_FILE = "resources/schema/drop_schema.sql";

	
	public static void main(String[] args) throws PropertyVetoException, IOException, SQLException {
		MetadataSource dataSource = new MetadataSource(
				ConfigurationUtils.loadConfiguration());

		runScript(dataSource, SQL_DROP_SCHEMA_FILE);
	}
}
