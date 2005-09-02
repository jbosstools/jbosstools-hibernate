package org.hibernate.eclipse.mapper.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.hibernate.eclipse.console.model.IReverseEngineeringDefinition;
import org.hibernate.eclipse.console.model.ITableFilter;
import org.hibernate.eclipse.mapper.factory.ElementAdapterFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ReverseEngineeringDefinitionElement extends HibernateElement implements IReverseEngineeringDefinition {
	
	
	public ReverseEngineeringDefinitionElement(IDOMNode node) {
		super(node);
	}
	
	public void add(HibernateElement adapter) {
		if(adapter instanceof TableFilterElement) {
			pcs.firePropertyChange("tableFilters", null, adapter);			
		}
	}
	
	public void remove(HibernateElement adapter) {
		if(adapter instanceof TableFilterElement) {
			pcs.firePropertyChange("tableFilters", adapter, null);
		}
	}
	
	public List getTableFiltersList() {
		List result = new ArrayList();
		NodeList list = getNode().getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			INodeAdapter adapter = ((IDOMNode)list.item(i)).getAdapterFor(HibernateElement.class);
			if (adapter instanceof TableFilterElement) {
				result.add(adapter);
			}
		}
		return result;
	}

	public ITableFilter createTableFilter() {
		IDOMNode result = (IDOMNode)getNode().getOwnerDocument().createElement("table-filter");
		return (ITableFilter) ElementAdapterFactory.getDefault().adapt(result);
	}

	protected void addElementBefore(IDOMNode result, IDOMNode before) {
		IDOMNode parentNode = getNode();
		addElementBefore( parentNode, result, before);
	}

	protected void addElementBefore(Node parentNode, Node element, Node before) {
		if (before == null) {
			parentNode.appendChild(element);
		} else {
			parentNode.insertBefore(element, before);
		}
	/*	if(before==null) {
			IDOMNode text = (IDOMNode)parentNode.getOwnerDocument().createTextNode("\n" + getPaddingString(getLevel()));
			parentNode.insertBefore(text, element);
		} else if (!isTextNodeWithLinebreak( before )) {
			IDOMNode text = (IDOMNode)parentNode.getOwnerDocument().createTextNode("\n" + getPaddingString(getLevel()));
			parentNode.insertBefore(text, before);			
		}*/
	}
	

	private boolean isTextNodeWithLinebreak(Node before) {
		if(before!=null  && before.getNodeType()==Node.TEXT_NODE) {
			boolean val = before.getNodeValue().indexOf('\n')>=0;
			return val;
		} else {
			return false;
		}
	}

	public void addTableFilter(ITableFilter filter) {
		IDOMNode last = (IDOMNode)getNode().getLastChild();
		/*if (last == null) {
			last = (IDOMNode)getNode().getOwnerDocument().createTextNode("\n" + getPaddingString(getLevel() - 1));
			getNode().appendChild(last);
		}*/
		addElementBefore(((TableFilterElement)filter).getNode(), last);			
	}

	public void removeTableFilter(ITableFilter item) {
		Node node2 = getNode().removeChild(((TableFilterElement)item).getNode());
		System.out.println(node2);
		pcs.firePropertyChange("tableFilterList", item, null);
	}

	public void moveTableFilterDown(ITableFilter item) {
		TableFilterElement tfe = (TableFilterElement)item;
		
		Node nextSibling = getNextNamedSibling( tfe.getNode(), "table-filter" );
		if(nextSibling!=null) {
			addElementBefore(tfe.getNode().getParentNode(), nextSibling, tfe.getNode());			
		}
		pcs.firePropertyChange("tableFilters",null, null);
	}

	private Node getNextNamedSibling(Node node, String nodeName) {
		Node nextSibling = node.getNextSibling();
		while(nextSibling!=null && !nextSibling.getNodeName().equals(nodeName)) {
			nextSibling = nextSibling.getNextSibling();
		}
		return nextSibling;
	}

	private Node getPreviousNamedSibling(Node node, String nodeName) {
		Node nextSibling = node.getPreviousSibling();
		while(nextSibling!=null && !nextSibling.getNodeName().equals(nodeName)) {
			nextSibling = nextSibling.getPreviousSibling();
		}
		return nextSibling;
	}
	
	public void moveTableFilterUp(ITableFilter item) {
		TableFilterElement tfe = (TableFilterElement)item;
		
		Node sibling = getPreviousNamedSibling( tfe.getNode(), "table-filter");
		if(sibling!=null) {
			addElementBefore(tfe.getNode().getParentNode(), tfe.getNode(), sibling);			
		}
		pcs.firePropertyChange("tableFilters",null, null);
	}

	public ITableFilter[] getTableFilters() {
		return (ITableFilter[]) getTableFiltersList().toArray(new ITableFilter[0]);
	}
}

