/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.ui.bot.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import org.jboss.reddeer.common.platform.OS;
import org.jboss.reddeer.common.platform.RunningPlatform;

/**
 * Utility class for manipulating with test database
 *
 */
public class DatabaseUtils {

	private static final Logger LOG = Logger.getLogger(DatabaseUtils.class.getName());
	private static Process sakila;
	private static String jarName = "h2-1.3.161.jar";
	private static final String DB_URL = "jdbc:h2:tcp://localhost/sakila";
	private static final String USER = "sa";
	private static final String PASS = "";

	/**
	 * Start sakila db located in a set folder
	 * 
	 * @param dbFolder
	 *            folder the database is located in
	 */
	public static void runSakilaDB(String dbFolder) {
		if (sakila == null) {
			try {
				String path = new File(dbFolder).getCanonicalPath();

				ProcessBuilder pb = new ProcessBuilder("java", "-cp", jarName, "org.h2.tools.Server",
						"-ifExists", "-tcp", "-web");
				pb.directory(new File(path));
				sakila = pb.start();
				LOG.info("Starting h2 server - jdbc url: " + DB_URL);

				// check if the process really started
				long startTime = System.currentTimeMillis();
				boolean isAlive = false;
				boolean isAcceptingConnections = false;
				while (System.currentTimeMillis() - startTime < 100000) {
					if (isRunning(sakila)) {
						isAlive = true;
						if(isAcceptingConnections(path + File.separator + jarName)){
							isAcceptingConnections = true;
							break;	
						}
					}
					Thread.sleep(200);
				}
				if (!isAlive || !isAcceptingConnections) {
					throw new RuntimeException("Sakila Database startup failed");
				}

				LOG.info("Sakila DB started");
			} catch (IOException | InterruptedException e) {
				LOG.warning("An error occured starting sakila DB");
				throw new RuntimeException(e);
			}
		}
	}

	
	/**
	 * Stop the sakila database instance if it exists
	 */
	public static void stopSakilaDB() {
		// destroy the process itself
		if (sakila != null) {
			sakila.destroy();
			sakila = null;

			try {
				List<String> processes = new ArrayList<String>();
				Process p = Runtime.getRuntime().exec("jps -l");
				p.waitFor();
				BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

				String line;
				while ((line = input.readLine()) != null) {
					processes.add(line);
				}
				input.close();

				// and kill the java process it created
				for (String process : processes) {
					if (process.contains("org.h2.tools.Server")) {
						int pid = Integer.parseInt(process.split(" ")[0]);
						Process proc = null;
						if (RunningPlatform.isOperationSystem(OS.WINDOWS)) {
							proc = Runtime.getRuntime().exec("taskkill /f /pid " + pid);
						} else {
							proc = Runtime.getRuntime().exec("kill -9 " + pid);
						}
						proc.waitFor();
						break;
					}
				}
			} catch (IOException | InterruptedException e) {
				LOG.warning("An error occured stopping Sakila DB");
				throw new RuntimeException(e);
			}
		} else {
			LOG.warning("Sakila DB is not running");
		}
	}

	private static boolean isRunning(Process process) {
		try {
			process.exitValue();
			return false;
		} catch (IllegalThreadStateException e) {
			return true;
		}
	}
	
	public static boolean isAcceptingConnections(String driverJar){		
		try{
			File driverJarFile = new File(driverJar); 
			URL[] urls = new URL[]{driverJarFile.toURL()};
			ClassLoader cl = new URLClassLoader(urls, ClassLoader.getSystemClassLoader());
			Driver driver = (Driver) Class.forName("org.h2.Driver", true, cl).newInstance();
			Properties props = new Properties();
			props.put("user", USER);
			props.put("password", PASS);
			driver.connect(DB_URL, props);
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
			return false;
		}
		catch (SQLException e) {
	    	LOG.info(e.getMessage());
			return false;
		} 
		catch (InstantiationException e) {
			e.printStackTrace();
			return false;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}