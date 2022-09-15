package com.kmecpp.osmium.api.database.api;

import com.kmecpp.osmium.api.database.api.filter.AbstractFilter;
import com.kmecpp.osmium.api.database.api.filter.AbstractFilter.FilterOperator;
import com.kmecpp.osmium.api.database.api.filter.Filter;
import com.kmecpp.osmium.api.database.api.filter.FunctionFilter;
import com.kmecpp.osmium.api.database.api.filter.FunctionFilter.Functions;

public class WhereClause {

	private final WhereFilter where;

	private WhereClause(WhereFilter whereFilter) {
		if (whereFilter == null) {
			throw new IllegalArgumentException("The Filter cannot be null!");
		}

		this.where = whereFilter;
	}

	public static WhereClause create(WhereFilter filter) {
		return new WhereClause(filter);
	}

	public WhereFilter getWhereFilter() {
		return this.where;
	}

	public String getParameterizedStatement() {
		StringBuilder sb = new StringBuilder(" WHERE");
		return where.createParameterizedStatement(sb).toString();
	}

	private WhereClause appendFilter(FilterOperator operator, WhereFilter filter) {
		if (!where.hasNext()) {
			where.setHead(operator, filter);
			return this;
		}

		AbstractFilter headNodeFilter = where.getHead().getFilter();
		while (headNodeFilter.hasNext()) {
			headNodeFilter = headNodeFilter.getHead().getFilter();
		}

		headNodeFilter.setHead(operator, filter);
		return this;
	}

	/**
	 * This method emulates a SQL AND operator that has the ability
	 * to nest other SQL operators. For example if this WhereFilter
	 * has more than 1 CompleteFilter, it will encase them in ().
	 * This allows for more complex conditionals that are processed
	 * in a certain order of operations. An example of how this would appear
	 * in SQL is ... WHERE ... AND(... OPERATOR ...) ... AND(... OPERATOR...).
	 * <p>
	 * If there is only 1 CompleteFilter instance stored internally,
	 * then there is now encasing that CompleteFilter in ().
	 * An example of this in SQL would be ... WHERE ... AND ... AND.
	 *
	 * @param filter
	 * 			  A WhereFilter instance that will become
	 * 			  the new head FilterNode for this class.
	 * @return This instance of the WhereClause that the WhereFilter was added to.
	 */
	public WhereClause and(WhereFilter filter) {
		return appendFilter(FilterOperator.AND, filter);
	}

	/**
	 * This method emulates a SQL OR operator that has the ability
	 * to nest other SQL operators. For example if this WhereFilter
	 * has more than 1 CompleteFilter, it will encase them in ().
	 * This allows for more complex conditionals that are processed
	 * in a certain order of operations. An example of how this would appear
	 * in SQL is ... WHERE ... OR(... OPERATOR ...) ... OR(... OPERATOR...).
	 * <p>
	 * If there is only 1 CompleteFilter instance stored internally,
	 * then there is now encasing that CompleteFilter in ().
	 * An example of this in SQL would be ... WHERE ... AND ... AND.
	 *
	 * @param filter
	 * 			  A WhereFilter instance that will become
	 * 			  the new head FilterNode for this class.
	 * @return This instance of the WhereClause that the WhereFilter was added to.
	 */
	public WhereClause or(WhereFilter filter) {
		return appendFilter(FilterOperator.OR, filter);
	}

	/**
	 * This method emulates a SQL NOT operator that has the ability
	 * to nest other SQL operators. For example if this WhereFilter
	 * has more than 1 CompleteFilter, it will encase them in ().
	 * This allows for more complex conditionals that are processed
	 * in a certain order of operations. An example of how this would appear
	 * in SQL is ... WHERE ... NOT(... OPERATOR ...) ... NOT(... OPERATOR...).
	 * <p>
	 * If there is only 1 CompleteFilter instance stored internally,
	 * then there is now encasing that CompleteFilter in ().
	 * An example of this in SQL would be ... WHERE ... AND ... AND.
	 *
	 * @param filter
	 * 			  A WhereFilter instance that will become
	 * 			  the new head FilterNode for this class.
	 * @return This instance of the WhereClause that the WhereFilter was added to.
	 */
	public WhereClause not(WhereFilter filter) {
		return appendFilter(FilterOperator.NOT, filter);
	}

	public static class WhereFilter extends AbstractFilter {

