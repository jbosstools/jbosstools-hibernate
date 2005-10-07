package org.hibernate.eclipse.mapper.model;

import java.util.List;

import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.hibernate.eclipse.console.model.IRevEngColumn;
import org.hibernate.eclipse.console.model.IRevEngPrimaryKey;
import org.hibernate.eclipse.console.model.IRevEngTable;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class RevEngTableAdapter extends DOMAdapter implements IRevEngTable {

	public RevEngTableAdapter(Node item, DOMReverseEngineeringDefinition model) {
		super(item, model);
	}

	public String getCatalog() {
		String attrib = "catalog";
		String nullValue = null;		
		return getNodeValue( attrib, nullValue );
	}

	public String getSchema() {
		return getNodeValue("schema", null);
	}

	public String getName() {
		return getNodeValue("name", null);
	}

	public IRevEngPrimaryKey getPrimaryKey() {
		String elementName = "primary-key";
		return (IRevEngPrimaryKey) getAdaptedElement( elementName );
	}

	private Object getAdaptedElement(String elementName) {
		List adaptedElements = getAdaptedElements((Element) getNode(), elementName);
		if(adaptedElements.isEmpty()) {
			return null;
		} else {
			return adaptedElements.get(0);
		}
	}

	public IRevEngColumn[] getColumns() {
		return (IRevEngColumn[]) getColumnList().toArray(new IRevEngColumn[0]);
	}
	
	private List getColumnList() {
		return getAdaptedElements( (Element) getNode(), "column" );
	}

	public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
		getModel().tablesChanged(notifier);
		if(changedFeature==null) {
			firePropertyChange("unknown-changed-feature", oldValue, newValue);
		} else {
			firePropertyChange(((Node)changedFeature).getNodeName(), oldValue, newValue);
		}
	}

	public void setName(String value) {
		setAttribute("name", value, "");
	}

	public void setCatalog(String value) {	
		setAttribute("catalog", value, null);
	}

	public void setSchema(String value) {
		setAttribute("schema", value, null);		
	}
	
	public void addColumn(IRevEngColumn revCol) {
		getNode().appendChild(((RevEngColumnAdapter)revCol).getNode());
		DOMModelUtil.formatNode(getNode().getParentNode());
	}

	public void addPrimaryKey() {
		DOMAdapter key = (DOMAdapter) getModel().createPrimaryKey();
		getNode().insertBefore(key.getNode(), getNode().getFirstChild());
		DOMModelUtil.formatNode(getNode().getParentNode());		
	}
}
