package edu.bitsgoa.logmining.driver;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;

import edu.bitsgoa.logmining.dataimport.AbstractLogImporter;
import edu.bitsgoa.logmining.dataimport.DataManager;
import edu.bitsgoa.logmining.dataimport.LogImporterFactory;
import edu.bitsgoa.logmining.utils.FileUtility;
import edu.bitsgoa.logmining.utils.PropertyManager;
import edu.bitsgoa.logmining.utils.SQLFileReader;

public class MainDriver {
	private DataManager _mgr =null;
	private Connection _conn=null;
	public static void main(String[] args) {
		MainDriver theDrv= new MainDriver();
		try {
			System.out.println(PropertyManager.getDBHost());
			theDrv.initializeSystem();
			theDrv.importLog();
		} catch (IOException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void initializeSystem() throws SQLException, IOException {
		_mgr= new DataManager();
		_conn= _mgr.getConnection(PropertyManager.getMasterDBName());
		ArrayList<String> createTablequeries = SQLFileReader
				.createQueries(PropertyManager.getMasterScriptFile());
		if (createTablequeries != null)
			for (String crTableSql : createTablequeries) {
				Statement st = _conn.createStatement();
				st.executeUpdate(crTableSql);
				st.close();
			}
	}
	private void importLog() throws IOException, SQLException {
		AbstractLogImporter importer= LogImporterFactory.getLogFileImporter(_conn, 
				PropertyManager.getLogName(),PropertyManager.getLogName());
		FileUtility fileutil = new FileUtility();
		fileutil.setFileExtns(null);
		int totalFiles = 0;
		int totalLines = 0;
		String location="." + File.separator + "input";
		HashSet<String> fileNames = new HashSet<String>();
		fileutil.getFilesWithExtn(location, true, fileNames);
		totalFiles += fileNames.size();
		for (String afile : fileNames) {
			importer.setLogFileName(afile);
			importer.importLog();
			totalLines += importer.getLogLineCount();
		}	
		_mgr.closeConnection(_conn);
	}
}
