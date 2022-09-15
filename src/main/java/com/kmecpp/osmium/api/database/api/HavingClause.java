package com.kmecpp.osmium.api.database.api;

import com.kmecpp.osmium.api.database.api.filter.AbstractFilter;
import com.kmecpp.osmium.api.database.api.filter.AbstractFilter.FilterOperator;
import com.kmecpp.osmium.api.database.api.filter.AggregateFunctionFilter;
import com.kmecpp.osmium.api.database.api.filter.AggregateFunctionFilter.AggregateFunctions;
import com.kmecpp.osmium.api.database.api.filter.Filter;
import com.kmecpp.osmium.api.database.api.filter.FunctionFilter;
import com.kmecpp.osmium.api.database.api.filter.FunctionFilter.Functions;

public class HavingClause {

	private final HavingFilter having;

	HavingClause(AbstractFilter filter) {
		this.having = new HavingFilter(filter);
	}

	public static HavingClause create(AbstractFilter filter) {
		return new HavingClause(filter);
	}

	public AbstractFilter getHavingFilter() {
		return having;
	}

	private HavingClause appendFilter(FilterOperator operator, HavingFilter filter) {
		if (!having.hasNext()) {
			having.setHead(operator, filter);
			return this;
		}

		AbstractFilter headNodeFilter = having.getHead().getFilter();
		while (headNodeFilter.hasNext()) {
			headNodeFilter = headNodeFilter.getHead().getFilter();
		}

		headNodeFilter.setHead(operator, filter);
		return this;
	}

	/**
	 * This method emulates a SQL AND operator that has the ability
	 * to nest other SQL operators. For example if this HavingFilter
	 * has more than 1 CompleteFilter, it will encase them in ().
	 * This allows for more complex conditionals that are processed
	 * in a certain order of operations. An example of how this would appear
	 * in SQL is ... HAVING ... AND(... OPERATOR ...) ... AND(... OPERATOR...).
	 * <p>
	 * If there is only 1 CompleteFilter instance stored internally,
	 * then there is now encasing that CompleteFilter in ().
	 * An example of this in SQL would be ... HAVING ... AND ... AND.
	 *
	 * @param filter
	 * 			  A HavingFilter instance that will become
	 * 			  the new head FilterNode for this class.
	 * @return This instance of the HavingClause that the HavingFilter was added to.
	 */
	public HavingClause and(HavingFilter filter) {
		return appendFilter(FilterOperator.AND, filter);
	}

	/**
	 * This method emulates a SQL OR operator that has the ability
	 * to nest other SQL operators. For example if this HavingFilter
	 * has more than 1 CompleteFilter, it will encase them in ().
	 * This allows for more complex conditionals that are processed
	 * in a certain order of operations. An example of how this would appear
	 * in SQL is ... HAVING ... OR(... OPERATOR ...) ... OR(... OPERATOR ...).
	 * <p>
	 * If there is only 1 CompleteFilter instance stored internally,
	 * then there is now encasing that CompleteFilter in ().
	 * An example of this in SQL would be ... HAVING ... OR ... OR ...
	 *
	 * @param filter
	 * 			  A HavingFilter instance that will become
	 * 			  the new head FilterNode for this class.
	 * @return This instance of the HavingClause that the HavingFilter was added to.
	 */
	public HavingClause or(HavingFilter filter) {
		return appendFilter(FilterOperator.OR, filter);
	}

	/**
	 * This method emulates a SQL NOT operator that has the ability
	 * to nest other SQL operators. For example if this HavingFilter
	 * has more than 1 CompleteFilter, it will encase them in ().
	 * This allows for more complex conditionals that are processed
	 * in a certain order of operations. An example of how this would appear
	 * in SQL is ... HAVING ... NOT(... OPERATOR ...) ... NOT(... OPERATOR ...).
	 * <p>
	 * If there is only 1 CompleteFilter instance stored internally,
	 * then there is now encasing that CompleteFilter in (). An example
	 * of this in SQL would be ... HAVING ... NOT ... NOT.
	 *
	 * @param filter
	 * 			  A HavingFilter instance that will become
	 * 			  the new head FilterNode for this class.
	 * @return This instance of the HavingClause tha the HavingFilter was added to.
	 */
	public HavingClause not(HavingFilter filter) {
		return appendFilter(FilterOperator.NOT, filter);
	}

	public String getParameterizedStatement() {
		StringBuilder sb = new StringBuilder(" HAVING");
		return having.createParameterizedStatement(sb).toString();
	}

