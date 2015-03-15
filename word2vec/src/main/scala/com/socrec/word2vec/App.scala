package com.socrec.word2vec

import scala.collection.JavaConversions._
import org.apache.spark._
import org.apache.spark.rdd._
import org.apache.spark.SparkContext._
import org.apache.spark.mllib.feature.Word2Vec
import org.apache.spark.mllib.feature.Word2VecModel
import java.io._
import com.google.api.client.http.GenericUrl
import com.google.api.client.http.HttpRequest
import com.google.api.client.http.HttpRequestFactory
import com.google.api.client.http.HttpResponse
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.jayway.jsonpath.JsonPath
import java.io.FileInputStream
import java.util.Properties
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import com.database.CassandraConnect
import com.datastax.driver.core.Row;
import com.nlp.Entity

/**
 * @author Nhan Nguyen [ndnhan@gmail.com]
 */
object App {
  val apiKey: String = "AIzaSyC2oiWpU1eqqAsItJPE3kPvIdophBZBzEM"
  def simpleFreebaseQuery(query: String): JSONArray = {
    //query Freebase
    try {
      val httpTransport: HttpTransport  = new NetHttpTransport()
      val requestFactory: HttpRequestFactory  = httpTransport.createRequestFactory()
      val parser: JSONParser = new JSONParser()
      val url: GenericUrl = new GenericUrl("https://www.googleapis.com/freebase/v1/search")
      url.put("query", query)
      url.put("limit", "5")
      url.put("indent", "true")
      url.put("key", apiKey)
      val request: HttpRequest = requestFactory.buildGetRequest(url)
      val httpResponse: HttpResponse = request.execute()
      val response: JSONObject = parser.parse(httpResponse.parseAsString()).asInstanceOf[JSONObject]
      val results: JSONArray = response.get("result").asInstanceOf[JSONArray]
      return results
    } catch {
      case ex: Exception => ex.printStackTrace()
      return null
    } 
  }
  
  def freebaseQuery(query: String, domain: String): JSONArray = {
    //query Freebase
    try {
      val httpTransport: HttpTransport  = new NetHttpTransport()
      val requestFactory: HttpRequestFactory  = httpTransport.createRequestFactory()
      val parser: JSONParser = new JSONParser()
      val url: GenericUrl = new GenericUrl("https://www.googleapis.com/freebase/v1/search")
      url.put("query", query)
      url.put("filter", "(all domain:\""+domain+"\"")
      url.put("limit", "10")
      url.put("indent", "true")
      url.put("key", apiKey)
      val request: HttpRequest = requestFactory.buildGetRequest(url)
      val httpResponse: HttpResponse = request.execute()
      val response: JSONObject = parser.parse(httpResponse.parseAsString()).asInstanceOf[JSONObject]
      val results: JSONArray = response.get("result").asInstanceOf[JSONArray]
      return results
    } catch {
      case ex: Exception => ex.printStackTrace()
               return null
    } 
  }
  
  def processUserData(rows: List[Row]) {
    val textbuilder = new StringBuilder()
    if(rows.size()==1){
        textbuilder.append(rows.get(0).getString(0));
    }
    else{
      for(i <- 0 to (rows.size-1)) {
        textbuilder.append(rows.get(i).getSet(0, classOf[String]).toString());
      }
    }
    var contentText=textbuilder.toString()
    contentText=contentText.replaceAll("'", "")
    contentText= contentText.replaceAll("\\[", "")
    contentText= contentText.replaceAll("]", ". ")
    val en = new Entity()
   //keywords
    val keywords = en.keywordList(contentText)
   //entity and its type
   //HashMap<String, ArrayList<String>>entities =en.entityList(contentText);
    println("List of keywords "+keywords.toString())
    keywords.foreach { x => 
      {
        println("Finding synonyms of word: "+x)
        try{
          val synonyms = Word2VecWrapper.findSynonyms(x, 5)
          for((synonym, cosineSimilarity) <- synonyms) {
            println("*** Synonym: " + synonym)
            println("*** Freebase query results for " + synonym)
            val res: JSONArray =  simpleFreebaseQuery(synonym)
            val it = res.iterator()
            while (it.hasNext()) {
              println(it.next().toString())  
            }    
          } 
        } 
        catch {
            case ioe: IllegalStateException=>{}
        }
      } 
    }
  }
  
  def main(args : Array[String]) {
    Word2VecConf.load("etc/word2vec.conf")
    val modelObjFile = "ser_w2v.object"
    val conf = new SparkConf().setAppName("Word2Vec demo").setMaster("local[4]")
    val sc = new SparkContext(conf)
    if (args(0)=="train") {
      println(Word2VecConf.getTrainingDataFileName())
      val input = sc.textFile(Word2VecConf.getTrainingDataFileName()).map(line => line.split(" ").toSeq)
      Word2VecWrapper.train_model(input)
      Word2VecWrapper.saveTrainedModel(modelObjFile)
    } else if (args(0)=="use") {
      Word2VecWrapper.loadTrainedModel(modelObjFile)
      
      System.out.println("Connect Cassandra database...")
      val client = new CassandraConnect()
      client.connect("127.0.0.1")
      client.createSchema()
      println("Get user's FB data and process")
      val rows = client.getUserFavoriteMovie("Tina Trang").all().toList
      println("Movies of user 1" + rows.toString())
      App.processUserData(rows)
      
      val rows2 = client.getUserStatus("Peter").all().toList
      println("Status of user 2" + rows2.toString())
      App.processUserData(rows2)
      
      
      /*****
      val synonyms = App.findSynonyms("vietnam", 5)
      
      for((synonym, cosineSimilarity) <- synonyms) {
        println("*** Synonym: " + synonym)
        println("*** Freebase query results for " + synonym)
        val res: JSONArray =  FreebaseQuery.simpleQuery(synonym)
        val it = res.iterator()
        while (it.hasNext()) {
          println(it.next().toString())  
        }    
      } ****/
      
    }
     
  }

}
