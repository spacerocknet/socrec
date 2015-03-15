package samples

import org.junit._
import Assert._
import org.apache.spark._
import org.apache.spark.rdd._
import org.apache.spark.SparkContext._
import org.apache.spark.mllib.feature.Word2Vec
import org.apache.spark.mllib.feature.Word2VecModel
import java.io._
import scala.util._

import com.socrec.word2vec._

@Test
class AppTest {
    val testCases = Array("japan", "love", "country", "nation", "journal")
    val ran: Random = new Random(System.currentTimeMillis())
    def testModel() {
      println("Testing model...")
      val str = testCases(ran.nextInt(testCases.length))
      val synonyms = Word2VecWrapper.findSynonyms(str, 10)
      assert(synonyms.length > 0)
      println("Synonyms of" + str)
      for((synonym, cosineSimilarity) <- synonyms) {
        println(s"$synonym $cosineSimilarity")
      }
    }
    
    def testSerialization(fileName: String) {
      println("Testing serialization ...")
      Word2VecWrapper.saveTrainedModel(fileName)
    }
    
    def testDeserialization(fileName: String) {
      println("Testing deserialization...")
      Word2VecWrapper.resetModel()
      Word2VecWrapper.loadTrainedModel(fileName)
      assert(Word2VecWrapper.model != null)
      testModel();
    }
    
    @Test
    def testOK() {
      val conf = new SparkConf().setAppName("Word2Vec demo").setMaster("local[4]")
      
      /*conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      conf.registerKryoClasses(Array(classOf[Word2VecModel]))*/
      
      val sc = new SparkContext(conf)
      val input = sc.textFile("data/wiki1_tiny.txt").map(line => line.split(" ").toSeq)
      Word2VecWrapper.train_model(input)
      testModel()
      
      val fileName: String = "testser_w2v.object"
      testSerialization(fileName)
      testDeserialization(fileName)
    } 

}


