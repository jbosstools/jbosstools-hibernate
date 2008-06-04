package org.hibernate.eclipse.jdt.ui.internal;

public class ELTransformer {

	/**
	 * transform any #{el expressions} into named parameters so HQL validation won't fail on it.
	 * @param hql
	 * @return
	 */
	static public String removeEL(String hql) {
		int elStart = hql.indexOf("#{"); //$NON-NLS-1$
		int next = hql.indexOf("}", elStart); //$NON-NLS-1$
		
		while(elStart!=-1 && next!=-1) {
		  	String result = hql.substring(0, elStart);
		  	result += ":_" + hql.substring(elStart+2, next).replaceAll("[^\\p{javaJavaIdentifierStart}]","_") + "_"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		  	result += hql.substring(next+1); 
		  	
		  	hql = result;
		  	
			elStart = hql.indexOf("#{"); //$NON-NLS-1$
			next = hql.indexOf("}", elStart); //$NON-NLS-1$
		}
		
		return hql;
	}
}
