package org.jboss.tools.hibernate.jpt.ui.internal.platform;

import org.eclipse.jpt.ui.JpaPlatformUi;
import org.eclipse.jpt.ui.JpaPlatformUiFactory;
import org.eclipse.jpt.ui.internal.GenericJpaPlatformUiProvider;
import org.eclipse.jpt.ui.internal.GenericJpaUiFactory;
import org.eclipse.jpt.ui.internal.platform.generic.GenericJpaPlatformUi;
import org.eclipse.jpt.ui.internal.platform.generic.GenericJpaPlatformUiFactory;
import org.eclipse.jpt.ui.internal.platform.generic.GenericNavigatorProvider;
import org.eclipse.jpt.ui.internal.structure.JavaResourceModelStructureProvider;
import org.eclipse.jpt.ui.internal.structure.PersistenceResourceModelStructureProvider;

public class HibernateJpaPlatformUiFactory implements JpaPlatformUiFactory {

	public HibernateJpaPlatformUiFactory() {
		super();
	}

	public JpaPlatformUi buildJpaPlatformUi() {
		return new HibernateJpaPlatformUi(
			new HibernateJpaUiFactory(),
			new GenericNavigatorProvider(),
			JavaResourceModelStructureProvider.instance(), 
			PersistenceResourceModelStructureProvider.instance(),
			GenericJpaPlatformUiProvider.instance()
		);
	}

}
