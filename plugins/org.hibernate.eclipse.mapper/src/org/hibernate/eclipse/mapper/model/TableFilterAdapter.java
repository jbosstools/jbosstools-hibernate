/**
 * 
 */
package org.hibernate.eclipse.mapper.model;

import org.hibernate.eclipse.console.model.ITableFilter;
import org.w3c.dom.Node;

class TableFilterAdapter extends DOMAdapter implements ITableFilter {

	public TableFilterAdapter(Node node) {
		super(node);
	}
	
	public void setExclude(Boolean exclude) {
		setAttribute("exclude", exclude==null?"false":"true", "false");					
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
		Node type = node.getAttributes().getNamedItem("exclude");
		return type == null ? Boolean.FALSE : Boolean.valueOf(type.getNodeValue());
	}

	public String getMatchCatalog() {
		Node type = node.getAttributes().getNamedItem("match-catalog");
		return type == null ? ".*" : type.getNodeValue();
	}

	public String getMatchSchema() {
		Node type = node.getAttributes().getNamedItem("match-schema");
		return type == null ? ".*" : type.getNodeValue();
	}

	public String getMatchName() {
		Node type = node.getAttributes().getNamedItem("match-name");
		return type == null ? ".*" : type.getNodeValue();	
	}
	
}