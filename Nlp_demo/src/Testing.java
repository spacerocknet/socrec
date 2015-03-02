import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.database.CassandraConnect;
import com.fb.FbDataCollector;
import com.fb.FbUser;
import com.fb.PageConnections;
import com.restfb.Parameter;
import com.restfb.json.JsonObject;
import com.restfb.types.User;

import facebook4j.FacebookException;
import facebook4j.Post;
import facebook4j.ResponseList;


public class Testing {

	public static void main(String[] args) {
		
		//connect database
		System.out.println("Connect Cassandra database...");
		CassandraConnect client = new CassandraConnect();
		client.connect("127.0.0.1");
		client.createSchema();
		//
		
		System.out.println("Get facebook user profile by given user access token...");
		String userId = "";
		String access_token = "CAACEdEose0cBAJRHggF0Fov3oBZAIavRSJjZAQTj1VUMKWwtlGmg37RXuD2hEElzknfQYNZAq4SdGUqsc7WaPELoZCZAv3mNLBQZBZA45WJZBKk24L4ChNDeh5nV1oZAuzAQZCGtuzVbPZCZBeNjdnA4SzwqHHK5b1rsLBUVGPxhqwLh3t18SSPeGlUbaTBm9YBa7QLdWeKbEXS8z99ZAyZAIvsc6AWoJ5mUoIGl4ZD";
		FbUser user = new FbUser(userId, access_token);
		FbDataCollector dataCollector = new FbDataCollector(user);
		HashMap<String, String>friends_movie=dataCollector.retrieveUserProfile("movies");
	
		//Save data to cassandra
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
        
        //
        System.out.println("Done");
		client.close();

	}

}
