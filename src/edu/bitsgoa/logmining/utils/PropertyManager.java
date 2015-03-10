package edu.bitsgoa.logmining.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.XMLFormatter;


/*
 * This is the only property handler class that will be used
 * Other classes will be retired
 */
public class PropertyManager {
	private static Properties _prop = null;
	private static Logger _LOGGER= null; 
	static {
		try {
			loadProperties();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static final String getDBHost() {
		String host = _prop.getProperty("db.host");
		String port = _prop.getProperty("db.port");
		if (host == null)
			host = "localhost";
		if (port != null)
			host += ":"+port;
		else 
			host += ":3306";
		return host;
	}
	public static final String getSQLClass() {
		String clsname= _prop.getProperty("db.mysqlclass");
		if(clsname == null)
			clsname =  "com.mysql.jdbc.Driver";
		return clsname;
	}
	public static final String getDBUserName() {
		return _prop.getProperty("db.username");
	}
	public static final String getDBPwd() {
		return _prop.getProperty("db.pwd");
	}
	public static final String getMasterScriptFile() {
		return _prop.getProperty("db.scriptfile");
	}
	public static final String getMasterDBName() {
		return _prop.getProperty("db.masterdbname");
	}
	public static final int getDBInsertBatchCount() {
		return Integer.parseInt(_prop.getProperty("db.batchcount"));
	}
	public static final String getMasterMetaDataLocation() {
		return _prop.getProperty("master.datalocation");
	}

	public static final String getLogDateformat(String clientName) {
		return _prop.getProperty("log.dateformat");
	}
	public static final String getLogTimeUnit(String clientName) {
		return _prop.getProperty("log.timeunit");
	}
	public static final String getLogTimezone(String clientName) {
		return _prop.getProperty("log.timezone");
	}

public static final String getImportQueryFileName() {
	return _prop.getProperty("log.importqueryfile");
}
public static final String getLogName() {
	return _prop.getProperty("log.name");
}


	/**
	 * Initializes Logger to filename specified in applog.filename property of icirrus.properties
	 * @return
	 */
	static private FileHandler fileXML=null;
	static private Formatter formatterXML=null;


	public static final Logger getLogger() {
		if (_LOGGER==null) {
			String name="out"+File.separator+"log";
			_LOGGER= Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
			_LOGGER.setLevel(Level.ALL);
			try {
				fileXML = new FileHandler(name,1024000,1000,true);
			}catch(Exception e){
				e.printStackTrace();
			}
			formatterXML=new XMLFormatter(){
				@Override
				public String getHead(Handler h) {
					return "";
				}
			};
			fileXML.setFormatter(formatterXML);
			_LOGGER.addHandler(fileXML);
		}
		return _LOGGER;
	}

	private static final void loadProperties() throws IOException {
		_prop= new Properties();
		String fileName = "config"+File.separator+"loganalysis.properties";
		InputStream is = new FileInputStream(fileName);
		_prop.load(is);
	}

}
