package com.fb;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;

import com.restfb.Facebook;
import com.restfb.types.PageConnection;
public class PageConnections {
	 @Facebook("data")
     private List<PageConnection> data = new ArrayList<PageConnection>();

     /**
      * A list of page connetions of the user's profile.
      * 
      * @return A list of page connetions of the user's profile.
      */
     public List<PageConnection> getData() {
             return unmodifiableList(data);
     }

	
}
