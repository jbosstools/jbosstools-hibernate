/**
 * 
 */
package org.hibernate.eclipse.mapper.model;

import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.hibernate.eclipse.console.model.ITableFilter;
import org.w3c.dom.Node;

public class TableFilterAdapter extends DOMAdapter implements ITableFilter {

	public TableFilterAdapter(Node node, DOMReverseEngineeringDefinition revEngDef) {
		super(node, revEngDef);
	}
	
	public void setExclude(Boolean exclude) {
		setAttribute("exclude", exclude==null?"false":exclude.toString(), "false");					
	}

	public void setMatchCatalog(String catalog) {
		setAttribute( "match-catalog", catalog, ".*" );
	}

	public void setMatchSchema(String schema) {
		setAttribute("match-schema", schema, ".*");		
	}

	public void setMatchName(String name) {
		setAttribute("match-name", name, null);		
	}

	public Boolean getExclude() {
		Node type = getNode().getAttributes().getNamedItem("exclude");
		return type == null ? Boolean.FALSE : Boolean.valueOf(type.getNodeValue());
	}

	public String getMatchCatalog() {
		Node type = getNode().getAttributes().getNamedItem("match-catalog");
		return type == null ? ".*" : type.getNodeValue();
	}

	public String getMatchSchema() {
		Node type = getNode().getAttributes().getNamedItem("match-schema");
		return type == null ? ".*" : type.getNodeValue();
	}

	public String getMatchName() {
		Node type = getNode().getAttributes().getNamedItem("match-name");
		return type == null ? ".*" : type.getNodeValue();	
	}
	
	public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
		getModel().tableFilterChanged(notifier);	
	}
}