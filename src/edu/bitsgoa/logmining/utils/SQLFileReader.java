package edu.bitsgoa.logmining.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class SQLFileReader {
	private static ArrayList<String> _listOfQueries = null;

	/*
	 * @param path Path to the SQL file
	 * 
	 * @return List of query strings
	 */
	/**
	 * parses the .sql file line by line and returns a list of queries for
	 * execution
	 * 
	 * @param fileName
	 *            = path to sql file
	 * @return
	 */
	public static ArrayList<String> createQueries(String fileName)
			throws IOException {
		String queryLine = new String();
		StringBuffer sBuffer = new StringBuffer();
		_listOfQueries = new ArrayList<String>();
		try {
			FileReader fr = new FileReader(new File(fileName));
			BufferedReader br = new BufferedReader(fr);

			// read the SQL file line by line
			while ((queryLine = br.readLine()) != null) {
				// ignore comments beginning with #
				int indexOfCommentSign = queryLine.indexOf('#');
				if (indexOfCommentSign != -1) {
					if (queryLine.startsWith("#")) {
						queryLine = new String("");
					} else
						queryLine = new String(queryLine.substring(0,
								indexOfCommentSign - 1));
				}
				// ignore comments beginning with --
				indexOfCommentSign = queryLine.indexOf("--");
				if (indexOfCommentSign != -1) {
					if (queryLine.startsWith("--")) {
						queryLine = new String("");
					} else
						queryLine = new String(queryLine.substring(0,
								indexOfCommentSign - 1));
				}
				// ignore comments surrounded by /* */
				indexOfCommentSign = queryLine.indexOf("/*");
				if (indexOfCommentSign != -1) {
					if (queryLine.startsWith("#")) {
						queryLine = new String("");
					} else
						queryLine = new String(queryLine.substring(0,
								indexOfCommentSign - 1));

					sBuffer.append(queryLine + " ");
					// ignore all characters within the comment
					do {
						queryLine = br.readLine();
					} while (queryLine != null && !queryLine.contains("*/"));
					indexOfCommentSign = queryLine.indexOf("*/");
					if (indexOfCommentSign != -1) {
						if (queryLine.endsWith("*/")) {
							queryLine = new String("");
						} else
							queryLine = new String(queryLine.substring(
									indexOfCommentSign + 2,
									queryLine.length() - 1));
					}
				}

				// the + " " is necessary, because otherwise the content before
				// and after a line break are concatenated
				// like e.g. a.xyz FROM becomes a.xyzFROM otherwise and can not
				// be executed
				if (queryLine != null)
					sBuffer.append(queryLine + " ");
			}
			br.close();

			// here is our splitter ! We use ";" as a delimiter for each request
			String[] splittedQueries = sBuffer.toString().split(";");

			// filter out empty statements
			for (int i = 0; i < splittedQueries.length; i++) {
				if (!splittedQueries[i].trim().equals("")
						&& !splittedQueries[i].trim().equals("\t")) {
					_listOfQueries.add(new String(splittedQueries[i]));
				}
			}
			return _listOfQueries;
		} catch (IOException e) {
			throw new IOException("Error at:" + sBuffer.toString(), e);
		}
	}

}