		public WhereFilter(AbstractFilter filter) {
			super(filter);
		}

		private WhereFilter appendFilter(FilterOperator operator, WhereFilter filter) {
			AbstractFilter.CompleteFilter data = filter.getCompleteFilter(0);
			addCompleteFilter(new AbstractFilter.CompleteFilter(operator, data.getFilterString(), data.getFilterObject()));
			return this;
		}

		/**
		 * This method emulates a SQL AND operator but Filters added
		 * through this method will be internally chained inside ().
		 * The resulting SQL string from this would appear as
		 * WHERE ... OPERATOR(... AND ...).
		 * <p>
		 *
		 * @param filter
		 * 			  A WhereFilter instance that will be added as
		 * 			  a CompleteFilter internally to this class.
		 * @return This instance of the WhereFilter that the WhereFilter was added to.
		 */
		public WhereFilter and(WhereFilter filter) {
			return appendFilter(FilterOperator.AND, filter);
		}

		/**
		 * This method emulates a SQL OR operator but Filters added
		 * through this method will be internally chained inside ().
		 * The resulting SQL string from this would appear as
		 * WHERE ... OPERATOR(... OR ...).
		 * <p>
		 *
		 * @param filter
		 * 			  A WhereFilter instance that will be added as
		 * 			  a CompleteFilter internally to this class.
		 * @return This instance of the WhereFilter that the WhereFilter was added to.
		 */
		public WhereFilter or(WhereFilter filter) {
			return appendFilter(FilterOperator.OR, filter);
		}

		/**
		 * This method emulates a SQL NOT operator but Filters added
		 * through this method will be internally chained inside ().
		 * The resulting SQL string from this would appear as
		 * WHERE ... OPERATOR(... NOT ...).
		 * <p>
		 *
		 * @param filter
		 * 			  A WhereFilter instance that will be added as
		 * 			  a CompleteFilter internally to this class.
		 * @return This instance of the WhereFilter that the WhereFilter was added to.
		 */
		public WhereFilter not(WhereFilter filter) {
			return appendFilter(FilterOperator.NOT, filter);
		}

		public static WhereFilter of(String filter, Object value) {
			return new WhereFilter(Filter.of(filter, value));
		}

		public static WhereFilter distinct(String filter, Object value) {
			return new WhereFilter(new FunctionFilter(Functions.DISTINCT, filter, value));
		}

		public static WhereFilter date(String filter, Object value) {
			return new WhereFilter(new FunctionFilter(Functions.DATE, filter, value));
		}

		public static WhereFilter year(String filter, Object value) {
			return new WhereFilter(new FunctionFilter(Functions.YEAR, filter, value));
		}

		public static WhereFilter quarter(String filter, Object value) {
			return new WhereFilter(new FunctionFilter(Functions.QUARTER, filter, value));
		}

		public static WhereFilter month(String filter, Object value) {
			return new WhereFilter(new FunctionFilter(Functions.MONTH, filter, value));
		}

		public static WhereFilter week(String filter, Object value) {
			return new WhereFilter(new FunctionFilter(Functions.WEEK, filter, value));
		}

		public static WhereFilter weekOfYear(String filter, Object value) {
			return new WhereFilter(new FunctionFilter(Functions.WEEKOFYEAR, filter, value));
		}

		public static WhereFilter day(String filter, Object value) {
			return new WhereFilter(new FunctionFilter(Functions.DAY, filter, value));
		}

		public static WhereFilter dayOfWeek(String filter, Object value) {
			return new WhereFilter(new FunctionFilter(Functions.DAYOFWEEK, filter, value));
		}

		public static WhereFilter dayOfMonth(String filter, Object value) {
			return new WhereFilter(new FunctionFilter(Functions.DAYOFMONTH, filter, value));
		}

		public static WhereFilter dayOfYear(String filter, Object value) {
			return new WhereFilter(new FunctionFilter(Functions.DAYOFYEAR, filter, value));
		}

		public static WhereFilter hour(String filter, Object value) {
			return new WhereFilter(new FunctionFilter(Functions.HOUR, filter, value));
		}

		public static WhereFilter minute(String filter, Object value) {
			return new WhereFilter(new FunctionFilter(Functions.MINUTE, filter, value));
		}

		public static WhereFilter second(String filter, Object value) {
			return new WhereFilter(new FunctionFilter(Functions.SECOND, filter, value));
		}

	}

}
