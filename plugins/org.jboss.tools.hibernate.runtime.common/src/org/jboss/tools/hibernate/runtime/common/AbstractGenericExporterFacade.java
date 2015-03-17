package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IGenericExporter;

public abstract class AbstractGenericExporterFacade 
extends AbstractFacade 
implements IGenericExporter {

	public AbstractGenericExporterFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}
	
	@Override
	public void setFilePattern(String filePattern) {
		Util.invokeMethod(
				getTarget(), 
				"setFilePattern", 
				new Class[] { String.class }, 
				new Object[] { filePattern });
	}

	@Override
	public void setTemplateName(String templateName) {
		Util.invokeMethod(
				getTarget(), 
				"setTemplateName", 
				new Class[] { String.class }, 
				new Object[] { templateName });
	}

	@Override
	public void setForEach(String foreach) {
		Util.invokeMethod(
				getTarget(), 
				"setForEach", 
				new Class[] { String.class }, 
				new Object[] { foreach });
	}

	@Override
	public String getFilePattern() {
		return (String)Util.invokeMethod(
				getTarget(), 
				"getFilePattern", 
				new Class[] {}, 
				new Object[] {});
	}

}
