package com.kmecpp.osmium.api.database.api.filter;

public class Filter extends AbstractFilter {

	Filter(String filter, Object value) {
		super(filter, value);
	}

	public static Filter of(String filter, Object value) {
		return new Filter(filter, value);
	}

	public static Filter where(String filter, Object value) {
		return new Filter(filter, value);
	}

}
