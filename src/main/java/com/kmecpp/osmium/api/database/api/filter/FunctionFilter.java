package com.kmecpp.osmium.api.database.api.filter;

public class FunctionFilter extends AbstractFilter {
	
	public FunctionFilter(Functions function, String filter, Object value) {
		super(function.toString(), filter, value);
	}

	public static enum Functions {

		DISTINCT,
		DATE,
		YEAR,
		QUARTER,
		MONTH,
		WEEK,
		WEEKOFYEAR,
		DAY,
		DAYOFWEEK,
		DAYOFMONTH,
		DAYOFYEAR,
		HOUR,
		MINUTE,
		SECOND;

	}

	//TODO
	//static enum SearchFunction {
	//
	//	BETWEEN,
	//	IN,
	//
	//}

}
