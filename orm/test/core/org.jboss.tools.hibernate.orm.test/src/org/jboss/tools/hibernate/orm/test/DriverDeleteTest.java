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
package org.jboss.tools.hibernate.orm.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Enumeration;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.hibernate.console.ConsoleConfigClassLoader;
import org.hibernate.console.execution.DefaultExecutionContext;
import org.hibernate.console.execution.ExecutionContext;
import org.hibernate.util.xpl.ReflectHelper;
import org.jboss.tools.hibernate.orm.test.utils.GarbageCollectionUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.osgi.framework.Bundle;

/**
 * The class intended to reproduce the reason of 
 * http://opensource.atlassian.com/projects/hibernate/browse/HBX-936
 * and road map how to fix it.
 *  
 * @author Vitali Yemialyanchyk
 * @author koen
 */
public class DriverDeleteTest {

	private static final String DRIVER_TEST_NAME = "hsqldb-2.4.0.jar"; 
	private static final String DRIVER_TEST_CLASS = "org.hsqldb.jdbcDriver"; 
	private static final String CONNECTION_USERNAME = "sa"; 
	private static final String CONNECTION_PASSWORD = ""; 
	private static final String CONNECTION_URL = "jdbc:hsqldb:."; 
	private static final String DRIVER_GET_PATH = "lib/" + DRIVER_TEST_NAME; 
	private static final String DRIVER_PUT_PATH = "target/" + DRIVER_TEST_NAME; 
	private static final String PUT_PATH = "target"; 

	@Rule 
	public TestName testName = new TestName();
	
	private ClassLoader prevClassLoader = null;

	@Before
	public void setUp() throws Exception {
		File driverJar = getResourceItem(DRIVER_GET_PATH);
		File driverJarFolder = getResourceItem(PUT_PATH);
		copyFile(driverJar, driverJarFolder);
	}

	@After
	public void tearDown() throws Exception {
		cleanupExecutionContext();
	}

	@Test
	public void testDelete() {
		initExecutionContext();
		cleanupExecutionContext();
		boolean res = false;
		File file = null;
		try {
			file = getResourceItem(DRIVER_PUT_PATH);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (file != null && file.exists()) {
			if (file.delete()) {
				res = true;
			}
		}
		Assert.assertEquals(true, res);
	}

	private void copyFile(File src, File dst) {
		FileOutputStream fos = null;
		FileInputStream fis = null;
		try {
			File ff = new File(dst.getPath(), src.getName());
			if (!ff.exists()) {
				ff.createNewFile();
			}
			fos = new FileOutputStream(ff);
			fis = new FileInputStream(src);
			byte b[] = new byte[ 1024 * 1024 ];
			int nOut = 0;
			do {
				nOut = fis.read(b);
				fos.write(b, 0, nOut);
			} while (nOut == b.length);
			fos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {}
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {}
			}
		}
	}
	
	private ConsoleConfigClassLoader createJarClassLoader() {
		final URL[] customClassPathURLs = getCustomClassPathURLs();
		ConsoleConfigClassLoader urlClassLoader = AccessController.doPrivileged(new PrivilegedAction<ConsoleConfigClassLoader>() {
			public ConsoleConfigClassLoader run() {
				return new ConsoleConfigClassLoader(customClassPathURLs, getParentClassLoader()) {
				    public InputStream getResourceAsStream(String name) {
				    	InputStream is = super.getResourceAsStream(name);
				    	return is;
				    }
				    
				    public URL findResource(final String name) {
				    	URL res = super.findResource(name);
				    	return res;
				    }

				    public Enumeration<URL> findResources(final String name) throws IOException {
				    	Enumeration<URL> res = super.findResources(name);
				    	return res;
				    }

				    protected Class<?> findClass(String name) throws ClassNotFoundException {
						Class<?> res = null;
						try {
							res = super.findClass(name);
						} catch (ClassNotFoundException cnfe) {
							throw cnfe;
						}
						return res;
					}

					protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
						Class<?> res = null;
						try {
							res = super.loadClass(name, resolve);
						} catch (ClassNotFoundException cnfe) {
							throw cnfe;
						}
						return res;
					}

					public Class<?> loadClass(String name) throws ClassNotFoundException {
						Class<?> res = null;
						try {
							res = super.loadClass(name);
						} catch (ClassNotFoundException cnfe) {
							throw cnfe;
						}
						return res;
					}
				};
			}
		});
		return urlClassLoader;
	}
	
	private void initExecutionContext() {
		final ConsoleConfigClassLoader urlClassLoader = createJarClassLoader();
		DefaultExecutionContext dec = new DefaultExecutionContext(testName.getMethodName(), urlClassLoader);
		ExecutionContext.Command command = new ExecutionContext.Command() {
			public Object execute() {
				try {
					Class<?> driverClass = null;
					driverClass = (Class<?>)ReflectHelper.classForName(DRIVER_TEST_CLASS);
					Driver driver2 = (Driver)driverClass.newInstance();
					ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
					if (contextClassLoader != null) {
						driverClass = (Class<?>)contextClassLoader.loadClass(DRIVER_TEST_CLASS);
					}
					java.util.Properties info = new java.util.Properties();
				    info.put("user", CONNECTION_USERNAME);
				    info.put("password", CONNECTION_PASSWORD);

				    try {
				    		Connection connection = driver2.connect(CONNECTION_URL, info);
				    		connection.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}						
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				return null;
			}
		};
		dec.execute(command);
		command = null;
		dec = null;
		urlClassLoader.close();
	}
	
	private void cleanupExecutionContext() {
		if (prevClassLoader != null) {
			Thread.currentThread().setContextClassLoader(prevClassLoader);
		}
		GarbageCollectionUtil.forceCollectGarbage();
	}

	private ClassLoader getParentClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	private URL[] getCustomClassPathURLs() {
		URL[] customClassPathURLs = new URL[1];
		File driverJar2;
		try {
			driverJar2 = getResourceItem(DRIVER_PUT_PATH);
			customClassPathURLs[0] = driverJar2.toURI().toURL();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return customClassPathURLs;
	}

	private File getResourceItem(String strResPath) throws IOException {
		IPath resourcePath = new Path(strResPath);
		File resourceFolder = resourcePath.toFile();
		Bundle bundle = Platform.getBundle("org.jboss.tools.hibernate.orm.test");
		URL entry = bundle.getEntry(strResPath);
		URL resProject = FileLocator.resolve(entry);
		String tplPrjLcStr = FileLocator.resolve(resProject).getFile();
		resourceFolder = new File(tplPrjLcStr);
		return resourceFolder;
	}

}
