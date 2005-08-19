package org.hibernate.eclipse.console.workbench;

import org.hibernate.mapping.Any;
import org.hibernate.mapping.Array;
import org.hibernate.mapping.Bag;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.DependantValue;
import org.hibernate.mapping.IdentifierBag;
import org.hibernate.mapping.List;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.Map;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.OneToOne;
import org.hibernate.mapping.PrimitiveArray;
import org.hibernate.mapping.Set;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.ValueVisitor;

public class TypeNameValueVisitor implements ValueVisitor {

	/** if true then only return the classname, not the fully qualified classname */
	final boolean dequalify; 
	
	TypeNameValueVisitor(boolean dequalify) {
		this.dequalify=dequalify;
	}
	
	public Object accept(Bag bag) {
		return "Bag <" + bag.getElement().accept(this) + ">";
	}

	public Object accept(IdentifierBag bag) {
		return "IdBag <" + bag.getElement().accept(this) + ">";
	}

	public Object accept(List list) {
		return "List <" + list.getElement().accept(this) + ">";
	}

	public Object accept(PrimitiveArray primitiveArray) {
		return primitiveArray.getElement().accept(this) + "[]";
	}

	public Object accept(Array list) {
		return list.getElement().accept(this) + "[]";
	}

	public Object accept(Map map) {
		return "Map<" + map.getElement().accept(this) + ">";
	}

	public Object accept(OneToMany many) {
		return dequalify(many.getReferencedEntityName());
	}

	private String dequalify(String referencedEntityName) {
		if(dequalify && referencedEntityName!=null && referencedEntityName.indexOf(".")>=0) {			
			return referencedEntityName.substring(referencedEntityName.lastIndexOf('.')+1);
		}
		return referencedEntityName;
	}

	public Object accept(Set set) {
		return "Set<" + set.getElement().accept(this) + ">";
	}

	public Object accept(Any any) {
		return "Any";
	}

	public Object accept(SimpleValue value) {
		return dequalify(value.getTypeName());
	}

	public Object accept(DependantValue value) {
		return null;
	}

	public Object accept(Component component) {
		return dequalify(component.getComponentClassName());
	}

	public Object accept(ManyToOne mto) {
		return dequalify(mto.getReferencedEntityName());
	}

	public Object accept(OneToOne oto) {
		return dequalify(oto.getEntityName());
	}

}
