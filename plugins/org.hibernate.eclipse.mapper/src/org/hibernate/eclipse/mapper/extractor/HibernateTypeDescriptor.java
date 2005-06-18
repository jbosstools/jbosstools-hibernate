/**
 * 
 */
package org.hibernate.eclipse.mapper.extractor;

/**
 * Internal class for describing hibernate types.
 * 
 */
class HibernateTypeDescriptor implements Comparable {

	final String name;
	final String returnClass;
	final String primitiveClass;

	public HibernateTypeDescriptor(String name, String returnClass, String primitiveClass) {
		this.name = name;
		this.returnClass = returnClass;
		this.primitiveClass = primitiveClass;
	}

	public String getName() {
		return name;
	}
	

	public String getPrimitiveClass() {
		return primitiveClass;
	}
	

	public String getReturnClass() {
		return returnClass;
	}

	public int compareTo(Object o) {
		
		return name.compareTo( ( (HibernateTypeDescriptor)o).getName() );
	}
	
	
}