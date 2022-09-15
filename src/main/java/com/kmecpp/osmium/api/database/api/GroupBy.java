package com.kmecpp.osmium.api.database.api;

import com.kmecpp.osmium.api.util.StringUtil;

public class GroupBy {

	private final String[] columns;

	private GroupBy(String... columns) {
		if (columns == null) {
			throw new IllegalArgumentException("Database GroupBy columns cannot be null!");
		} else if (columns.length == 0) {
			throw new IllegalArgumentException("Database GroupBy columns cannot be empty!");
		}

		this.columns = columns;
	}

	public static GroupBy of(String column) {
		return new GroupBy(column);
	}

	public String[] getColumns() {
		return columns;
	}

	@Override
	public String toString() {
		return " GROUP BY " + StringUtil.join(columns, ",");
	}

}
