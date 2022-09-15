package com.kmecpp.osmium.api.database.api.filter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.kmecpp.osmium.api.database.DBUtil;

public abstract class AbstractFilter {

	protected final ArrayList<CompleteFilter> filters; //Ex: column>=, AND
	protected FilterNode head = null; //Needed to support chaining for complex filters such as WHERE ... AND(... OR ...) OR ...

	public AbstractFilter(String filter, Object value) {
		validateFilter(filter);
		filter = filter.trim();
		char lastChar = filter.charAt(filter.length() - 1);
		if (lastChar != '=' && lastChar != '>' && lastChar != '<') {
			throw new IllegalArgumentException("Invalid database filter: '" + filter + "'");
		}

		this.filters = new ArrayList<>(Arrays.asList(new CompleteFilter(null, filter, value)));
	}

	public AbstractFilter(String function, String filter, Object value) {
		validateFilter(filter);
		filter = filter.trim();
		String[] parts = parseFilter(filter);
		
		if (parts == null || parts.length != 2) {
			throw new IllegalArgumentException("Unable to parse filter " + filter + "!");
		}
		
		String joinedFilter = function + "(" + parts[0] + ")" + parts[1];
		this.filters = new ArrayList<>(Arrays.asList(new CompleteFilter(null, joinedFilter, value)));
	}

	public AbstractFilter(AbstractFilter filter) {
		this.filters = filter.getCompleteFilters();
		this.head = filter.getHead();
	}

	/*
	 * Validate provided filter is not null.
	 */
	protected final void validateFilter(String filter) {
		if (filter == null) {
			throw new IllegalArgumentException("Database filter cannot be null!");
		} else if (filter.isEmpty()) {
			throw new IllegalArgumentException("Database filter cannot be empty!");
		}
	}

	/*
	 * Split the string on the first non alpha character to extract the operator from the provided filter string.
	 */
	protected final String[] parseFilter(String filter) {
		for (int i = 0; i < filter.length(); i++) {
			char c = filter.charAt(i);
			if (c != 95 && c != 54 && (c < 65 || (c > 90 && c < 97) || c > 122)) { //Find non alpha excluding _ and - since they are commonly used in sql column names
				return filter.split(String.valueOf(c));
			}
		}

		return null;
	}

	public final StringBuilder createParameterizedStatement(StringBuilder sb) {
		if (!filters.isEmpty()) { //Build internal filter parameterized statement string first
			sb.append(filters.size() > 1 ? "(" : "");
			for (int i = 0; i < filters.size(); ++i) {
				CompleteFilter filter = filters.get(i);
				sb.append((i > 0 ? " " + filter.getOperator() + " " : " ") + filter.getFilterString() + " ?");
			}
			sb.append(filters.size() > 1 ? ")" : "");
		}

		if (this.hasNext()) { //Now move on to head Node if there is one
			sb.append(" " + head.getOperator() + "");
			head.filter.createParameterizedStatement(sb);
		}

		return sb;
	}

	public final int size() {
		return filters.size();
	}

	public final void addCompleteFilter(CompleteFilter completeFilter) {
		filters.add(completeFilter);
	}

	public final ArrayList<CompleteFilter> getCompleteFilters() {
		return filters;
	}

	public final CompleteFilter getCompleteFilter(int index) {
		return filters.get(index);
	}

	@Deprecated
	public final ArrayList<String> getFilters() { //Should phase this out in favor of the new getCompleteFilters
		return filters.stream().map(CompleteFilter::getFilterString).collect(Collectors.toCollection(ArrayList::new));
	}

	public final Object getValue(int filterIndex) {
		return filters.get(filterIndex).getFilterObject();
	}

	public final int link(PreparedStatement ps) throws SQLException {
		return link(ps, 0);
	}

	public final int link(PreparedStatement ps, int psIndex) throws SQLException {
		for (CompleteFilter filter : filters) {
			DBUtil.updatePreparedStatement(ps, psIndex + 1, filter.getFilterObject());
			psIndex += 1;
		}

		if (hasNext()) { //Continue linking all filter nodes down the chain
			psIndex = head.getFilter().link(ps, psIndex);
		}

		return psIndex;
	}

	public final boolean hasNext() {
		return this.head != null;
	}

	public final FilterNode getHead() {
		return this.head;
	}

	public final void setHead(@Nonnull FilterOperator operator, @Nullable AbstractFilter filter) {
		this.head = new FilterNode(operator, filter);
	}

	public static enum FilterOperator {

		AND,
		OR,
		NOT,

	}

	public static class FilterNode {

		private final FilterOperator operator;
		private final AbstractFilter filter;

		public FilterNode(FilterOperator operator, AbstractFilter filter) {
			this.operator = operator;
			this.filter = filter;
		}

		public FilterOperator getOperator() {
			return operator;
		}

		public AbstractFilter getFilter() {
			return filter;
		}

	}

	/*
	 * Package all contents needed for a filter in 1 class so we don't need to make multiple ArrayList::get calls
	 */
	public static class CompleteFilter {

		private final String filterString;
		private final Object filterObject;
		private final FilterOperator operator;

		public CompleteFilter(FilterOperator operator, String filterString, Object filterObject) {
			this.filterString = filterString;
			this.filterObject = filterObject;
			this.operator = operator;
		}

		public String getFilterString() {
			return filterString;
		}

		public Object getFilterObject() {
			return filterObject;
		}

		public FilterOperator getOperator() {
			return operator;
		}

	}

}
