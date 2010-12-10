/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.hibernate.console;

import org.eclipse.datatools.connectivity.ConnectionProfileConstants;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.datatools.connectivity.drivers.DriverInstance;
import org.eclipse.datatools.connectivity.drivers.jdbc.IJDBCDriverDefinitionConstants;

/**
 * @author Vitali Yemialyanchyk
 */
public class ConnectionProfileUtil {

	public static String getDriverDefinitionId(IConnectionProfile profile) {
		if (profile == null) {
			return null;
		}
		return profile.getBaseProperties().getProperty(
				ConnectionProfileConstants.PROP_DRIVER_DEFINITION_ID);
	}

	public static DriverInstance getDriverDefinition(String connectionProfile) {
		if (connectionProfile == null) {
			return null;
		}
		IConnectionProfile profile = ProfileManager.getInstance().getProfileByName(
				connectionProfile);
		if (profile == null) {
			return null;
		}
		String driverID = getDriverDefinitionId(profile);
		return org.eclipse.datatools.connectivity.drivers.DriverManager.getInstance()
				.getDriverInstanceByID(driverID);
	}

	/*
	 * try get a path to the sql driver jar file from DTP connection profile
	 */
	public static String[] getConnectionProfileDriverURL(String connectionProfile) {
		DriverInstance di = getDriverDefinition(connectionProfile);
		if (di == null) {
			return null;
		}
		return di.getJarListAsArray();
	}

	public static String getDriverClass(String connectionProfile) {
		DriverInstance di = getDriverDefinition(connectionProfile);
		String driverClass = di != null ? 
			di.getProperty(IJDBCDriverDefinitionConstants.DRIVER_CLASS_PROP_ID) : ""; //$NON-NLS-1$
		return driverClass;
	}
}
