/**
 * Datasource接口定义了与数据源进行交互的方法
 * 它提供了获取数据库连接、获取数据库元数据、表元数据、列元数据和索引元数据等功能
 */
package com.drizzlepal.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.drizzlepal.jdbc.exception.ConnectionOperationException;
import com.drizzlepal.jdbc.exception.UnknowDatabaseException;
import com.drizzlepal.jdbc.metadata.ColumnMetaData;
import com.drizzlepal.jdbc.metadata.DatabaseMetaData;
import com.drizzlepal.jdbc.metadata.IndexMetaData;
import com.drizzlepal.jdbc.metadata.TableMetaData;
import com.drizzlepal.utils.functions.ConsumerThrowable;

/**
 * Datasource接口定义了与数据源进行交互的方法
 * 它提供了获取数据库连接、获取数据库元数据、表元数据、列元数据和索引元数据等功能
 */
public interface DataSource extends AutoCloseable {

	/**
	 * 获取数据库连接
	 * 
	 * @return Connection对象，用于与数据库建立连接
	 * @throws SQLException 如果获取连接时发生SQL异常
	 */
	Connection getConnection() throws SQLException;

	/**
	 * 使用数据库连接执行给定的操作
	 * 
	 * @param thingsToDo 消费者函数，用于在数据库连接上执行操作
	 * @throws ConnectionOperationException 如果执行操作时发生异常
	 */
	void doWithConnection(ConsumerThrowable<Connection> thingsToDo) throws ConnectionOperationException;

	/**
	 * 获取数据库元数据
	 * 
	 * @return DatabaseMetaData对象，包含数据库的元数据信息
	 * @throws UnknowDatabaseException 如果数据库未知
	 * @throws SQLException            如果获取元数据时发生SQL异常
	 */
	DatabaseMetaData getMetaData() throws UnknowDatabaseException, SQLException;

	/**
	 * 根据指定的schema和数据库名获取数据库元数据
	 * 
	 * @param schema       数据库模式
	 * @param databaseName 数据库名称
	 * @return DatabaseMetaData对象，包含指定数据库的元数据信息
	 * @throws SQLException 如果获取元数据时发生SQL异常
	 */
	DatabaseMetaData getMetaData(String schema, String databaseName) throws SQLException;

	/**
	 * 根据表名获取表元数据
	 * 
	 * @param tableName 表名称
	 * @return TableMetaData对象，包含指定表的元数据信息
	 * @throws UnknowDatabaseException 如果数据库未知
	 * @throws SQLException            如果获取元数据时发生SQL异常
	 */
	TableMetaData getTableMetaData(String tableName) throws UnknowDatabaseException, SQLException;

	/**
	 * 根据指定的schema、数据库名和表名获取表元数据
	 * 
	 * @param schema       数据库模式
	 * @param databaseName 数据库名称
	 * @param tableName    表名称
	 * @return TableMetaData对象，包含指定表的元数据信息
	 * @throws SQLException 如果获取元数据时发生SQL异常
	 */
	TableMetaData getTableMetaData(String schema, String databaseName, String tableName) throws SQLException;

	/**
	 * 根据表名和列名获取列元数据
	 * 
	 * @param tableName  表名称
	 * @param columnName 列名称
	 * @return ColumnMetaData对象，包含指定列的元数据信息
	 * @throws UnknowDatabaseException 如果数据库未知
	 * @throws SQLException            如果获取元数据时发生SQL异常
	 */
	ColumnMetaData getColumnMetaData(String tableName, String columnName)
			throws UnknowDatabaseException, SQLException;

	/**
	 * 根据指定的schema、数据库名、表名和列名获取列元数据
	 * 
	 * @param schema       数据库模式
	 * @param databaseName 数据库名称
	 * @param tableName    表名称
	 * @param columnName   列名称
	 * @return ColumnMetaData对象，包含指定列的元数据信息
	 * @throws SQLException 如果获取元数据时发生SQL异常
	 */
	ColumnMetaData getColumnMetaData(String schema, String databaseName, String tableName, String columnName)
			throws SQLException;

	/**
	 * 根据表名和是否唯一的标志获取索引元数据
	 * 
	 * @param tableName 表名称
	 * @param unique    索引是否唯一的标志
	 * @return 包含IndexMetaData对象的列表，每个对象代表一个索引的元数据信息
	 * @throws UnknowDatabaseException 如果数据库未知
	 * @throws SQLException            如果获取元数据时发生SQL异常
	 */
	List<IndexMetaData> getIndexMetaData(String tableName, boolean unique)
			throws UnknowDatabaseException, SQLException;

