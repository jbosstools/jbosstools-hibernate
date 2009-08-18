package org.jboss.tools.hibernate.jpt.core.internal.context.java;

import org.eclipse.jpt.core.context.java.JavaColumn;
import org.eclipse.jpt.core.context.java.JavaJpaContextNode;
import org.eclipse.jpt.core.internal.context.java.GenericJavaColumn;
import org.hibernate.cfg.NamingStrategy;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJpaProject;

@SuppressWarnings("restriction")
public class HibernateJavaColumn extends GenericJavaColumn {

	public HibernateJavaColumn(JavaJpaContextNode parent, JavaColumn.Owner owner) {
		super(parent, owner);
	}
	
	@Override
	public HibernateJpaProject getJpaProject() {
		return (HibernateJpaProject) super.getJpaProject();
	}
		
	@Override
	protected String buildDefaultName() {
		NamingStrategy namingStrategy = getJpaProject().getNamingStrategy();
		if (namingStrategy != null && super.buildDefaultName() != null){
				return namingStrategy.propertyToColumnName(super.buildDefaultName());
		}
		return super.buildDefaultName();
	}

}
