package com.buddycloud.mediaserver.business.jdbc.schema;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.buddycloud.mediaserver.business.jdbc.MetadataSource;
import com.buddycloud.mediaserver.commons.ConfigurationUtils;

public class Schema {
	
	private static final String SQL_DELIMITER = ";";
	private static final String SQL_CREATE_SCHEMA_FILE = "resources/schema/create_schema.sql";
	private static final String SQL_DROP_SCHEMA_FILE = "resources/schema/drop_schema.sql";
	
	
	@SuppressWarnings("unchecked")
	public void runScript(MetadataSource dataSource, String sqlFile)
			throws IOException, FileNotFoundException, SQLException {
		List<String> readLines = IOUtils.readLines(
				new FileInputStream(sqlFile));

		Connection connection = dataSource.getConnection();
		StringBuilder statementStr = new StringBuilder();

		for (String line : readLines) {
			statementStr.append(line);
			if (line.endsWith(SQL_DELIMITER)) {
				Statement statement = connection.createStatement();
				statement.execute(statementStr.toString());
				statement.close();
				statementStr.setLength(0);
			}
		}

		connection.close();
	}
	
	public void create() throws FileNotFoundException, IOException, SQLException {
		MetadataSource dataSource = new MetadataSource(
				ConfigurationUtils.loadConfiguration());

		runScript(dataSource, SQL_CREATE_SCHEMA_FILE);
	}
	
	public void drop() throws FileNotFoundException, IOException, SQLException {
		MetadataSource dataSource = new MetadataSource(
				ConfigurationUtils.loadConfiguration());

		runScript(dataSource, SQL_DROP_SCHEMA_FILE);
	}
}
