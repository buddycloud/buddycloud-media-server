/*
 * Copyright 2012 buddycloud
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
package com.buddycloud.mediaserver.business.jdbc.schema;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.buddycloud.mediaserver.business.jdbc.MetaDataSource;

public class Schema {

	private static final String SQL_DELIMITER = ";";
	private static final String SQL_CREATE_SCHEMA_FILE = "resources/schema/create_schema.sql";
	private static final String SQL_DROP_SCHEMA_FILE = "resources/schema/drop_schema.sql";

	@SuppressWarnings("unchecked")
	public void runScript(MetaDataSource dataSource, String sqlFile)
			throws IOException, FileNotFoundException, SQLException {
		List<String> readLines = IOUtils
				.readLines(new FileInputStream(sqlFile));

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

	public void create() throws FileNotFoundException, IOException,
			SQLException {
		MetaDataSource dataSource = new MetaDataSource();

		runScript(dataSource, SQL_CREATE_SCHEMA_FILE);
	}

	public void drop() throws FileNotFoundException, IOException, SQLException {
		MetaDataSource dataSource = new MetaDataSource();

		runScript(dataSource, SQL_DROP_SCHEMA_FILE);
	}
}
