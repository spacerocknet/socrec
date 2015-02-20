import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import facebook4j.FacebookException;
import facebook4j.Post;
import facebook4j.ResponseList;


public class Testing {

	public static void main(String[] args) {
		FaceOAuthToken fOAuthToken=new FaceOAuthToken();
		try{
			ResponseList<Post>postList=fOAuthToken.facebook.getHome();	
			Iterator<Post> t=postList.iterator();
			DateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd");
			Date date=new Date();
			String d= "Facebook date feed at :"+dateFormat.format(date);
			writeToFile(d);
			while(t.hasNext())
			{
				Post p=t.next();
				String name=p.getFrom().getName();
				String message=p.getMessage();
			//String output=new String(name.getBytes(), Charset.forName("UTF-8"));
				
				String output=name+"---"+message;
				writeToFile(output);
				System.out.println(output);
			}
		}
		catch(FacebookException fex)
		{
			System.out.println(fex.getErrorMessage());
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}
		

	}
	public static void writeToFile(String output)throws Exception
	{
		String filename="output/filename.txt";
		File file = new File("output/filename.txt");
		 
		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(filename, true);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.append(output+"\n");
		bw.close();

		System.out.println("Done");
	}

}
