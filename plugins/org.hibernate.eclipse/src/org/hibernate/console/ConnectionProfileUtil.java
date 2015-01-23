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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.eclipse.datatools.connectivity.ConnectionProfileConstants;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.datatools.connectivity.drivers.DriverInstance;
import org.eclipse.datatools.connectivity.drivers.jdbc.IJDBCDriverDefinitionConstants;
import org.jboss.tools.hibernate.runtime.spi.IDialect;
import org.jboss.tools.hibernate.runtime.spi.IEnvironment;
import org.jboss.tools.hibernate.runtime.spi.IService;

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
	
	/**
	 * This method extracts connection properties from connection profile and convert them to
	 * hiberante properties (uses other "keys" for them)
	 * @param profile
	 * @return
	 */
	public static Properties getHibernateConnectionProperties(IService service, IConnectionProfile profile){
		Properties props = new Properties();
		IEnvironment environment = service.getEnvironment();
		if (profile != null) {
			final Properties cpProperties = profile.getProperties(profile.getProviderId());
			String driverClass = ConnectionProfileUtil.getDriverClass(profile.getName());
			props.setProperty(environment.getDriver(), driverClass);
			String url = cpProperties.getProperty(IJDBCDriverDefinitionConstants.URL_PROP_ID);
			props.setProperty(environment.getURL(), url);
			String user = cpProperties.getProperty(IJDBCDriverDefinitionConstants.USERNAME_PROP_ID);
			if (null != user && user.length() > 0) {
				props.setProperty(environment.getUser(), user);
			}
			String pass = cpProperties.getProperty(IJDBCDriverDefinitionConstants.PASSWORD_PROP_ID);
			if (null != pass && pass.length() > 0) {
				props.setProperty(environment.getPass(), pass);
			}
		}
		return props;
	}
	
	public static String autoDetectDialect(IService service, Properties properties) {
		IEnvironment environment = service.getEnvironment();		
		if (properties.getProperty(environment.getDialect()) == null) {
			String url = properties.getProperty(environment.getURL());
			String user = properties.getProperty(environment.getUser());
			String pass = properties.getProperty(environment.getPass());
			Connection connection = null;
			try {
				connection = DriverManager.getConnection(url, user, pass);
				// SQL Dialect:
				//note this code potentially could throw class cast exception
				//see https://issues.jboss.org/browse/JBIDE-8192
				//probably when not Hiberante3.5 is used
				IDialect dialect = service.newDialect(properties, connection);
				return dialect.toString();
			} catch (SQLException e) {
				// can't determine dialect
			} finally {
				if (connection != null) {
					try {
						connection.close();
					} catch (SQLException e) {
						// ignore
					}
				}
			}
			return null;
		} else {
			return properties.getProperty(environment.getDialect());
		}
	}
}
