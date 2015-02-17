package com.socrec.word2vec

import java.util._
import java.io._
import scala.collection.JavaConverters._

object Word2VecConf {
  val key_trainingDataPath = "training.data.path"
  var configuration: collection.mutable.Map[String,String] = null

  def load(fileName: String) {
    val x = new Properties
    x.load(new FileInputStream(fileName))
    configuration = x.asScala
  }
  
  def getTrainingDataFileName(): String = {
    if (configuration.contains(key_trainingDataPath)) 
      return configuration.get(key_trainingDataPath).toString()
    else 
      return "data/wiki1.txt"
  }
}