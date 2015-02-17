package com.socrec.word2vec

import org.apache.spark._
import org.apache.spark.rdd._
import org.apache.spark.SparkContext._
import org.apache.spark.mllib.feature.Word2Vec
import org.apache.spark.mllib.feature.Word2VecModel
import java.io._
import com.google.api.client.http.GenericUrl
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.jayway.jsonpath.JsonPath;
import java.io.FileInputStream;
import java.util.Properties;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * @author ${user.name}
 */
object App {
  var model: Word2VecModel = null
  
  def train_model[S <: Iterable[String]](data: RDD[S]){
    val word2vec = new Word2Vec()
    model = word2vec.fit(data)  
  }
  
  def saveTrainedModel (fileName: String) {
    val oos = new ObjectOutputStream(new FileOutputStream(fileName))
    oos.writeObject(model)
    oos.close
  }
  
  def resetModel() {
    model = null  
  }
  
  def loadTrainedModel(fileName: String) {
    try {
      val ois = new ObjectInputStream(new FileInputStream(fileName))
      model = ois.readObject().asInstanceOf[Word2VecModel]
      ois.close
    }catch {
      case ioe: IOException => error("Cannot read trained Word2Vec object from file ".concat(fileName))
      case e: Exception => error("Cannot load trained Word2Vec object")
    }
  }
  
  def findSynonyms(word: String, num: Int): Array[(String, Double)] = {
    return model.findSynonyms(word, num)
  }
    
  def main(args : Array[String]) {
    if (args.length>0)
      Word2VecConf.load(args(0))
    else
      Word2VecConf.load("etc/word2vec.conf")
      
    val conf = new SparkConf().setAppName("Word2Vec demo").setMaster("local[4]")
    val sc = new SparkContext(conf)
    println(Word2VecConf.getTrainingDataFileName())
    val input = sc.textFile(Word2VecConf.getTrainingDataFileName()).map(line => line.split(" ").toSeq)
    App.train_model(input)
    val synonyms = App.findSynonyms("china", 40)
    for((synonym, cosineSimilarity) <- synonyms) {
      println(s"$synonym $cosineSimilarity")
    } 
    
/*    
    //query Freebase
    val properties: Properties = new Properties()
    try {
      properties.load(new FileInputStream("freebase.properties"))
      val httpTransport: HttpTransport  = new NetHttpTransport()
      val requestFactory: HttpRequestFactory  = httpTransport.createRequestFactory()
      val parser: JSONParser = new JSONParser()
      val url: GenericUrl = new GenericUrl("https://www.googleapis.com/freebase/v1/search")
      url.put("query", "Cee Lo Green")
      url.put("filter", "(all type:/music/artist created:\"The Lady Killer\")")
      url.put("limit", "10")
      url.put("indent", "true")
      url.put("key", properties.get("API_KEY"))
      val request: HttpRequest = requestFactory.buildGetRequest(url)
      val httpResponse: HttpResponse = request.execute()
      val response: JSONObject = parser.parse(httpResponse.parseAsString()).asInstanceOf[JSONObject]
      val results: JSONArray = response.get("result").asInstanceOf[JSONArray]
      for (result in results) {
        System.out.println(JsonPath.read(result,"$.name").toString());
      }
    } catch {
      case ex: Exception => ex.printStackTrace();
    } */
  }

}
