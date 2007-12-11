package org.hibernate.eclipse.console.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.hibernate.cfg.Configuration;
import org.hibernate.eclipse.console.actions.OpenMappingAction;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;

/**
 * @author Dmitry Geraskov
 */

public class OpenMappingTests extends TestCase{

	public static final String MAPPING_PACKAGE = "src/org/hibernate/eclipse/console/test/mapping/";
    public static final String HIBERATE_CFG_FILE = "hibernate.cfg.xml";
    public static final String PERSON_CFG_FILE = "Person.hbm.xml";
    
    public void testParceComplexProcess() {
      IPath path_hib = new Path(MAPPING_PACKAGE+HIBERATE_CFG_FILE);
      IPath path_pers = new Path(MAPPING_PACKAGE+PERSON_CFG_FILE);
      File file_hib = path_hib.toFile();
      File file_pers = path_pers.toFile();      
      if (!file_hib.exists()) {
  		fail("File "+HIBERATE_CFG_FILE+" not found!");
      }
      if (!file_pers.exists()) {
  		fail("File "+PERSON_CFG_FILE+" not found!");
      }
      try {	
		Configuration config = new Configuration();
		config.configure(file_hib);
		
		FileReader reader = new FileReader(file_pers);
		BufferedReader in = new BufferedReader(reader);
		String s, text = "";
		while ((s = in.readLine()) != null) {
			text += s + '\n';
		}      
		Document document = new Document(text);
		FindReplaceDocumentAdapter findAdapter = new FindReplaceDocumentAdapter(document);
	    
	    Iterator classMappings = config.getClassMappings();
	    while (classMappings.hasNext()) {
	    	Object element = classMappings.next();
	    	if (element instanceof PersistentClass) {
	    		PersistentClass type = (PersistentClass)element; 
		    	assertNotNull(OpenMappingAction.findSelection(type, findAdapter));	
		    	Iterator properties = type.getPropertyIterator();
		    	while (properties.hasNext()) {
					Property prop = (Property) properties.next();
					assertNotNull(OpenMappingAction.findSelection(prop, findAdapter));
				}
			}
	    }
      } catch (FileNotFoundException e) {
    	  fail("FileNotFoundException: "+ e.getMessage());
      } catch (IOException e) {
    	  fail("IOException: "+ e.getMessage());
      } 
    }  
}
