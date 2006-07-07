/*
 * Created on 22-Mar-2005
 *
 */
package org.hibernate.eclipse.console.views.properties;

import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import org.hibernate.Session;
import org.hibernate.console.QueryPage;
import org.hibernate.console.execution.ExecutionContextHolder;
import org.hibernate.eclipse.console.views.QueryPageTabView;
import org.hibernate.proxy.HibernateProxyHelper;

public class HibernatePropertySourceProvider implements IPropertySourceProvider
{	
	// TODO: refactor to be some interface that can provide currentsession and currentconfiguration
	private final QueryPageTabView view;

	public HibernatePropertySourceProvider(QueryPageTabView view) {
		this.view = view;
	}

	public IPropertySource getPropertySource(Object object) {
		if (object==null) {
			return null;
		}
		else if (object instanceof QueryPage)
		{
			return new QueryPagePropertySource( (QueryPage)object);
		}
		else if (object instanceof CollectionPropertySource) {
			return (IPropertySource) object;
		}
		else {
			//			 maybe we should be hooked up with the queryview to get this ?
			Session currentSession = view.getSelectedQueryPage().getSession();
			ExecutionContextHolder currentConfiguration = view.getSelectedQueryPage().getConsoleConfiguration();
			if((currentSession.isOpen() && currentSession.contains(object)) || hasMetaData( object, currentSession) ) {
				return new EntityPropertySource(object, currentSession, currentConfiguration);	
			} else {
				return null;
			}
			
		}
		
	}

	private boolean hasMetaData(Object object, Session currentSession) {
		return currentSession.getSessionFactory().getClassMetadata(HibernateProxyHelper.getClassWithoutInitializingProxy(object))!=null;
	}
}