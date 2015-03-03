package com.nlp;

import com.alchemyapi.api.AlchemyAPI;
import com.alchemyapi.api.AlchemyAPI_KeywordParams;
import com.alchemyapi.api.AlchemyAPI_NamedEntityParams;

import org.xml.sax.SAXException;
import org.w3c.dom.Document;

import java.io.*;
import java.net.URL;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

class Entity {
	private AlchemyAPI alchemyClient;
	public Entity()
	{
		setAlchemyClient();
	}
	public void showentity(String contentText) throws IOException, SAXException,
    ParserConfigurationException, XPathExpressionException
	{

        System.out.println(" AlCHEMY ENTITIES:");
        
        AlchemyAPI_NamedEntityParams entityParams = new AlchemyAPI_NamedEntityParams();
        entityParams.setDisambiguate(true);
		entityParams.setSentiment(true);
		Document alchemyRankedNamedEntities = this.alchemyClient.TextGetRankedNamedEntities(contentText, entityParams);
	    //System.out.println(getStringFromDocument(alchemyRankedNamedEntities));
	    
	    AlchemyAPI_KeywordParams keywordParams = new AlchemyAPI_KeywordParams();
		keywordParams.setSentiment(true);
		Document alchemyRankedKeywords = this.alchemyClient.TextGetRankedKeywords(contentText);
	    System.out.println(getStringFromDocument(alchemyRankedKeywords));
	    
	}
    public static void main(String[] args)
        throws IOException, SAXException,
               ParserConfigurationException, XPathExpressionException
    {
      Entity entity = new Entity();
      entity.showentity("David beckham, Ronaldo speaking at the UN Human Rights Council, Mr Kerry said he had warned Russia that it faced further sanctions if the conditions of the ceasefire were not met in full."

+"But he said he was optimistic the truce could be completed in "+"hours, certainly not more than days ");
    
    }

    
    // utility function
    private static String getFileContents(String filename)
        throws IOException, FileNotFoundException
    {
        File file = new File(filename);
        StringBuilder contents = new StringBuilder();

        BufferedReader input = new BufferedReader(new FileReader(file));

        try {
            String line = null;

            while ((line = input.readLine()) != null) {
                contents.append(line);
                contents.append(System.getProperty("line.separator"));
            }
        } finally {
            input.close();
        }

        return contents.toString();
    }

    // utility method
    private static String getStringFromDocument(Document doc) {
        try {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);

            return writer.toString();
        } catch (TransformerException ex) {
            ex.printStackTrace();
            return null;
        }
    }

	AlchemyAPI getAlchemyClient() {
		return alchemyClient;
	}

	void setAlchemyClient() {
		URL url = ClassLoader.getSystemResource("properties");
		Properties p = new Properties();
		try {
			InputStream is = url.openStream();
			p.load(is);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		String alchemyAPIKey = p.getProperty("alchemy.key");
		this.alchemyClient = AlchemyAPI.GetInstanceFromString(alchemyAPIKey);
	}
}
