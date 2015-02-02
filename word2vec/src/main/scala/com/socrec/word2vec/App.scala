package com.socrec.word2vec

import org.apache.spark._
import org.apache.spark.rdd._
import org.apache.spark.SparkContext._
import org.apache.spark.mllib.feature.Word2Vec
import org.apache.spark.mllib.feature.Word2VecModel
import java.io._

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
    val conf = new SparkConf().setAppName("Word2Vec demo").setMaster("local[4]")
    val sc = new SparkContext(conf)
    val input = sc.textFile("data/enwiki-latest-pages-articles1.xml-p000000010p000010000").map(line => line.split(" ").toSeq)
    App.train_model(input)
    val synonyms = App.findSynonyms("china", 40)
    for((synonym, cosineSimilarity) <- synonyms) {
      println(s"$synonym $cosineSimilarity")
    }
  }

}
