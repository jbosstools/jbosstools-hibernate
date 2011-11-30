/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.hibernate.eclipse.console.ext;

import java.io.File;
import java.util.Properties;

/**
 * @author Dmitry Geraskov
 *
 */
public interface IExporter {

	
	/**
	 * @param file basedirectory to be used for generated files.
	 */
	public void setOutputDirectory(File file);

	public File getOutputDirectory();
	
	/**
	 * @param templatePath array of directories used sequentially to lookup templates
	 */
	public void setTemplatePath(String[] templatePath);
	
	public String[] getTemplatePath();
	
	/**
	 * 
	 * @param properties set of properties to be used by exporter.
	 */
	public void setProperties(Properties properties);
	
	public Properties getProperties();
	
	/**
	 * 
	 * @param collector Instance to be consulted when adding a new file.
	 */
	public void setArtifactCollector(IArtifactCollector collector);
	
	/**
	 * 
	 * @return artifact collector
	 */
	public IArtifactCollector getArtifactCollector();
	
	/**
	 * Called when exporter should start generating its output
	 */
	public void start();

}
