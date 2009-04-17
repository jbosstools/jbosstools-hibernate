package org.jboss.tools.hibernate.jpt.core.internal;

import org.eclipse.jpt.core.JpaAnnotationProvider;
import org.eclipse.jpt.core.JpaFactory;
import org.eclipse.jpt.core.JpaPlatform;
import org.eclipse.jpt.core.JpaPlatformFactory;
import org.eclipse.jpt.core.JpaValidation;
import org.eclipse.jpt.core.JpaValidation.Supported;
import org.eclipse.jpt.core.internal.platform.GenericJpaAnnotationDefinitionProvider;
import org.eclipse.jpt.core.internal.platform.GenericJpaAnnotationProvider;
import org.eclipse.jpt.core.internal.platform.GenericJpaFactory;
import org.eclipse.jpt.core.internal.platform.GenericJpaPlatform;
import org.eclipse.jpt.core.internal.platform.GenericJpaPlatformProvider;

public class HibernateJpaPlatformFactory implements JpaPlatformFactory {

	/**
	 * zero-argument constructor
	 */
	public HibernateJpaPlatformFactory() {
		super();
	}
	
	public JpaPlatform buildJpaPlatform(String id) {
		return new HibernateJpaPlatform(
			id,
			buildJpaFactory(), 
			buildJpaAnnotationProvider(), 
			buildJpaValidation(),
			GenericJpaPlatformProvider.instance());
	}
	
	protected JpaFactory buildJpaFactory() {
		return new HibernateJpaFactory();
	}
	
	protected JpaAnnotationProvider buildJpaAnnotationProvider() {
		return new GenericJpaAnnotationProvider(
			GenericJpaAnnotationDefinitionProvider.instance(),
			HibernateJpaAnnotationDefinitionProvider.instance());
	}
	
	protected JpaValidation buildJpaValidation() {
		return new JpaValidation() {
			public Supported getTablePerConcreteClassInheritanceIsSupported() {
				return Supported.MAYBE;
			}
		};
	}

}
