package org.jboss.tools.hibernate.jpt.core.internal.jpa2.context.definition;

import org.eclipse.jpt.jpa.core.context.java.JavaAttributeMappingDefinition;
import org.eclipse.jpt.jpa.core.context.java.JavaPersistentAttribute;
import org.eclipse.jpt.jpa.core.internal.context.java.JavaAttributeMappingDefinitionWrapper;
import org.jboss.tools.hibernate.jpt.core.internal.context.definition.HibernateJavaIdMappingDefinition;

public class HibernateJavaIdMappingDefinition2_0 extends JavaAttributeMappingDefinitionWrapper {

	private static final HibernateJavaIdMappingDefinition DELEGATE = HibernateJavaIdMappingDefinition.instance();

	// singleton
	private static final HibernateJavaIdMappingDefinition2_0 INSTANCE = new HibernateJavaIdMappingDefinition2_0();

	/**
	 * Return the singleton.
	 */
	public static HibernateJavaIdMappingDefinition2_0 instance() {
		return INSTANCE;
	}

	/**
	 * Enforce singleton usage
	 */
	private HibernateJavaIdMappingDefinition2_0() {
		super();
	}
	

	@Override
	protected JavaAttributeMappingDefinition getDelegate() {
		return DELEGATE;
	}
	
	/**
	 * The annotation is "specified" only if it is not "derived" (i.e.
	 * accompanied by a M-1 or 1-1 annotation).
	 */
	@Override
	public boolean isSpecified(JavaPersistentAttribute persistentAttribute) {
		boolean idSpecified = super.isSpecified(persistentAttribute);
		return idSpecified && ! this.isDerivedId(persistentAttribute);
	}

	/**
	 * Return whether the specified attribute's <code>Id</code> annotation is
	 * a supporting annotation for M-1 or 1-1 mapping, as opposed to a primary
	 * mapping annotation.
	 * <p>
	 * This might produce confusing behavior if the annotations look something
	 * like:<pre>
	 *     @Id @Basic @ManyToOne private int foo;
	 * </pre>
	 */
	private boolean isDerivedId(JavaPersistentAttribute persistentAttribute) {
		return this.attributeHasManyToOneMapping(persistentAttribute) ||
			this.attributeHasOneToOneMapping(persistentAttribute);
	}

	private boolean attributeHasManyToOneMapping(JavaPersistentAttribute persistentAttribute) {
		return HibernateJavaManyToOneMappingDefinition2_0.instance().isSpecified(persistentAttribute);
	}

	private boolean attributeHasOneToOneMapping(JavaPersistentAttribute persistentAttribute) {
		return HibernateJavaOneToOneMappingDefinition2_0.instance().isSpecified(persistentAttribute);
	}
		
}
