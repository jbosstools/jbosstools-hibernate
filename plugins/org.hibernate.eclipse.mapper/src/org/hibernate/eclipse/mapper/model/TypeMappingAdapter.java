package org.hibernate.eclipse.mapper.model;

import org.hibernate.eclipse.console.model.ITypeMapping;
import org.w3c.dom.Node;

public class TypeMappingAdapter extends DOMAdapter implements ITypeMapping {

	
	public TypeMappingAdapter(Node node) {
		super( node );
	}

	public String getJDBCType() {
		Node type = node.getAttributes().getNamedItem("jdbc-type");
		return type == null ? null : type.getNodeValue();
	}

	public String getHibernateType() {
		Node type = node.getAttributes().getNamedItem("hibernate-type");
		return type == null ? null : type.getNodeValue();
	}

	public Integer getLength() {
		String name = "length";
		return getInteger( name );
	}

	private Integer getInteger(String name) {
		Node type = node.getAttributes().getNamedItem(name);
		if(type == null) {
			return null;
		} else {
			try {
				Integer decode = Integer.decode(type.getNodeValue());
				return decode;
			} catch (NumberFormatException nfe) {
				return null;
			}
		}		
	}

	public Integer getPrecision() {
		return getInteger( "precision" );
	}

	public Integer getScale() {
		return getInteger( "scale" );
	}

	public void setJDBCType(String string) {
		setAttribute("jdbc-type", string, null);
	}

	public void setLength(Integer string) {
		setAttribute("length", string==null?null:string.toString(), null);
	}

	public void setHibernateType(String string) {
		setAttribute("hibernate-type", string, "");
	}

	public void setPrecision(Integer string) {
		setAttribute("precision", string==null?null:string.toString(), null);
	}

	public void setScale(Integer string) {
		setAttribute("scale", string==null?null:string.toString(), null);
	}

}
