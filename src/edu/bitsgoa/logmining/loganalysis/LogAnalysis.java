package edu.bitsgoa.logmining.loganalysis;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import edu.bitsgoa.logmining.utils.PropertyManager;
import edu.bitsgoa.logmining.utils.SQLFileReader;

public class LogAnalysis {
	
	PreparedStatement[] insstmts;
	int i;
	Connection conn;
	public LogAnalysis(Connection c) {
			conn = c;
		// TODO Auto-generated constructor stub
	}
	
	public void executeAnaysis() throws IOException, SQLException
	{
		ArrayList<String> insertqueries = SQLFileReader
				.createQueries(PropertyManager.getAnalysisQueryFileName());
		insstmts= new PreparedStatement[insertqueries.size()];
		for (String s : insertqueries)
		{
			insstmts[i] = conn.prepareStatement(s);
			insstmts[i].executeUpdate();
			i++;
		}
	}
	
	

}
