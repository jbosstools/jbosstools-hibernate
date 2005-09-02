package org.hibernate.eclipse.mapper.model;

import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.hibernate.eclipse.console.model.ITableFilter;

public class TableFilterElement extends HibernateElement implements ITableFilter {

	public TableFilterElement(IDOMNode node) {
		super( node );		
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
		IDOMNode type = (IDOMNode)getNode().getAttributes().getNamedItem("exclude");
		return type == null ? Boolean.FALSE : Boolean.valueOf(type.getNodeValue());
	}

	public String getMatchCatalog() {
		IDOMNode type = (IDOMNode)getNode().getAttributes().getNamedItem("match-catalog");
		return type == null ? ".*" : type.getNodeValue();
	}

	public String getMatchSchema() {
		IDOMNode type = (IDOMNode)getNode().getAttributes().getNamedItem("match-schema");
		return type == null ? ".*" : type.getNodeValue();
	}

	public String getMatchName() {
		IDOMNode type = (IDOMNode)getNode().getAttributes().getNamedItem("match-name");
		return type == null ? ".*" : type.getNodeValue();	
	}
}