	/**
	 * 根据指定的schema、数据库名、表名和是否唯一的标志获取索引元数据
	 * 
	 * @param schema       数据库模式
	 * @param databaseName 数据库名称
	 * @param tableName    表名称
	 * @param unique       索引是否唯一的标志
	 * @return 包含IndexMetaData对象的列表，每个对象代表一个索引的元数据信息
	 * @throws SQLException 如果获取元数据时发生SQL异常
	 */
	List<IndexMetaData> getIndexMetaData(String schema, String databaseName, String tableName, boolean unique)
			throws SQLException;

	/**
	 * 获取所有数据库名称的列表
	 * 
	 * @return 包含所有数据库名称的列表
	 * @throws UnknowDatabaseException 如果数据库未知
	 * @throws SQLException            如果获取数据库名称时发生SQL异常
	 */
	List<String> getDatabaseNames() throws UnknowDatabaseException, SQLException;

	/**
	 * 根据数据库名获取所有表名称的列表
	 * 
	 * @param databaseName 数据库名称
	 * @return 包含指定数据库中所有表名称的列表
	 * @throws UnknowDatabaseException 如果数据库未知
	 * @throws SQLException            如果获取表名称时发生SQL异常
	 */
	List<String> getTableNames(String databaseName) throws UnknowDatabaseException, SQLException;

	/**
	 * 根据结果集获取元数据列标签的列表
	 * 
	 * @param resultSet ResultSet对象，包含查询结果
	 * @return 包含结果集元数据列标签的列表
	 * @throws SQLException 如果获取元数据时发生SQL异常
	 */
	List<String> getResultSetMetaDataColumnLabels(ResultSet resultSet) throws SQLException;

	/**
	 * 根据表名获取默认列元数据列标签的列表
	 * 
	 * @param tableName 表名称
	 * @return 包含默认列元数据列标签的列表
	 * @throws UnknowDatabaseException 如果数据库未知
	 * @throws SQLException            如果获取元数据时发生SQL异常
	 */
	List<String> getDefaultColumnsResultSetMetaDataColumnLabels(String tableName)
			throws UnknowDatabaseException, SQLException;

	/**
	 * 根据指定的schema、数据库名和表名获取默认列元数据列标签的列表
	 * 
	 * @param schema       数据库模式
	 * @param databaseName 数据库名称
	 * @param tableName    表名称
	 * @return 包含默认列元数据列标签的列表
	 * @throws SQLException 如果获取元数据时发生SQL异常
	 */
	List<String> getDefaultColumnsResultSetMetaDataColumnLabels(String schema, String databaseName,
			String tableName)
			throws SQLException;

	/**
	 * 根据表名获取默认索引元数据列标签的列表
	 * 
	 * @param tableName 表名称
	 * @return 包含默认索引元数据列标签的列表
	 * @throws UnknowDatabaseException 如果数据库未知
	 * @throws SQLException            如果获取元数据时发生SQL异常
	 */
	List<String> getDefaultIndexesResultSetMetaDataColumnLabels(String tableName)
			throws UnknowDatabaseException, SQLException;

	/**
	 * 根据指定的schema、数据库名和表名获取默认索引元数据列标签的列表
	 * 
	 * @param schema       数据库模式
	 * @param databaseName 数据库名称
	 * @param tableName    表名称
	 * @return 包含默认索引元数据列标签的列表
	 * @throws SQLException 如果获取元数据时发生SQL异常
	 */
	List<String> getDefaultIndexesResultSetMetaDataColumnLabels(String schema, String databaseName,
			String tableName)
			throws SQLException;

	/**
	 * 根据表名获取默认表元数据列标签的列表
	 * 
	 * @param tableName 表名称
	 * @return 包含默认表元数据列标签的列表
	 * @throws UnknowDatabaseException 如果数据库未知
	 * @throws SQLException            如果获取元数据时发生SQL异常
	 */
	List<String> getDefaultTableResultSetMetaDataColumnLabels(String tableName)
			throws UnknowDatabaseException, SQLException;

	/**
	 * 根据指定的schema、数据库名和表名获取默认表元数据列标签的列表
	 * 
	 * @param schema       数据库模式
	 * @param databaseName 数据库名称
	 * @param tableName    表名称
	 * @return 包含默认表元数据列标签的列表
	 * @throws SQLException 如果获取元数据时发生SQL异常
	 */
	List<String> getDefaultTableResultSetMetaDataColumnLabels(String schema, String databaseName, String tableName)
			throws SQLException;

}