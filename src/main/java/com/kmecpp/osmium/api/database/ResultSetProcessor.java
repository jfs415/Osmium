package com.kmecpp.osmium.api.database;

import java.sql.ResultSet;

@FunctionalInterface
public interface ResultSetProcessor {

	<T> T process(ResultSet rs);

}
