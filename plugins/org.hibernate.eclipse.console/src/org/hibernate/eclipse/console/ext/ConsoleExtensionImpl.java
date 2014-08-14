package org.hibernate.eclipse.console.ext;

import java.io.File;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.ui.views.properties.IPropertySource;
import org.hibernate.console.QueryPage;
import org.hibernate.eclipse.console.common.HibernateExtension;

public class ConsoleExtensionImpl implements ConsoleExtension {

	@Override
	public CompletionProposalsResult hqlCodeComplete(String query,
			int startPosition, int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setHibernateExtention(HibernateExtension hibernateExtension) {
		// TODO Auto-generated method stub

	}

	@Override
	public Map<String, File[]> launchExporters(
			ILaunchConfiguration configuration, String mode, ILaunch launch,
			IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPropertySource getPropertySource(Object object,
			QueryPage selectedQueryPage) {
		// TODO Auto-generated method stub
		return null;
	}

}
