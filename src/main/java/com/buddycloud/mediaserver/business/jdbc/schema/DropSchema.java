package com.buddycloud.mediaserver.business.jdbc.schema;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.SQLException;

public class DropSchema {

	public static void main(String[] args) throws PropertyVetoException, IOException, SQLException {
		new Schema().drop();
	}
}
