package edu.bitsgoa.logmining.dataimport;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.csvreader.CsvReader;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import edu.bitsgoa.logmining.utils.PropertyManager;

public abstract class AbstractLogImporter {
	protected static final Logger LOGGER = PropertyManager.getLogger();
	protected Connection conn;
	protected String logFileName;
	protected PreparedStatement[] insstmts;
	protected InputStream in;
	protected Object infile; // Should be overwritten
	protected String importerName = "";
	protected boolean isheader = true;
	protected int _numLines;

	private String _clientName;
	private int _numRec;
	private int _unsuccessfulRec;
	private int _successfulRec;
	private String _dateFormat;

	protected AbstractLogImporter(Connection c) {
		conn = c;
	}

	protected AbstractLogImporter(Connection c, String f) {
		conn = c;
		logFileName = f;
	}

	public final String getLogFileName() {
		return logFileName;
	}

	public final void setLogFileName(String l) {
		logFileName = l;
	}

	public final void setDateFormat(String d) {
		_dateFormat=d;
	}

	public final void setClientName(String n) {
		_clientName=n;
	}
	public final String getClientName() {
		return _clientName;
	}
	//	abstract protected boolean prepareRecord(StatementPointer insstP) throws SQLException, ParseException, IOException;
	abstract protected void initialize() throws Exception;
	abstract protected int readLogAndPrepareRecords() 
			throws SQLException, ParseException, IOException;
	/** Following 3 APIs need to be overwritten 
	 * @throws IOException **/
	protected void openLogFile() throws IOException {
		infile = new CsvReader(logFileName);
	}
	protected int readHeader() {
		boolean res = true;
		try {
			res = ((CsvReader)infile).readHeaders();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res == true ? 1 : 0;
	}

	protected void closeLogFile() throws IOException {
		((CsvReader)infile).close();
	}


	public void importLog() {
		try {
			_numRec = 0;
			_numLines = 0;
			_unsuccessfulRec = 0;
			_successfulRec = 0;
			openLogFile();
			int batchCount = 0;
			int linesRead = 0;
			
		/*	if (isheader) {
				linesRead = readHeader();
				_numLines += linesRead;
			}*/
			
			do {
				try {
					linesRead = readLogAndPrepareRecords();
					// one line can result in populating multiple tables
					_numLines += linesRead;
					batchCount++;
					_numRec+= insstmts.length;
					if (batchCount > PropertyManager.getDBInsertBatchCount()) {
						for (PreparedStatement ins: insstmts){
							updateUnsuccessfulRecord(ins.executeBatch());
						}
						batchCount = 0;
					}

				} catch (ParseException e) {
					if (e.getMessage().equalsIgnoreCase("comment")) {
					} else {
						LOGGER.logp(Level.WARNING, this.getClass().getName(), "importLog", "Error in Parsing " + e.getMessage());
					}

				} catch (BatchUpdateException e) {
					int[] success = e.getUpdateCounts();
					updateUnsuccessfulRecord(success);
				}
			} while (linesRead > 0); 
			// Last Batch
			for (PreparedStatement ins: insstmts) {
				updateUnsuccessfulRecord(ins.executeBatch());
				ins.close();

			}

		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), "importLog", "Log file open error. " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), "importLog", "Database insert error. " + e.getMessage());
		} finally {
			try {
				closeLogFile();
				//TODO move the file to archive
			} catch (IOException e) {
				e.printStackTrace();
				LOGGER.logp(Level.SEVERE, this.getClass().getName(), "importLog", "Log file close error. " + e.getMessage());
			}
		}
	}

	public final int getImportedRecCount() {
		return _successfulRec;
	}

	public final int getLogRecCount() {
		return _numRec;
	}

	public final int getLogLineCount() {
		return _numLines;
	}

	@Override
	public final String toString() {
		return importerName;
	}

	//TODO Read locale from config log
	protected void setDate(PreparedStatement insst, int pos, String dateStr)
			throws IOException {
		try {
			if (isNullValue(dateStr) == false) {
				SimpleDateFormat df = new SimpleDateFormat(_dateFormat);
				Date d = df.parse(dateStr);
				insst.setTimestamp(pos, new java.sql.Timestamp(d.getTime()));
			} else
				insst.setNull(pos, java.sql.Types.DATE);
		} catch (SQLException e) {
			e.printStackTrace();
			LOGGER.logp(Level.WARNING, this.getClass().getName(), "setDate", "Prepared Statement error. " + e.getMessage());
		} catch (ParseException e) {
			e.printStackTrace();
			LOGGER.logp(Level.WARNING, this.getClass().getName(), "setDate", "Unable to parse " + dateStr + ". " + e.getMessage());
		}
	}

	protected void setInt(PreparedStatement insst, int pos, String intStr) {
		try {
			if (isNullValue(intStr) == false) {
				insst.setInt(pos, Integer.valueOf(intStr));
			} else
				insst.setNull(pos, java.sql.Types.INTEGER);
		} catch (SQLException e) {
			e.printStackTrace();
			LOGGER.logp(Level.WARNING, this.getClass().getName(), "setInt", "Prepared Statement error. " + e.getMessage());
		}
	}

	protected void setLong(PreparedStatement insst, int pos, String longStr) {
		try {
			if (isNullValue(longStr) == false) {
				insst.setLong(pos, Long.valueOf(longStr));
			} else
				insst.setNull(pos, java.sql.Types.INTEGER);
		} catch (SQLException e) {
			e.printStackTrace();
			LOGGER.logp(Level.WARNING, this.getClass().getName(), "setLong", "Prepared Statement error. " + e.getMessage());
		}
	}

	protected void setFloat(PreparedStatement insst, int pos, String floatStr) {
		try {
			if (isNullValue(floatStr) == false) {
				insst.setFloat(pos, Float.valueOf(floatStr));
			} else
				insst.setNull(pos, java.sql.Types.FLOAT);
		} catch (SQLException e) {
			e.printStackTrace();
			LOGGER.logp(Level.WARNING, this.getClass().getName(), "setFloat", "Prepared Statement error. " + e.getMessage());
		}
	}
	protected void setDouble(PreparedStatement insst, int pos, String doubleStr) {
		try {
			if (isNullValue(doubleStr) == false) {
				insst.setDouble(pos, Double.valueOf(doubleStr));
			} else
				insst.setNull(pos, java.sql.Types.DOUBLE);
		} catch (SQLException e) {
			e.printStackTrace();
			LOGGER.logp(Level.WARNING, this.getClass().getName(), "setDouble", "Prepared Statement error. " + e.getMessage());
		}
	} 

	protected void setString(PreparedStatement insst, int pos, String str) {
		try {
			if (isNullValue(str) == false) {
				insst.setString(pos, str);
			} else
				insst.setString(pos, null);
		} catch (SQLException e) {
			e.printStackTrace();
			LOGGER.logp(Level.WARNING, this.getClass().getName(), "setString", "Prepared Statement error. " + e.getMessage());
		}
	}


	//TODO Read locale from config log
	protected Long convertDateToTimeInSec(String dateStr) {
		Date d = null;
		try {
			SimpleDateFormat df = new SimpleDateFormat(	_dateFormat, Locale.US);
			d = df.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
			LOGGER.logp(Level.WARNING, this.getClass().getName(), "convertDateToTimeInSec", "Prepared Statement error. " + e.getMessage());
		}
		return d.getTime() / 1000;
	}

	static private boolean isNullValue(String str) {
		if (str == null || str.equals("")
				|| str.trim().equalsIgnoreCase("null"))
			return true;
		else
			return false;
	}

	private void updateUnsuccessfulRecord(int[] executedRec) {
		int count = 0;
		for (int i = 0; i < executedRec.length; i++) {
			if (executedRec[i] >= 0
					|| executedRec[i] == Statement.SUCCESS_NO_INFO) {
				count++;
			} else if (executedRec[i] == Statement.EXECUTE_FAILED) {
				LOGGER.logp(Level.WARNING, this.getClass().getName(), "updateUnsuccessfulRecord", "Unsuccessful record id:"
						+ (_successfulRec + i + 1));
			}
		}
		_unsuccessfulRec += (executedRec.length - count);
		_successfulRec += count;
		if ((executedRec.length - count) > 0)
			LOGGER.logp(Level.WARNING, this.getClass().getName(), "updateUnsuccessfulRecord", "Total unsuccessful records:"
					+ (executedRec.length - count));
	}
}
