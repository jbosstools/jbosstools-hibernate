package org.jboss.tools.hibernate.jpt.core.internal.context.java;

import org.eclipse.jpt.core.context.java.JavaColumn;
import org.eclipse.jpt.core.context.java.JavaJpaContextNode;
import org.eclipse.jpt.core.internal.context.java.GenericJavaColumn;
import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.hibernate.cfg.NamingStrategy;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJpaProject;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJptPlugin;
import org.jboss.tools.hibernate.jpt.core.internal.context.Messages;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernatePersistenceUnit.LocalMessage;

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
		NamingStrategy ns = getJpaProject().getNamingStrategy();
		if ( getJpaProject().isNamingStrategyEnabled() && ns != null && super.buildDefaultName() != null){
			try {
				return ns.propertyToColumnName(super.buildDefaultName());
			} catch (Exception e) {
				Message m = new LocalMessage(IMessage.HIGH_SEVERITY, 
						Messages.NAMING_STRATEGY_EXCEPTION, new String[0], null);
				HibernateJptPlugin.logException(m.getText(), e);
			}
		}
		return super.buildDefaultName();
	}

}
