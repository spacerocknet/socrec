package com.fb;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.DefaultJsonMapper;
import com.restfb.JsonMapper;
import com.restfb.Parameter;
import com.restfb.json.JsonObject;
import com.restfb.types.PageConnection;
import com.restfb.types.User;

public class FbDataCollector {
	
	static String APP_SECRET = "27d0624c558d59dc098145b332314ec8" ;
	static String APP_ID = "881677971854230" ;
	private FbUser mainUser;
	public FbDataCollector(FbUser user) {
		
		setMainUser(user);
	}
	private FbUser getMainUser() {
		return mainUser;
	}
	private void setMainUser(FbUser mainUser) {
		this.mainUser = mainUser;
	}
public HashMap<String, String> retrieveUserProfile(String object){
		
		
		DefaultFacebookClient fbClient = new DefaultFacebookClient(getMainUser().getAccess_token());
		JsonObject user = fbClient.fetchObject("me",JsonObject.class);
		Parameter[] parameters = {Parameter.with("fields",	"id, name, email, picture, work")};
		Connection<User> myFriends= fbClient.fetchConnection("me/friends", User.class, Parameter.with("limit",5000));
		
		List<String> ids = new ArrayList<String>();
          ids.add("me"); // add  myself
		 boolean next;
		 
         do {
                 next = myFriends.hasNext();
                 
                 for (User friend : myFriends.getData()) {
                       
                	 String id = friend.getId(); 
                	 ids.add(id);
                	 //System.out.println(friend.getName());
                 }
                 
                 if (next) {
                	 myFriends = fbClient.fetchConnectionPage(myFriends.getNextPageUrl(), User.class);
                 }
         
         } while (next);
	
		
      // step 2: get the basic profile info of my friends
         JsonObject friendsProfile = fbClient.fetchObjects(ids, JsonObject.class);
         JsonMapper mapper = new DefaultJsonMapper();
         List<User> friends = new ArrayList<User>();
         Iterator<?> itr = friendsProfile.keys();
         while (itr.hasNext()) {
                 String key = (String) itr.next();
                 User friend = mapper.toJavaObject(friendsProfile.getString(key), User.class);
                 friends.add(friend);
                // System.out.println(friend.getFavoriteTeams().toString());
         }
      // step 3: get specific object  of my friends
         JsonObject rObject = fbClient.fetchObject(object, JsonObject.class, Parameter.with("ids", getAsIdParamList(friends)));
         Map<String, PageConnections> favorite = new HashMap<String, PageConnections>();
         itr = rObject.keys();
         while (itr.hasNext()) {
                 String key = (String) itr.next();
                 JsonObject rList = rObject.getJsonObject(key);
                 PageConnections pageConnections = mapper.toJavaObject(rList.toString(), PageConnections.class);
                 favorite.put(key, pageConnections);
         }
         
         // step 3a: output result
         HashMap<String, String> result = new HashMap<String, String>();
         for (User friend : friends) {
                 //System.out.println("Favorite "+object +" of "+ friend.getName()+":");
                 PageConnections connections = favorite.get(friend.getId());
                 if(connections.getData().size()>=1)
                 {
                	 String value="{";
                	 int i = connections.getData().size();
                     for (PageConnection connection : connections.getData()) {
                            // System.out.print("  " + connection.getName() );
                             value= value +"'"+ connection.getName()+"'";
                             i--;
                             if(i>0){
                            	 value=value+",";
                             }
                             
                     }
                     value=value+"}";
                     //System.out.println(value);
                     result.put(friend.getName(), value);
                 }
               
                 
         }
        
         
         return result;
		
         
	}

public String getAsIdParamList(List<User> users) {
	
    StringBuilder param = new StringBuilder();
    
    for (User user : users) {
            
   	 param.append(user.getId() + ",");
    }
    param.deleteCharAt(param.length()-1);
    return param.toString();
}

public static void main(String[] args) {
	String userId = "";
	String access_token = "CAACEdEose0cBAPsZBNzxZA00AXaGYljtP5j93TNctBnTU6XQt5g2Ag89Lm0jknVlJiVZB3ohIBvCtMcw8Fku8ptSSr1zkCEmdCq1W0Lx2bUUI0LV0dS8Kdm8NGNu0VVEucIez0xyTP9Ww14gTgZC1APjUENZByYdBVSDf8TELIj7eK7Ph0OsairOJwBZBhFc7VEWWUKJKXZCiITdZCZBFIrqvZAPqv68tSteqS6yFNXXEZCDQZDZD";
	FbUser user = new FbUser(userId, access_token);
	FbDataCollector dataCollector = new FbDataCollector(user);
	dataCollector.retrieveUserProfile("movies");
}
	
	

}