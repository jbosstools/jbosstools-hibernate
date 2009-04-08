/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.hibernate.eclipse.console.test;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.hibernate.eclipse.console.EclipseLaunchConsoleConfigurationPreferences;
import org.hibernate.eclipse.launch.IConsoleConfigurationLaunchConstants;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.osgi.framework.Bundle;

import junit.framework.TestCase;

/**
 * 
 * 
 * @author Vitali Yemialyanchyk
 */
public class EclipseLaunchConsoleConfigurationPreferencesTest extends TestCase {
	
	public Mockery context = new Mockery() {{
		setImposteriser(ClassImposteriser.INSTANCE);
	}};

	static public String getPropertiesFilePath() {
		Bundle bundle = HibernateConsoleTestPlugin.getDefault().getBundle();
		String resPath = null;
		try {
			URL url = FileLocator.resolve(bundle.getEntry("/res/LaunchCC.properties")); //$NON-NLS-1$
			resPath = url.getPath();
		} catch (IOException e) {
			//ignore
		}
		return resPath;
	}
	
	/**
	 * test method for eclipseLaunchConsoleConfigurationPreferences.getProperties
	 * @throws CoreException
	 */
	public void testCloseStream() throws CoreException {

		final ILaunchConfiguration launchConfiguration = context.mock(ILaunchConfiguration.class);

		final EclipseLaunchConsoleConfigurationPreferences eclipseLaunchConsoleConfigurationPreferences =
			new EclipseLaunchConsoleConfigurationPreferences(launchConfiguration);
		final String propertiesFilePath = getPropertiesFilePath();
		
		context.checking(new Expectations() {{
        	
        	allowing(launchConfiguration).getAttribute(IConsoleConfigurationLaunchConstants.PROPERTY_FILE, (String)null);
        	will(returnValue(propertiesFilePath));
        }});
		Properties properties = eclipseLaunchConsoleConfigurationPreferences.getProperties();
		assertNotNull(properties);
		assertTrue(properties.size() == 1);
        context.assertIsSatisfied();
		
	}

}
