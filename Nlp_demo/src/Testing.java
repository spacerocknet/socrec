import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.database.CassandraConnect;
import com.fb.FbDataCollector;
import com.fb.FbUser;

import facebook4j.FacebookException;
import facebook4j.Post;
import facebook4j.ResponseList;


public class Testing {

	public static void main(String[] args) {
		System.out.println("Connect Cassandra database...");
		CassandraConnect client = new CassandraConnect();
		client.connect("127.0.0.1");
		client.createSchema();
		//
		System.out.println("Get facebook user profile by given user access token...");
		String userId = "";
		String access_token = "CAACEdEose0cBALGmPrAWkBQmVhW2htmN5ZCLGGsKpmZBjocXES5ENd34zZAbZBgHnGWoszEGYofZCbg3ATuIYLjD0kO5DJlgAXsQYVSpqiF0mFS4M7Pvwu4FjzqUK0YNF8ERZBmOg9DAuYjzbSPZBdoA4ONtwDZAk7h9RZAzGRzFNetftTvzsFZA1dipIF58xlfZBgvekozL1Gvlf8ZAyAXniL1K40InQonvWUIZD";
		FbUser user = new FbUser(userId, access_token);
		FbDataCollector dataCollector = new FbDataCollector(user);
		dataCollector.retrieveUserProfile("movies");
		

	}

}
