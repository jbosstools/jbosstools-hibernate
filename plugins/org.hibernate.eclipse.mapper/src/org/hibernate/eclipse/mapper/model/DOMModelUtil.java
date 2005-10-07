package org.hibernate.eclipse.mapper.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.xml.core.internal.provisional.format.ElementNodeFormatter;
import org.jboss.deployment.DeploymentException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DOMModelUtil {

	private DOMModelUtil() {

	}

	static public Node getNextNamedSibling(Node node, String nodeName) {
		Node nextSibling = node.getNextSibling();
		while ( nextSibling != null
				&& !nextSibling.getNodeName().equals( nodeName ) ) {
			nextSibling = nextSibling.getNextSibling();
		}
		return nextSibling;
	}

	static Node getPreviousNamedSibling(Node node, String nodeName) {
		Node nextSibling = node.getPreviousSibling();
		while ( nextSibling != null
				&& !nextSibling.getNodeName().equals( nodeName ) ) {
			nextSibling = nextSibling.getPreviousSibling();
		}
		return nextSibling;
	}

	static public void addElementBefore(Node parentNode, Node element,
			Node before) {
		if ( before == null ) {
			parentNode.appendChild( element );
		}
		else {
			parentNode.insertBefore( element, before );
		}
		DOMModelUtil.formatNode( parentNode );
	}

	static void formatNode(Node node) {
		ElementNodeFormatter formatter = new ElementNodeFormatter();
		formatter.format( node );
	}

	static public boolean isWhiteSpace(Node node) {
		return node != null && node.getNodeType() == Node.TEXT_NODE
				&& node.getNodeValue().trim().length() == 0;
	}

	public static List getAdaptedElements(Element n, String elementName,
			INodeAdapterFactory factory) {
		List result = new ArrayList();
		List list = DOMModelUtil.getChildrenByTagName(n, elementName );
		for (int i = 0; i < list.size(); i++) {
			Node item = (Node) list.get( i );
			result.add( factory.adapt( (INodeNotifier) item ) );
		}
		return result;
	}

	/**
	 * Returns an list with the children of the given element with the given
	 * tag name.
	 * 
	 * @param element
	 *            The parent element
	 * @param tagName
	 *            The name of the desired child
	 */
	public static List getChildrenByTagName(Node element, String tagName) {
		if ( element == null )
			return null;
		// getElementsByTagName gives the corresponding elements in the whole
		// descendance. We want only children

		NodeList children = element.getChildNodes();
		ArrayList goodChildren = new ArrayList();
		for (int i = 0; i < children.getLength(); i++) {
			Node currentChild = children.item( i );
			if ( currentChild.getNodeType() == Node.ELEMENT_NODE
					&& ((Element) currentChild ).getTagName().equals( tagName ) ) {
				goodChildren.add( currentChild );
			}
		}
		return goodChildren;
	}

	   /**
	    * Gets the child of the specified element having the
	    * specified name. If the child with this name doesn't exist
	    * then the supplied default element is returned instead.
	    *
	    * @param element the parent element
	    * @param tagName the name of the desired child
	    * @param defaultElement the element to return if the child
	    *                       doesn't exist
	    * @return either the named child or the supplied default
	    */
	   public static Element getOptionalChild(Element element, String tagName)
	      throws DeploymentException
	   {
	      List goodChildren = getChildrenByTagName(element, tagName);

	      if(goodChildren.size()==1) {
	    	  return (Element) goodChildren.get(0);
	      } else {
	    	  return null;
	      }
	   }
}
