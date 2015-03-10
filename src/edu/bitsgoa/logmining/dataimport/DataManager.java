package edu.bitsgoa.logmining.dataimport;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import edu.bitsgoa.logmining.utils.PropertyManager;

/**
 * Database Handler for project
 * @author denis_jose
 * @version benchmark 13-10-2012
 */
public class DataManager {
	private static final Logger LOGGER= PropertyManager.getLogger();
	private static Hashtable<String,Connection> _connPool=null;
	/**
	 * Connector for sql connectivity
	 * @param dbName =database name to connect to
	 * @throws SQLException
	 */
	public Connection getConnection(String dbName) throws SQLException {
		String dbstring = null; 
		Properties prop = new Properties();
		prop.put("user", PropertyManager.getDBUserName());
		prop.put("password", PropertyManager.getDBPwd());
		prop.put("autoReconnect", true);
	
		try {
			if (_connPool == null) 
				_connPool = new Hashtable<String,Connection>();
			Connection conn = _connPool.get(dbName);
			if (conn == null || conn.isClosed()) {
				Class.forName(PropertyManager.getSQLClass());
				dbstring="jdbc:mysql://"+PropertyManager.getDBHost()+"/" + dbName+"?autoReconnect=true";
				conn = DriverManager.getConnection(dbstring, PropertyManager.getDBUserName(), PropertyManager.getDBPwd());
//				conn = DriverManager.getConnection(dbstring, prop);

				if (_connPool.get(dbName)==null)
					_connPool.put(dbName, conn);
			}
			return conn;
		} catch (ClassNotFoundException e) {
			LOGGER.severe("Can't load JDBC Driver class" + PropertyManager.getSQLClass() + ". Failed to connect " + 
		dbstring + ". Exiting..");
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}
	
	public void closeConnection(Connection conn) {
		try {
			Set<Entry<String, Connection>> enteries= _connPool.entrySet();
			for (Entry<String, Connection> e : enteries) {
				if (e.getValue()== conn) {
					String key= e.getKey();
					_connPool.remove(key);
					break;
				}
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			LOGGER.severe("Can't close database connection" + conn.toString()+ ". Error message:" + e.getMessage());
		}
	}
	public long getTableRowCount(Connection conn, String tableName, String whereClause) {
		long count=0;
		Statement stmt=null;
		ResultSet results=null;
		String sql= "SELECT count(*) FROM " + tableName + " " + whereClause;
		try {
			stmt= conn.createStatement();
			results = stmt.executeQuery(sql);
			results.next();
			count= results.getLong(1);
			
		} catch (SQLException e) {
			LOGGER.severe(sql + "::: to count table entries fails, error " + e.getMessage());
		} finally {
			try {
				if (stmt!= null) stmt.close();
				if (results != null) results.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return count;
	}

	/**
	 * Executes a group of queries and displays result
	 * @param queries
	 */
	public void runQueries(Connection conn, String[] queries) {
		for (String command : queries) {
			try {
				Statement s = conn.createStatement();
				s.execute(command);
				s.close();
				//_conn.commit();
			} catch (SQLException e) {
				e.printStackTrace();
				LOGGER.severe(e.getMessage());
				LOGGER.severe("Failed to execute '"+command+"'");
			}
		}
	}

	/**
	 * Executes a query and return resultset
	 * @param query
	 * @return
	 */
	public static ResultSet runResultQuery(Connection conn, String query) {

		ResultSet rs = null;
		try {
			Statement s = conn.createStatement();
			rs = s.executeQuery(query);
			LOGGER.info("Executed '"+query+"'");
			//s.close(); dont close this, or you cant use rs
			//_conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			LOGGER.severe(e.getMessage());
			LOGGER.severe("Failed to execute '"+query+"'");
		}
		return rs;
	}
}