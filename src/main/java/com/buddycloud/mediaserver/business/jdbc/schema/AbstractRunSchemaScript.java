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

public abstract class AbstractRunSchemaScript {
	
	protected static final String SQL_DELIMITER = ";";

	
	@SuppressWarnings("unchecked")
	protected static void runScript(MetadataSource dataSource, String sqlFile)
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

}
