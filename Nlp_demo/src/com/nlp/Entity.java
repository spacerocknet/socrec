package com.nlp;

import com.alchemyapi.api.AlchemyAPI;
import com.alchemyapi.api.AlchemyAPI_KeywordParams;
import com.alchemyapi.api.AlchemyAPI_NamedEntityParams;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;











import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


import javax.xml.xpath.XPathExpressionException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class Entity {
	private AlchemyAPI alchemyClient;
	
	public Entity()
	{
		setAlchemyClient();
	}
	public List<String> keywordList(String contentText) throws IOException, SAXException,
    ParserConfigurationException, XPathExpressionException
	{

	    
	    AlchemyAPI_KeywordParams keywordParams = new AlchemyAPI_KeywordParams();
		keywordParams.setSentiment(true);
		Document alchemyRankedKeywords = this.alchemyClient.TextGetRankedKeywords(contentText);
		//System.out.println(getStringFromDocument(alchemyRankedKeywords));
		return keywordParse(getStringFromDocument(alchemyRankedKeywords));
	    
	    
	}
	public HashMap<String, ArrayList<String>> entityList(String contentText) throws IOException, SAXException,
    ParserConfigurationException, XPathExpressionException
	{

        AlchemyAPI_NamedEntityParams entityParams = new AlchemyAPI_NamedEntityParams();
        entityParams.setDisambiguate(true);
		entityParams.setSentiment(true);
		Document alchemyRankedNamedEntities = this.alchemyClient.TextGetRankedNamedEntities(contentText, entityParams);
	    System.out.println(getStringFromDocument(alchemyRankedNamedEntities));
		return entityParse(getStringFromDocument(alchemyRankedNamedEntities));
	    
	    
	}
    public static void main(String[] args)
        throws IOException, SAXException,
               ParserConfigurationException, XPathExpressionException
    {
      Entity entity = new Entity();
      entity.entityList("David beckham, Ronaldo speaking at the UN Human Rights Council, Mr Kerry said he had warned Russia that it faced further sanctions if the conditions of the ceasefire were not met in full."

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
	
	
	public List<String> keywordParse(String input) throws ParserConfigurationException, SAXException, IOException
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    List<String> result=new ArrayList<String>();
	  
	    Document document = builder.parse(new InputSource(new StringReader(
	            input)));

	    NodeList flowList = document.getElementsByTagName("keyword");
	    for (int i = 0; i < flowList.getLength(); i++) {
	        NodeList childList = flowList.item(i).getChildNodes();
	        for (int j = 0; j < childList.getLength(); j++) {
	            Node childNode = childList.item(j);
	            if ("text".equals(childNode.getNodeName())) {
	          
	                result.add(childList.item(j).getTextContent()
	                        .trim());
	            }
	        }
	    }
	    return result;
	}
	

	public HashMap<String, ArrayList<String>> entityParse(String input) throws ParserConfigurationException, SAXException, IOException
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    HashMap<String, ArrayList<String>> result=new HashMap<String, ArrayList<String>>();
	  
	    Document document = builder.parse(new InputSource(new StringReader(
	            input)));

	    NodeList flowList = document.getElementsByTagName("entity");
	    for (int i = 0; i < flowList.getLength(); i++) {
	        NodeList childList = flowList.item(i).getChildNodes();
	        String text="";
	        ArrayList<String> type=new ArrayList<String>();
	        for (int j = 0; j < childList.getLength(); j++) {
	            Node childNode = childList.item(j);
	            if ("text".equals(childNode.getNodeName())) {
	            	
	          
	                text=childList.item(j).getTextContent()
	                        .trim();
	            }
	            if("type".equals(childNode.getNodeName()) || "subType".equals(childNode.getNodeName()) ){
	            	type.add(childList.item(j).getTextContent()
	                        .trim());
	            }
	        }
	    }
	    return result;
	}
	    

	  
	
}