	public static class HavingFilter extends AbstractFilter {

		protected HavingFilter(AbstractFilter filter) {
			super(filter);
		}

		private HavingFilter appendFilter(FilterOperator operator, AbstractFilter filter) {
			AbstractFilter.CompleteFilter data = filter.getCompleteFilter(0);
			addCompleteFilter(new CompleteFilter(operator, data.getFilterString(), data.getFilterObject()));
			return this;
		}

		/**
		 * This method emulates a SQL AND operator but Filters added
		 * through this method will be internally chained inside ().
		 * The resulting SQL string from this would appear as
		 * HAVING ... OPERATOR(... AND ...).
		 * <p>
		 *
		 * @param filter
		 * 			  A Filter instance that will be added as
		 * 			  a CompleteFilter internally to this class.
		 * @return This instance of the HavingFilter that the FunctionFilter was added to.
		 */
		public HavingFilter and(Filter filter) {
			return appendFilter(FilterOperator.AND, filter);
		}

		/**
		 * This method emulates a SQL AND operator but FunctionFilters
		 * added through this method will be internally chained inside ().
		 * The resulting SQL string from this would appear as
		 * HAVING ... OPERATOR(... AND ...).
		 * <p>
		 *
		 * @param filter
		 * 			  A FunctionFilter instance that will be added
		 * 			  as a CompleteFilter internally to this class.
		 * @return This instance of the HavingFilter that the FunctionFilter was added to.
		 */
		public HavingFilter and(FunctionFilter filter) {
			return appendFilter(FilterOperator.AND, filter);
		}

		/**
		 * This method emulates a SQL AND operator but AggregateFunctionFilters
		 * added through this method will be internally chained inside ().
		 * The resulting SQL string from this would appear as
		 * HAVING ... OPERATOR(... AND ...).
		 * <p>
		 *
		 * @param filter
		 * 			  A AggregateFunctionFilter instance that will be
		 * 			  added as a CompleteFilter internally to this class.
		 * @return This instance of the HavingFilter that the AggregateFunctionFilter was added to.
		 */
		public HavingFilter and(AggregateFunctionFilter filter) {
			return appendFilter(FilterOperator.AND, filter);
		}

		/**
		 * This method emulates a SQL OR operator but Filters added
		 * through this method will be internally chained inside ().
		 * The resulting SQL string from this would appear as
		 * HAVING ... OPERATOR(... OR ...).
		 * <p>
		 *
		 * @param filter
		 * 			  A Filter instance that will be added as
		 * 			  a CompleteFilter internally to this class.
		 * @return This instance of the HavingFilter that the FunctionFilter was added to.
		 */
		public HavingFilter or(Filter filter) {
			return appendFilter(FilterOperator.OR, filter);
		}

		/**
		 * This method emulates a SQL OR operator but FunctionFilters
		 * added through this method will be internally chained inside ().
		 * The resulting SQL string from this would appear as
		 * HAVING ... OPERATOR(... OR ...).
		 * <p>
		 *
		 * @param filter
		 * 			  A FunctionFilter instance that will be added
		 * 			  as a CompleteFilter internally to this class.
		 * @return This instance of the HavingFilter that the FunctionFilter was added to.
		 */
		public HavingFilter or(FunctionFilter filter) {
			return appendFilter(FilterOperator.OR, filter);
		}

		/**
		 * This method emulates a SQL OR operator but AggregateFunctionFilters
		 * added through this method will be internally chained inside ().
		 * The resulting SQL string from this would appear as
		 * HAVING ... OPERATOR(... OR ...).
		 * <p>
		 *
		 * @param filter
		 * 			  A AggregateFunctionFilter instance that will be
		 * 			  added as a CompleteFilter internally to this class.
		 * @return This instance of the HavingFilter that the AggregateFunctionFilter was added to.
		 */
		public HavingFilter or(AggregateFunctionFilter filter) {
			return appendFilter(FilterOperator.OR, filter);
		}

		/**
		 * This method emulates a SQL NOT operator but Filters added
		 * through this method will be internally chained inside ().
		 * The resulting SQL string from this would appear as
		 * HAVING ... OPERATOR(... NOT ...).
		 * <p>
		 *
		 * @param filter
		 * 			  A Filter instance that will be added as
		 * 			  a CompleteFilter internally to this class.
		 * @return This instance of the HavingFilter that the FunctionFilter was added to.
		 */
		public HavingFilter not(Filter filter) {
			return appendFilter(FilterOperator.NOT, filter);
		}

