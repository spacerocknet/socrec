/***
 * @author ChanhNLT1
 * 
 */
package com.database;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.xml.soap.Text;

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
				            "createat timestamp," +
				           "like set<text>,"+
				            "PRIMARY KEY(id, username, createat)"+
				            ");");
			
	}
	public ResultSet  querySchema() {
		ResultSet results = getSession().execute("SELECT * FROM facebook.movies " +
		        ";");
		
		return results;
		
	}
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
		   client.connect("127.0.0.1");
		   client.createSchema();
		   
		   //client.preInsertData(UUID.randomUUID().toString(), "fafa", "fff");
		 //  client.insertData("756716f7-2e54-4715-9f00-91dcbea6cf50","ta ta","{'aa','bb'}");
		   ResultSet results= client.querySchema();
		   System.out.println(String.format("\t%-20s\t%-20s\n%s", "username", "movietitle",
			       "-----------------------+--------------------"));
		   
			for (Row row : results) {
				
			    System.out.println(String.format("\t%-20s\t%-20s", row.getString("username"),
			    		row.getSet("movietitle", String.class)));
			   
			}
			System.out.println();
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
}
