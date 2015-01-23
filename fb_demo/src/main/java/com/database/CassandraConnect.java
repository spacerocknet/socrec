/***
 * @author ChanhNLT1
 * 
 */
package com.database;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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
			      "CREATE TABLE IF NOT EXISTS facebook.user_favoriteMovies (" +
			            "id uuid," +
			            "username text," +
			            "Movie text," +
			            "PRIMARY KEY (id, username)" +
			            ");");
			
	}
	public ResultSet  querySchema() {
		ResultSet results = getSession().execute("SELECT * FROM facebook.songs " +
		        "WHERE id = 756716f7-2e54-4715-9f00-91dcbea6cf50;");
		
		return results;
		
	}
	public void insertData() {
		
		getSession().execute(
			      "INSERT INTO facebook.user_favoriteMovies (id, username, Movie) " +
			      "VALUES (" +
			          "756716f7-2e54-4715-9f00-91dcbea6cf50," +
			          "'La Petite Tonkinoise'," +
			          "'Bye Bye Blackbird'," +
			          "'Joséphine Baker'," +
			          "{'jazz', '2013'})" +
			          ";");
	}
	
	public void preInsertData(String id, String username, String movie) {
		PreparedStatement preInsertStatement = getSession().prepare(
				"INSERT INTO facebook.user_favoriteMovies " +
				"(id, username,Movie) " +
			    "VALUES (?, ?, ?);");
		BoundStatement boundState = new BoundStatement(preInsertStatement);
		Set<String> tags = new HashSet<String>();
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
		   
		   //client.preInsertData();
		   ResultSet results= client.querySchema();
		   System.out.println(String.format("%-30s\t%-20s\t%-20s\n%s", "title", "album", "artist",
			       "-------------------------------+-----------------------+--------------------"));
			for (Row row : results) {
			    System.out.println(String.format("%-30s\t%-20s\t%-20s", row.getString("title"),
			    row.getString("album"),  row.getString("artist")));
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
