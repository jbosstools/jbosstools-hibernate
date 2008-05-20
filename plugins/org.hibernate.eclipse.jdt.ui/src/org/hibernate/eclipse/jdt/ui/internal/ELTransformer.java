package org.hibernate.eclipse.jdt.ui.internal;

public class ELTransformer {

	/**
	 * transform any #{el expressions} into named parameters so HQL validation won't fail on it.
	 * @param hql
	 * @return
	 */
	static public String removeEL(String hql) {
		int elStart = hql.indexOf("#{");
		int next = hql.indexOf("}", elStart);
		
		while(elStart!=-1 && next!=-1) {
		  	String result = hql.substring(0, elStart);
		  	result += ":_" + hql.substring(elStart+2, next).replaceAll("[^\\p{javaJavaIdentifierStart}]","_") + "_";
		  	result += hql.substring(next+1); 
		  	
		  	hql = result;
		  	
			elStart = hql.indexOf("#{");
			next = hql.indexOf("}", elStart);
		}
		
		return hql;
	}
}
