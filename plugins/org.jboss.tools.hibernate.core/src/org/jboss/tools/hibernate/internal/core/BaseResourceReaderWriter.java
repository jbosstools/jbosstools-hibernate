/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.hibernate.internal.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.SequencedHashMap;
import org.dom4j.Attribute;
import org.dom4j.Comment;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.Text;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;


import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

class NullResolver implements EntityResolver {
	private static NullResolver instance=new NullResolver();
  public InputSource resolveEntity (String publicId, String systemId)
  {
      return new InputSource(new StringReader(""));
  }
  public static NullResolver getInstance(){ return instance;}
}

public class BaseResourceReaderWriter 
{
	
	protected SequencedHashMap 	mapcomments = new SequencedHashMap();
	protected String 			searchParam = null;
	protected ArrayList<Node> elements = new ArrayList<Node>();
	
	
	protected void reorderElements(Element root, String elemName) {
		if (root.element(elemName) == null)
			return;
		Iterator elems = root.elementIterator(elemName);

		while (elems.hasNext()) {
// added by yk 21.07.2005 
			// collect comments.
			Element temp = (Element)elems.next();
			String name = (searchParam != null) ? temp.attributeValue(searchParam) : getDefaultName(temp);
			if(mapcomments.containsKey(name)) {
				Iterator it = ((List)mapcomments.remove(name)).iterator();
				while(it.hasNext()) {
					Object o = it.next();
					if(o instanceof Element) {
						elements.add((Element)o);
					}
				}
			}
// added by yk 21.07.2005 stop
			
			elements.add(temp);
		}
		for (int i = 0; i < elements.size(); ++i) {
			Node node = (Node) elements.get(i);
			root.add(node.detach());
		}
		elements.clear();
	}
	protected void reorderElements(Element root, String elementOrder[]) 
	{
		for (int i = 0; i < elementOrder.length; ++i) {
			reorderElements(root, elementOrder[i]);
		}
	}

// added by yk 19.09.2005
	protected void collectComments(Element root, String elementOrder[], String searchparam)
	{
		mapcomments.clear();
		searchParam = searchparam;
		for (int i = 0; i < elementOrder.length; ++i) 
		{
			Iterator elems = root.elementIterator(elementOrder[i]);
			while (elems.hasNext()) 
			{
				Element  el 		= (Element)elems.next();
				List 	 comments 	= getCommentsForNode(root, el); // attempt getting comments for the node;
				if(comments.size() > 0)
				{
					String name = (searchParam != null) ? el.attributeValue(searchParam) : getDefaultName(el);
					mapcomments.put(name,comments);
				}
			}
		}
	}

	private String getDefaultName(Element element)
	{
		String name = null;
		Iterator attributes = element.attributeIterator();
		if(attributes.hasNext())
		{
			Attribute attrib = (Attribute)attributes.next();
			if(attrib != null)
				name = attrib.getValue();
		}
		return name;
	}
	private List getCommentsForNode(Element root, Element elem)
	{
		ArrayList<Node> comments = new ArrayList<Node>();
		if(elem == null)		return comments;
		List cnt = root.content();
		int index = cnt.indexOf(elem); // index of for which search properties.
		
		while(index > 0)
		{
			Object item = cnt.get(--index);
			if(item instanceof Text) 			continue;				 							// ignore text;
			else if(item instanceof Comment) {
				((Node)item).detach(); comments.add((Comment)item);
			}	// collect comments;
			else								break;					 							// if there are no comments - exit.
		}
		Collections.reverse(comments);
		return comments;
	}
// added by yk 19.09.2005.

	protected void mergeAttribute(Element e, String attName, String value) {
		if (value != null && value.length() > 0)
			e.addAttribute(attName, value);
		else {
			Attribute node = e.attribute(attName);
			if (node != null)
				e.remove(node);
		}
	}
	
	protected void mergeElement(Element e, String elemName, String value) {
		Element elem = e.element(elemName);
		if (value != null && value.length() > 0) {
			if (elem == null)
				elem = e.addElement(elemName);
			elem.setText(value);
		} else {
			if (elem != null)
				e.remove(elem);
		}
		Attribute node = e.attribute(elemName);
		if (node != null)
			e.remove(node);
	}
	
// added by yk 22.09.2005
	protected void mergeElementMultilineCDATA(Element e, String elemName, String value) {
		Element elem = e.element(elemName);
		if (value != null && value.length() > 0) {
			if (elem != null)	{elem.clearContent();}
			if (elem == null)	elem = e.addElement(elemName);
			String[] lines = prepareStatementForWriting(value);
			String towrite = "";
			for(int i = 0; i < lines.length; i++)
			{
				towrite += lines[i];
				if(i < lines.length-1)
					towrite += System.getProperty("line.separator");
			}
			elem.addCDATA(towrite);
		} else {
			if (elem != null)
				e.remove(elem);
		}
		Attribute node = e.attribute(elemName);
		if (node != null)
			e.remove(node);
	}
	private String[] prepareStatementForWriting(String value)
	{
		return value.split(System.getProperty("line.separator"));		
	}
// added by yk 22.09.2005.
	
	protected void removeElements(Element node, String name) {
		Iterator it = node.elementIterator(name);
		while (it.hasNext()) {
			Element elem = (Element) it.next();
			elem.detach();
		}
	}


	public Document readDocument(InputStream input) throws DocumentException {
			SAXReader reader = new SAXReader();
			reader.setValidation(false);
			reader.setIncludeExternalDTDDeclarations(false);
			reader.setIncludeInternalDTDDeclarations(false);
			reader.setEntityResolver(NullResolver.getInstance());
			return reader.read(input);
	}
	
	public InputStream writeDocument(Document doc) throws UnsupportedEncodingException, IOException {
			OutputFormat format = OutputFormat.createPrettyPrint();
			ByteArrayOutputStream res=new ByteArrayOutputStream(2000);
			XMLWriter writer = new XMLWriter(res, format);
			writer.write(doc);
			return new ByteArrayInputStream(res.toByteArray());
	}

}
