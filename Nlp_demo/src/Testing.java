
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import com.database.CassandraConnect;
import com.datastax.driver.core.Row;
import com.fb.FbDataCollector;
import com.fb.FbUser;
import com.nlp.Entity;



public class Testing {

	public static void main(String[] args) throws FileNotFoundException, XPathExpressionException, IOException, SAXException, ParserConfigurationException {
		//test through get data from facebook -> save to database -> load data -> entity extraction
		test_nlp_userMovie();
		//test through load data from database -> entity extraction
		test_nlp_userStatus();
		
		
	}
	public static void test_nlp_userMovie() throws FileNotFoundException, IOException, XPathExpressionException, SAXException, ParserConfigurationException{
		//connect database
				System.out.println("Connect Cassandra database...");
				CassandraConnect client = new CassandraConnect();
				client.connect("127.0.0.1");
				client.createSchema();
				
				//get user favorite movies
				
				System.out.println("Get facebook user profile by given user access token...");
				String userId = "";
				String access_token = "CAACEdEose0cBADKLJigB0rrKlSdrCz3ir6NgMfZBVpBp7b9507WLnOZBqplx9CSFWWbEjRaTs05bVot8KdwzAewkt2vBPG762dPXGFZAe90w1hbU0hAXny7M3NgZBxqk0qQHLOL7KV8MFhJIs0GxQ4kPtV53eRPniZBXv4xDk7ObQ32uZCNmTei5iQY0opjolDZCv9EpjBnGVmCis5IZBUxHowwwTLJyB9aDnOnSg1Y4IAZDZD";
				FbUser user = new FbUser(userId, access_token);
				FbDataCollector dataCollector = new FbDataCollector(user);
				HashMap<String, String>friends_movie=dataCollector.retrieveUserProfile("movies");
			
				//Save data to cassandra table movies
				System.out.println("Save data to cassandra...");
				Set mapSet = (Set) friends_movie.entrySet();
		        Iterator mapIterator = mapSet.iterator();
		        while (mapIterator.hasNext()) 
		        {
		        
		        	Map.Entry mapEntry = (Map.Entry) mapIterator.next();
		        	String username = (String) mapEntry.getKey();
		        	String movieset = (String) mapEntry.getValue();
		        	client.insertMovieData(UUID.randomUUID().toString(), username, movieset);
		        }
		        
		        
		        
		        //now get data from table movies and use nlp to get entity
		        
		        List<Row> rows= client.getUserFavoriteMovie("Tina Trang").all();
		        StringBuilder textbuilder= new StringBuilder();
		        if(rows.size()==1){
		        	textbuilder.append(rows.get(0).getString(0));
		        }
		        else{
		        	for(int i=0;i<rows.size();i++){
		        		//System.out.println(rows.get(i).getSet("movietitle", String.class).toString());
		            	textbuilder.append(rows.get(i).getSet(0, String.class).toString());
		            }
		        }
		        
		        String contentText=textbuilder.toString();
		        contentText=contentText.replaceAll("'", "");
		    	contentText= contentText.replaceAll("\\[", "");
		    	contentText= contentText.replaceAll("]", ". ");
		     Entity en= new Entity();
		     
		     //keywords
		     List<String>keywords=en.keywordList(contentText);
		     
		     //entity and its type
		     HashMap<String, ArrayList<String>>entities =en.entityList(contentText);
		     
		     System.out.println("DONE");
			client.close();
	}
	public static void test_nlp_userStatus() throws FileNotFoundException, IOException, XPathExpressionException, SAXException, ParserConfigurationException{
		//connect database
				System.out.println("Connect Cassandra database...");
				CassandraConnect client = new CassandraConnect();
				client.connect("127.0.0.1");
				client.createSchema();
		//insert fake data to status table
				client.insertFakeDatatoStatusTable();
				
				 //now get data from table status and use nlp to get entity
		        //username: Peter/Obama/Nguyen Van A
			        List<Row>rows= client.getUserStatus("Peter").all();
			        StringBuilder textbuilder= new StringBuilder();
			        if(rows.size()==1){
			        	textbuilder.append(rows.get(0).getString(0));
			        }
			        else{
			        	for(int i=0;i<rows.size();i++){
			        		//System.out.println(rows.get(i).getSet("movietitle", String.class).toString());
			            	textbuilder.append(rows.get(i).getString("message"));
			            }
			        }
			        
			        String contentText=textbuilder.toString();
			        contentText=contentText.replaceAll("'", "");
			    	contentText= contentText.replaceAll("\\[", "");
			    	contentText= contentText.replaceAll("]", ". ");
			     Entity en= new Entity();
			     
			     //keywords
			     List<String>keywords=en.keywordList(contentText);
			     
			     //entity and its type
			     HashMap<String, ArrayList<String>>entities =en.entityList(contentText);
			     System.out.println("DONE");
				client.close();
		
	}

}
