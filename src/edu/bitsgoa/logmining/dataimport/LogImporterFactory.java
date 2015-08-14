package edu.bitsgoa.logmining.dataimport;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import edu.bitsgoa.logmining.utils.PropertyManager;


/**
 * Factory class to select type of data importer on pgm run. eg http log, cpu
 * log etc.
 * 
 * @author denis_jose
 * @version benchmark 13-10-12
 */
public class LogImporterFactory {
	public static AbstractLogImporter getLogFileImporter(Connection conn,
			String type, String clientName)
			throws IOException, SQLException {
		AbstractLogImporter dbimporter = null;
//		if (type.equalsIgnoreCase("hadoop_log")) {
//			dbimporter = new DatalogImporter(conn);
//		}
		if (type.equalsIgnoreCase("review_log")) {
			dbimporter = new ReviewLogImporter(conn);
		} 
		 if (type.equalsIgnoreCase("qt_log")) {
			dbimporter = new QtLogImporter(conn);
		}
		 if (type.equalsIgnoreCase("os_log")) {
				dbimporter = new OpenStackLogImporter(conn);
			}
		 if (type.equalsIgnoreCase("adr_log")) {
				dbimporter = new AndroidLogImporter(conn);
			}
		dbimporter.setClientName(clientName);
		dbimporter.setDateFormat(PropertyManager.getLogDateformat(clientName));
		try {
			dbimporter.initialize();
		} catch (Exception e) {
			throw new SQLException(e);
		}
		return dbimporter;
	}
}
