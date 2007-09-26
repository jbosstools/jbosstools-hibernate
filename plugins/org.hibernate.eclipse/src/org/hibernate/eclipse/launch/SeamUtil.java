package org.hibernate.eclipse.launch;

import java.util.HashSet;
import java.util.Set;

public class SeamUtil {

	   public String lower(String name)
	   {
	      return name.substring(0, 1).toLowerCase() + name.substring(1);
	   }
	   public String upper(String name)
	   {
	      return name.substring(0, 1).toUpperCase() + name.substring(1);
	   }
	   public Set set()
	   {
	      return new HashSet();
	   }

}
