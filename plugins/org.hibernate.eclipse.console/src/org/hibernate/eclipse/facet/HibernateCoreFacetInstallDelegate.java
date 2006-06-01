package org.hibernate.eclipse.facet;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

public class HibernateCoreFacetInstallDelegate implements IDelegate {

	public void execute(IProject project, IProjectFacetVersion fv,
			Object config, IProgressMonitor monitor) throws CoreException {
		System.out.println(config);

	}

}
