
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

	public static void main(String[] args) throws XPathExpressionException, IOException, SAXException, ParserConfigurationException {
		
		//connect database
		System.out.println("Connect Cassandra database...");
		CassandraConnect client = new CassandraConnect();
		client.connect("127.0.0.1");
		client.createSchema();
		
		//get user favorite movies
		
		System.out.println("Get facebook user profile by given user access token...");
		String userId = "";
		String access_token = "CAACEdEose0cBAEOlOUDCvYczeTwXHmSOAmZBKU7BbZAdhhULhA4OU3PgnsmKGjZBSsLZBYHi8wIEaSjl59SZCGw2YT74ZANKbuYmFyrEFjTNfWRzoj3xhJJc5qHU6t1ZBBIupZC8Dw6p0T0NnfwrfH1yN4dsH3wKGfK06aqGYZArwUl8q1jivueV5bZAXC794ifh1aAosJNnyVyDcs0GiPSptZC4OnQKytVef8ZD";
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
        System.out.println(rows.get(0).getSet(0, String.class).toString());
        StringBuilder textbuilder= new StringBuilder();
        for(int i=0;i<rows.size();i++){
        	textbuilder.append(rows.get(i).getSet(0, String.class).toString());
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
	client.close();
		
		
		
	}

}
