/***
 * @author ChanhNLT1
 * 
 */
package com.database;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;













import com.alchemyapi.api.AlchemyAPI;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;


public class CassandraConnect {
	
	private Cluster cluster;
	private Session session;
	private Connection con;
	public void connect(String node) {
	
		setCluster(Cluster.builder()
		         .addContactPoint(node)
		         .build());
		   Metadata metadata = getCluster().getMetadata();
		   System.out.printf("Connected to cluster: %s\n", 
		         metadata.getClusterName());
		   for ( Host host : metadata.getAllHosts() ) {
		      System.out.printf("Datacenter: %s; Host: %s; Rack: %s\n",
		         host.getDatacenter(), host.getAddress(), host.getRack());
		
		   }
		   
		   setSession(getCluster().connect());
	}
	
	public void close() {
		
		this.getCluster().close();
	}
	/**
	 * this function is to create schema for keyspace in which tables, data model of each table would be defined
	 */
	public void createSchema() {
		
		getSession().execute("CREATE KEYSPACE IF NOT EXISTS facebook WITH replication " + 
			      "= {'class':'SimpleStrategy', 'replication_factor':3};");
		getSession().execute(
			      "CREATE TABLE IF NOT EXISTS facebook.userinfo (" +
			            "id uuid PRIMARY KEY," + 
			            "username text," + 
			            "email text," + 
			            "phone text," + 
			            "hometown text," + 
			            "address text" + 
			            ");");
			getSession().execute(
			      "CREATE TABLE IF NOT EXISTS facebook.Movies (" +
			            "id uuid," +
			            "username text," +
			            "movietitle set<text>," +
			            "PRIMARY KEY (id, username)" +
			            ");");
			getSession().execute(
				      "CREATE TABLE IF NOT EXISTS facebook.like (" +
				            "id uuid ," + 
				            "username text," +
				            "createdat timestamp," +
				           "like set<text>,"+
				            "PRIMARY KEY(id, username, createdat)"+
				            ");");
			getSession().execute(
				      "CREATE TABLE IF NOT EXISTS facebook.status (" +
				            "id uuid ," + 
				            "username text," +
				           "message text,"+
				           "createdat timestamp," +
				            "PRIMARY KEY(id, username, createdat)"+
				            ");");
	}
	public ResultSet  querySchema() {
		ResultSet results = getSession().execute("SELECT * FROM facebook.movies " +
		        ";");
		
		return results;
		
	}
	public ResultSet getUserFavoriteMovie(String username){
		ResultSet results= getSession().execute("SELECT movietitle FROM facebook.movies " +
				"WHERE username = " + "'"+ username+ "'" +
		        "ALLOW FILTERING;");
		return results;
	}
	public ResultSet getUserStatus(String username){
		ResultSet results= getSession().execute("SELECT message FROM facebook.status " +
				"WHERE username = " + "'"+ username+ "'" +
		        " ALLOW FILTERING;");
		return results;
	}
	//public ResultSet getUser
	public void insertMovieData(String id, String username, String movielist) {
		
		getSession().execute(
				
			      "INSERT INTO facebook.Movies (id, username, movietitle) " +
			      "VALUES (" + 
			    	  id + "," +
			          "'"+ username+ "'," +
			          movielist+")" +
			          ";");
	}
	public void insertUserLikeData(String id, String username, String likelist,String createdat) {
		
		getSession().execute(
				
			      "INSERT INTO facebook.Like (id, username, like, createdat) " +
			      "VALUES (" + 
			    	  id + "," +
			          "'"+ username+ "'," +
			          likelist + "," +
			          createdat +
			          ")" +
			          ";");
	}
	public void insertUserStatusData(String id, String username, String status,String createdat){
		getSession().execute(
				
			      "INSERT INTO facebook.status (id, username, message, createdat) " +
			      "VALUES (" + 
			    	  id + "," +
			          "'"+ username+ "','" +
			          status + "','" +
			          createdat +
			          "')" +
			          ";");
	}
	public void preInsertData(String id, String username, String movie) {
		PreparedStatement preInsertStatement = getSession().prepare(
				"INSERT INTO facebook.Movies " +
				"(id,username,movietitle) " +
			    "VALUES (?, ?, ?);");
		BoundStatement boundState = new BoundStatement(preInsertStatement);
		getSession().execute(boundState.bind(
				UUID.fromString(id),
			      username,
			      movie)
				);
	}
	public static void main(String[] args) {

		CassandraConnect client = new CassandraConnect();
		client.init();  
		client.connect("127.0.0.1");
		   client.createSchema();
		   
		   //client.preInsertData(UUID.randomUUID().toString(), "fafa", "fff");
		 //  client.insertData("756716f7-2e54-4715-9f00-91dcbea6cf50","ta ta","{'aa','bb'}");
		   client.insertFakeDatatoStatusTable();
		   client.close();
		}

