package edu.bitsgoa.logmining.dataimport;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import edu.bitsgoa.logmining.utils.PropertyManager;
import edu.bitsgoa.logmining.utils.SQLFileReader;

public class QtLogImporter extends AbstractLogImporter{
	JSONObject message;
	JSONParser jsonParser = new JSONParser();
	JSONObject jsonObject;
	Gson gson = new Gson();
	JSONObject innerObj1, innerObj2, innerObj3, innerObj4;
	JSONArray lang2,lang3;
	Iterator itr1,q,j,k;
	InputStream in;
	JsonReader infile;
	int y=0,z=0,i=0;
	String s="";
	int m=0;
	
	protected QtLogImporter(Connection c) {
		super(c);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void initialize() throws Exception {
		ArrayList<String> insertqueries = SQLFileReader
				.createQueries(PropertyManager.getImportQueryFileName());
		insstmts= new PreparedStatement[insertqueries.size()];
		for (String s : insertqueries)
		{
			insstmts[i] = conn.prepareStatement(s);
			i++;
		}
		//insstmts= new PreparedStatement[4];
		/*
		insstmts[0]= conn.prepareStatement("insert ignore into person(email,name,_account_id) values(?,?,?)");
		insstmts[1]=conn.prepareStatement("insert into review(change_id,owner,project,branch,subject,created,updated) values(?,?,?,?,?,?,?)");
		insstmts[2]=conn.prepareStatement("insert ignore into approval(change_id,owner,status,mergeable) values(?,?,?,?)");
		insstmts[3]=conn.prepareStatement("insert into messages(id,date,_revision_number,message,author) values(?,?,?,?,?)");
		*/
		
	}
	
	@Override
	protected void openLogFile() throws IOException {
		in = new FileInputStream(getLogFileName());
		infile = new JsonReader(new InputStreamReader(in, "UTF-8"));
		infile.beginArray();
	}
	
	@Override
	protected int readHeader() {
		return 0;
	}
	
	@Override
	protected void closeLogFile() throws IOException {
		((JsonReader)infile).close();
	}

	@Override
	protected int readLogAndPrepareRecords() throws SQLException,
	ParseException, IOException {
		// TODO Auto-generated method stub
		try {
			
			if(!infile.hasNext()) return -1;
			message = gson.fromJson(infile, JSONObject.class);
			jsonObject = (JSONObject) jsonParser.parse(""+message);
			innerObj1 = (JSONObject)jsonObject.get("owner");
			
			insstmts[0].setInt(1,++m);
			insstmts[0].setString(2,(String)innerObj1.get("email"));
			insstmts[0].setString(3,(String)innerObj1.get("name"));
			insstmts[0].setDouble(4,(Double)innerObj1.get("_account_id"));
			insstmts[0].addBatch();

			

			lang2 = (JSONArray)jsonObject.get("messages");
			q = lang2.iterator();
			while(q.hasNext()){
				innerObj2 = (JSONObject)q.next();
				innerObj3=(JSONObject)innerObj2.get("author");
				insstmts[0].setInt(1,++m);
				insstmts[0].setString(2,(String)innerObj3.get("email"));
				insstmts[0].setString(3,(String)innerObj3.get("name"));
				insstmts[0].setDouble(4,(Double)innerObj3.get("_account_id"));
				insstmts[0].addBatch();
			}

			
			insstmts[1].setString(1,(String)message.get("change_id"));
			insstmts[1].setString(2,(String)innerObj1.get("name"));
			insstmts[1].setString(3,(String)message.get("project"));         
			insstmts[1].setString(4,(String)message.get("branch"));
			insstmts[1].setString(5,(String)message.get("subject"));
			insstmts[1].setString(6,(String)message.get("created"));
			insstmts[1].setString(7,(String)message.get("updated"));
			insstmts[1].addBatch();

			insstmts[2].setString(1,(String)message.get("change_id"));
			insstmts[2].setString(2,(String)innerObj1.get("name"));
			insstmts[2].setString(3,(String)message.get("status"));
			insstmts[2].setString(4,""+message.get("mergeable"));
			insstmts[2].addBatch();

			// jsonObject = (JSONObject) jsonParser.parse(""+message);

			
			j = lang2.iterator();
			while(j.hasNext())
			{
			innerObj4 = (JSONObject) j.next();
			
			
			insstmts[3].setString(1,(String)innerObj4.get("id"));
			insstmts[3].setString(2,(String)innerObj4.get("date"));
			insstmts[3].setDouble(3,(Double)innerObj4.get("_revision_number"));
			insstmts[3].setString(4,(String)innerObj4.get("message"));
			insstmts[3].setString(5,(String)innerObj3.get("name"));
			insstmts[3].addBatch();
			}
		}
		catch(Exception e)
		{
			System.out.println("Query error : " + e.getMessage());
		}
		
		
		return 1;
	}

}