		/**
		 * This method emulates a SQL NOT operator but FunctionFilters
		 * added through this method will be internally chained inside ().
		 * The resulting SQL string from this would appear as
		 * HAVING ... OPERATOR(... NOT ...).
		 * <p>
		 *
		 * @param filter
		 * 			  A FunctionFilter instance that will be added
		 * 			  as a CompleteFilter internally to this class.
		 * @return This instance of the HavingFilter that the FunctionFilter was added to.
		 */
		public HavingFilter not(FunctionFilter filter) {
			return appendFilter(FilterOperator.NOT, filter);
		}

		/**
		 * This method emulates a SQL NOT operator but AggregateFunctionFilters
		 * added through this method will be internally chained inside ().
		 * The resulting SQL string from this would appear as
		 * HAVING ... OPERATOR(... NOT ...).
		 * <p>
		 *
		 * @param filter
		 * 			  A AggregateFunctionFilter instance that will be
		 * 			  added as a CompleteFilter internally to this class.
		 * @return This instance of the HavingFilter that the AggregateFunctionFilter was added to.
		 */
		public HavingFilter not(AggregateFunctionFilter filter) {
			return appendFilter(FilterOperator.NOT, filter);
		}

		public static HavingFilter of(String filter, Object value) {
			return of(Filter.of(filter, value));
		}

		public static HavingFilter of(Filter filter) {
			return new HavingFilter(filter);
		}

		public static HavingFilter distinct(String column, Object value) {
			return new HavingFilter(new FunctionFilter(Functions.DISTINCT, column, value));
		}

		public static HavingFilter date(String column, Object value) {
			return new HavingFilter(new FunctionFilter(Functions.DATE, column, value));
		}

		public static HavingFilter year(String column, Object value) {
			return new HavingFilter(new FunctionFilter(Functions.YEAR, column, value));
		}

		public static HavingFilter quarter(String column, Object value) {
			return new HavingFilter(new FunctionFilter(Functions.QUARTER, column, value));
		}

		public static HavingFilter month(String column, Object value) {
			return new HavingFilter(new FunctionFilter(Functions.MONTH, column, value));
		}

		public static HavingFilter week(String column, Object value) {
			return new HavingFilter(new FunctionFilter(Functions.WEEK, column, value));
		}

		public static HavingFilter weekOfYear(String column, Object value) {
			return new HavingFilter(new FunctionFilter(Functions.WEEKOFYEAR, column, value));
		}

		public static HavingFilter day(String column, Object value) {
			return new HavingFilter(new FunctionFilter(Functions.DAY, column, value));
		}

		public static HavingFilter dayOfWeek(String column, Object value) {
			return new HavingFilter(new FunctionFilter(Functions.DAYOFWEEK, column, value));
		}

		public static HavingFilter dayOfMonth(String column, Object value) {
			return new HavingFilter(new FunctionFilter(Functions.DAYOFMONTH, column, value));
		}

		public static HavingFilter dayOfYear(String column, Object value) {
			return new HavingFilter(new FunctionFilter(Functions.DAYOFYEAR, column, value));
		}

		public static HavingFilter hour(String column, Object value) {
			return new HavingFilter(new FunctionFilter(Functions.HOUR, column, value));
		}

		public static HavingFilter minute(String column, Object value) {
			return new HavingFilter(new FunctionFilter(Functions.MINUTE, column, value));
		}

		public static HavingFilter second(String column, Object value) {
			return new HavingFilter(new FunctionFilter(Functions.SECOND, column, value));
		}

		public static HavingFilter min(String column, Object value) {
			return new HavingFilter(new AggregateFunctionFilter(AggregateFunctions.MIN, column, value));
		}

		public static HavingFilter max(String column, Object value) {
			return new HavingFilter(new AggregateFunctionFilter(AggregateFunctions.MAX, column, value));
		}

		public static HavingFilter sum(String column, Object value) {
			return new HavingFilter(new AggregateFunctionFilter(AggregateFunctions.SUM, column, value));
		}

		public static HavingFilter avg(String column, Object value) {
			return new HavingFilter(new AggregateFunctionFilter(AggregateFunctions.AVG, column, value));
		}

		public static HavingFilter count(String column, Object value) {
			return new HavingFilter(new AggregateFunctionFilter(AggregateFunctions.COUNT, column, value));
		}

	}

}