	private Session getSession() {
		return session;
		
	}

	private void setSession(Session session) {
		this.session = session;
	}

	private Cluster getCluster() {
		return cluster;
	}

	private void setCluster(Cluster cluster) {
		this.cluster = cluster;
	}
	
	public void init() {
		
        try {
			Class.forName("org.apache.cassandra.cql.jdbc.CassandraDriver");
			
			// Test if keyspace/column family present
			// Create keyspace/column family

			// Lookup "chambrebasse" keyspace
			setCon(DriverManager.getConnection("jdbc:cassandra://localhost:9160/ChambreBasse"));
			System.err.println("Creating keyspace and column families for with \"ChambreBasse\" schema");
			// getCon().prepareStatement("DROP KEYSPACE ChambreBasse").execute();
			getCon().prepareStatement("USE ChambreBasse").execute();
			getCon().prepareStatement("DROP COLUMNFAMILY Pub").execute();
			getCon().prepareStatement("CREATE COLUMNFAMILY Pub (id uuid PRIMARY KEY, providerId bigint, createdAt bigint, fromUser ascii, fromUserProviderId bigint, fromUserFullName text, fromUserLocation text, fromUserPlaceFullName text, fromUserPlaceType ascii, fromUserPlaceProviderId ascii, fromUserPlaceBoundingBoxJSON ascii, fromUserPlaceBoundingBoxType ascii, fromUserPlaceContainerJSON ascii, fromUserPlaceCountry text, fromUserPlaceCountryCode ascii, fromUserPlaceGeometryJSON ascii, fromUserPlaceGeometryType ascii, fromUserPlaceURL text, isoLanguageCode ascii, placeFullName text, placeType ascii, placeProviderId ascii, placeBoundingBox ascii, placeBoundingBoxType ascii, placeParentProviderId ascii, placeCountry text, placeCountryCode ascii, placeGeometry ascii, placeGeometryType ascii, placeURL text, profileImageUrl ascii, source ascii, content text, expandedContent text, annotations text, toUser ascii, toUserProviderId bigint, queryFactoryClass ascii, queryFactoryClassContainerPlaceProviderId ascii, queryString text, queryPage int, queryUntil ascii, querySinceProviderId bigint)").execute();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Connection getCon() {
		return con;
	}

	private void setCon(Connection con) {
		this.con = con;
	}
	public void insertFakeDatatoStatusTable()
	{
		List<String> ids= new ArrayList<String>();
		for (int i =0; i<3;i++){
			ids.add(UUID.randomUUID().toString());
		}
		List<String> username= new ArrayList<String>();
		username.add("Peter");
		username.add("Obama");
		username.add("Nguyen Van A");
		List<String> statuses=new ArrayList<String>();
		statuses.add("The top five most expensive cities in the world remain unchanged from a year earlier and include, in descending order, Paris, Oslo, Zurich and Sydney.");
		statuses.add("Amateur photographer Martin Le-May, from Essex, has recorded the extraordinary image of a weasel riding on the back of a green woodpecker as it flies through the air.");
		statuses.add("Physical self-improvement is a long-established business. During the 1940s, weightlifter Charles Atlas advertised his bodybuilding courses by describing himself as a the 97lb weakling who became the world most perfectly developed man. The pieces often featured stories of how skinny young men on beaches had followed his diktats for a short period, returned and successfully confronted bullies who had kicked sand in their faces.");
		List<String> createdat = new ArrayList<String>();
		for (int i =0; i<3;i++){ 
			createdat.add("2015-02-03");
		}
		for(int i=0;i<3;i++){
			insertUserStatusData(ids.get(i),username.get(i),statuses.get(i).replaceAll("'", ""),createdat.get(i));
		}
		
	}
	
}
