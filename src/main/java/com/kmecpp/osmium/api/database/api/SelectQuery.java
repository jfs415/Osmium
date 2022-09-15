package com.kmecpp.osmium.api.database.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.kmecpp.osmium.api.database.SQLDatabase;
import com.kmecpp.osmium.api.database.TableData;
import com.kmecpp.osmium.api.database.api.SQLInterfaces.SelectInterfaces.SIBase;
import com.kmecpp.osmium.api.database.api.SQLInterfaces.SelectInterfaces.SIGroupBy;
import com.kmecpp.osmium.api.database.api.SQLInterfaces.SelectInterfaces.SIHaving;
import com.kmecpp.osmium.api.database.api.SQLInterfaces.SelectInterfaces.SILimit;
import com.kmecpp.osmium.api.database.api.SQLInterfaces.SelectInterfaces.SIOrderBy;
import com.kmecpp.osmium.api.database.api.SQLInterfaces.SelectInterfaces.SITerminal;
import com.kmecpp.osmium.api.database.api.SQLInterfaces.SelectInterfaces.SIWhere;
import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.util.ArrayUtil;
import com.kmecpp.osmium.api.util.IOUtil;
import com.kmecpp.osmium.api.util.StringUtil;

public class SelectQuery<T> implements SIBase<T> {

	private final SQLDatabase database;
	private final TableData tableData;

	private JoinClause join;
	private GroupBy groupBy;
	private OrderBy orderBy;
	private HavingClause having;
	
	private WhereClause where;
	private LimitClause limit;
	
	private final String[] columns;

	public SelectQuery(SQLDatabase database, Class<T> tableClass, String... columns) {
		this.database = database;
		this.tableData = database.getTable(tableClass);
		if (this.tableData == null) {
			throw new IllegalArgumentException("Missing table registration for " + tableClass.getName() + "! Is it annotated with @" + DBTable.class.getSimpleName() + "?");
		}
		this.columns = columns;
	}
	
	private String createSelectStatement() {
		return ArrayUtil.isNullOrEmpty(columns) ? "*" : StringUtil.join(columns, ",");
	}

	@Override
	public List<T> execute() {
		return transform(resultSet -> {
			try {
				ArrayList<T> result = new ArrayList<>();
				while (resultSet.next()) {
					result.add(this.database.parse(resultSet, tableData));
				}
				return result;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}

	@Override
	public <R> R transform(ResultSetTransformer<R> resultHandler) {
		String query = "SELECT " + createSelectStatement() + " FROM " + this.tableData.getName()
				+ (join != null ? join : "")
				+ (where != null ? where.getParameterizedStatement() : "")
				+ (groupBy != null ? groupBy : "")
				+ (having != null ? having.getParameterizedStatement() : "")
				+ (orderBy != null ? orderBy : "")
				+ (limit != null ? limit : "");

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			OsmiumLogger.debug("Executing query: \"" + query + "\"");
			connection = this.database.getConnection();
			statement = connection.prepareStatement(query);
			
			int psIndex = 0; //Keep track of ps index as we link filters to statement, otherwise will get SQLExceptions relating to not enough parameters linked to statement
			if (where != null) {
				psIndex = where.getWhereFilter().link(statement);
			}
			
			if (having != null) {
				having.getHavingFilter().link(statement, psIndex);
			}
			
			resultSet = statement.executeQuery();
			return resultHandler.process(resultSet);
		} catch (Exception e) {
			OsmiumLogger.error("Failed to execute database query: \"" + query + "\"");
			throw new RuntimeException(e);
		} finally {
			IOUtil.close(connection, statement, resultSet);
		}
	}

	@Override
	public SIWhere<T> join(JoinClause join) {
		this.join = join;
		return this;
	}

	@Override
	public SIGroupBy<T> where(WhereClause where) {
		this.where = where;
		return this;
	}

	@Override
	public SIHaving<T> groupBy(GroupBy groupBy) {
		this.groupBy = groupBy;
		return this;
	}

	@Override
	public SIOrderBy<T> having(HavingClause having) {
		this.having = having;
		return this;
	}

	@Override
	public SILimit<T> orderBy(OrderBy orderBy) {
		this.orderBy = orderBy;
		return this;
	}

	@Override
	public SITerminal<T> limit(int offset, int rowCount) {
		this.limit = new LimitClause(offset, rowCount);
		return this;
	}

}
