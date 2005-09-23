package org.hibernate.eclipse.mapper.model;

import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.hibernate.eclipse.console.model.IRevEngColumn;
import org.w3c.dom.Node;

public class RevEngColumnAdapter extends DOMAdapter implements IRevEngColumn {
	
	public RevEngColumnAdapter(Node node, DOMReverseEngineeringDefinition model) {
		super( node, model );
	}

	public String getJDBCType() {
		return getNodeValue("jdbc-type", null);
	}

	public String getType() {
		return getNodeValue("type", null);
	}

	public String getPropertyName() {
		return getNodeValue("property", null);
	}

	public boolean getExclude() {
		String nodeValue = getNodeValue("exclude", "false");
		return nodeValue==null ? false : Boolean.valueOf(nodeValue).booleanValue();
	}

	public String getName() {
		return getNodeValue("name", null);
	}

	public void setName(String value) {
		setAttribute("name", value, "");		
	}

	public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
		getModel().tablesChanged(notifier);
		firePropertyChange(((Node)changedFeature).getNodeName(), oldValue, newValue);
	}

	public void setPropertyName(String value) {
		setAttribute("property", value, "");		
	}

	public void setJDBCType(String value) {
		setAttribute("jdbc-type", value, "");		
	}

	public void setType(String value) {
		setAttribute("type", value, "");		
	}

	public void setExcluded(boolean selection) {
		setAttribute("exclude", ""+selection, "false");		
	}

}
