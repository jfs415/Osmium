package com.kmecpp.osmium.api.database.api.filter;

public class AggregateFunctionFilter extends AbstractFilter {

	public AggregateFunctionFilter(AggregateFunctions function, String filter, Object value) {
		super(function.toString(), filter, value);
	}

	public static enum AggregateFunctions {

		SUM,
		AVG,
		MIN,
		MAX,
		COUNT;
	}

}
