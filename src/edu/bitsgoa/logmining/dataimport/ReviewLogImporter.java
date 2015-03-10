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

import com.csvreader.CsvReader;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import edu.bitsgoa.logmining.utils.PropertyManager;
import edu.bitsgoa.logmining.utils.SQLFileReader;

public class ReviewLogImporter extends AbstractLogImporter {

	JSONObject message;
	JSONParser jsonParser = new JSONParser();
	JSONObject jsonObject;
	Gson gson = new Gson();
	JSONObject innerObj1;
	JSONArray lang2,lang3;
	Iterator itr1,q,j,k;
	InputStream in;
	JsonReader infile;
	int y=0,z=0,i=0;
	String s="";
	
	protected ReviewLogImporter(Connection c) {
		super(c);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void initialize() throws Exception {
		ArrayList<String> insertqueries = SQLFileReader
				.createQueries(PropertyManager.getImportQueryFileName());
		insstmts= new PreparedStatement[insertqueries.size()];
//		insstmts= new PreparedStatement[4];
		for (String s : insertqueries)
		{
			insstmts[i] = conn.prepareStatement(s);
			i++;
		}
		
		/*insstmts[0]= conn.prepareStatement("insert ignore into person(owner_email,owner) values(?,?)");
		insstmts[1]=conn.prepareStatement("insert into review(issue,owner,description,subject,created,modified) values(?,?,?,?,?,?)");
		insstmts[2]=conn.prepareStatement("insert ignore into approval(issue,owner,closed,commit) values(?,?,?,?)");
		insstmts[3]=conn.prepareStatement("insert into comment(id,issue,sender,recipients,text,disapproval,date,approval) values(?,?,?,?,?,?,?,?)");
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
			insstmts[0].setString(1,(String)message.get("owner_email"));
			insstmts[0].setString(2,(String)message.get("owner"));
			insstmts[0].addBatch();
			
			jsonObject = (JSONObject) jsonParser.parse(""+message);

			lang2 = (JSONArray)jsonObject.get("reviewers");
			q = lang2.iterator();
			while(q.hasNext()){
				s=""+q.next();
				insstmts[0].setString(1,s);
				insstmts[0].setString(2,s);
				insstmts[0].addBatch();
				s="";
			}

			lang3 =(JSONArray)jsonObject.get("messages");
			itr1 = lang3.iterator();
			while(itr1.hasNext()){
				innerObj1 = (JSONObject) itr1.next();
				insstmts[0].setString(1,(String)innerObj1.get("sender"));
				insstmts[0].setString(2,(String)innerObj1.get("sender"));
				insstmts[0].addBatch();
			}
			insstmts[1].setDouble(1,(Double)message.get("issue"));
			insstmts[1].setString(2,(String)message.get("owner"));
			insstmts[1].setString(3,(String)message.get("description"));         
			insstmts[1].setString(4,(String)message.get("subject"));
			insstmts[1].setString(5,(String)message.get("created"));
			insstmts[1].setString(6,(String)message.get("modified"));
			insstmts[1].addBatch();
			
			insstmts[2].setDouble(1,(Double)message.get("issue"));
			insstmts[2].setString(2,(String)message.get("owner"));
			insstmts[2].setString(3,""+message.get("closed"));
			insstmts[2].setString(4,""+message.get("commit"));
			insstmts[2].addBatch();
			
			// jsonObject = (JSONObject) jsonParser.parse(""+message);

			lang2 =(JSONArray)jsonObject.get("messages");

			j = lang2.iterator();
			while(j.hasNext())
			{y++;
			innerObj1 = (JSONObject) j.next();
			lang3 =(JSONArray)innerObj1.get("recipients");

			k = lang3.iterator();
			while(k.hasNext()) {
				z++;
				if(z==1) s=s+(String)k.next();
				else s=s+", "+(String)k.next();
			}
			z=0;
			insstmts[3].setInt(1,y);
			insstmts[3].setDouble(2,(Double)jsonObject.get("issue"));
			insstmts[3].setString(3,(String)innerObj1.get("sender"));
			insstmts[3].setString(4,s);
			insstmts[3].setString(5,(String)innerObj1.get("text"));
			insstmts[3].setString(6,""+innerObj1.get("disapproval"));
			insstmts[3].setString(7,""+innerObj1.get("date"));
			insstmts[3].setString(8,""+innerObj1.get("approval"));
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
