package org.hibernate.eclipse.console.workbench;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.progress.IDeferredWorkbenchAdapter;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.utils.EclipseImages;
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
import org.hibernate.mapping.Property;
import org.hibernate.mapping.QueryList;
import org.hibernate.mapping.Set;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Value;
import org.hibernate.mapping.ValueVisitor;

public class PropertyWorkbenchAdapter extends BasicWorkbenchAdapter implements
		IDeferredWorkbenchAdapter {

	public Object[] getChildren(Object o) {
		Property p = (Property) o;
		
		Object[] result = (Object[]) p.getValue().accept(new ValueVisitor() {
		
			public Object accept(OneToOne oto) {
				return NO_CHILDREN;
			}
		
			public Object accept(ManyToOne mto) {
				return NO_CHILDREN;
			}
		
			public Object accept(Component component) {
				return toArray(component.getPropertyIterator(), Property.class);				
			}
		
			public Object accept(DependantValue value) {
				return NO_CHILDREN;
			}
		
			public Object accept(SimpleValue value) {
				return NO_CHILDREN;
			}
		
			public Object accept(Any any) {
				return NO_CHILDREN;
			}
		
			public Object accept(Set set) {
				return NO_CHILDREN; // should it look up the target entity?
			}
		
			public Object accept(QueryList list) {
				return NO_CHILDREN;
			}
		
			public Object accept(OneToMany many) {
				return NO_CHILDREN;
			}
		
			public Object accept(Map map) {
				return NO_CHILDREN;
			}
		
			public Object accept(Array list) {
				return NO_CHILDREN;
			}
		
			public Object accept(PrimitiveArray primitiveArray) {
				return NO_CHILDREN;
			}
		
			public Object accept(List list) {
				return NO_CHILDREN;
			}
		
			public Object accept(IdentifierBag bag) {
				return NO_CHILDREN;
			}
		
			public Object accept(Bag bag) {
				return NO_CHILDREN;
			}		
		});
		
		return result;
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		Property property = ((Property)object);
		
		if(property.getPersistentClass()!=null) {
			if(property.getPersistentClass().getIdentifierProperty()==property) {
				return EclipseImages.getImageDescriptor(ImageConstants.IDPROPERTY);
			}
		}
		Value v = property.getValue();
		return EclipseImages.getImageDescriptor(getIconNameForValue(v));
	}

	public String getLabel(Object o) {
		Property property = ((Property)o);
		Value value = property.getValue();
		if (value.isSimpleValue()) {
			SimpleValue sv = (SimpleValue)value;
			
			if(sv.getTypeName()!=null) {
				return property.getName() + " : " + sv.getTypeName();	
			}
			
		}
		return property.getName(); 
	}

	public Object getParent(Object o) {
		Property p = (Property) o;
		return p.getPersistentClass();
	}

	static public String getIconNameForValue(Value value) {
		String result;
		
		result = (String) value.accept(new ValueVisitor() {
		
			public Object accept(OneToOne oto) {
				return ImageConstants.ONETOONE;
			}
		
			public Object accept(ManyToOne mto) {
				return ImageConstants.MANYTOONE;
			}
		
			public Object accept(Component component) {
				return ImageConstants.COMPONENT;
			}
		
			public Object accept(DependantValue value) {
				return ImageConstants.UNKNOWNPROPERTY;
			}
		
			public Object accept(SimpleValue value) {
				return ImageConstants.PROPERTY;
			}
		
			public Object accept(Any any) {
				return ImageConstants.PROPERTY;
			}
		
			public Object accept(Set set) {
				return ImageConstants.MANYTOONE;
			}
		
			public Object accept(QueryList list) {
				return ImageConstants.UNKNOWNPROPERTY;
			}
		
			public Object accept(OneToMany many) {
				return ImageConstants.ONETOMANY;
			}
		
			public Object accept(Map map) {
				return ImageConstants.MANYTOONE;
			}
		
			public Object accept(Array list) {
				return ImageConstants.MANYTOONE;
			}
		
			public Object accept(PrimitiveArray primitiveArray) {
				return ImageConstants.MANYTOONE;			
			}
		
			public Object accept(List list) {
				return ImageConstants.MANYTOONE;
			}
		
			public Object accept(IdentifierBag bag) {
				return ImageConstants.MANYTOONE;
			}
		
			public Object accept(Bag bag) {
				return ImageConstants.MANYTOONE;
			}
		});
		
		if(result==null) {
			result = ImageConstants.UNKNOWNPROPERTY;
		}
		return result;
	}
}
