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
package org.hibernate.eclipse.console.test.utils.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.sql.Driver;
import java.security.PrivilegedAction;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Vector;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.hibernate.console.execution.DefaultExecutionContext;
import org.hibernate.console.execution.ExecutionContext;
import org.hibernate.eclipse.console.test.HibernateConsoleTestPlugin;
import org.hibernate.util.ReflectHelper;

import junit.framework.TestCase;

/**
 * The class intended to reproduce the reason of 
 * http://opensource.atlassian.com/projects/hibernate/browse/HBX-936
 * and road map how to fix it.
 *  
 * @author Vitali Yemialyanchyk
 */
public class DriverDeleteTest extends TestCase {

	public static final String DRIVER_TEST_NAME = "mysql-connector-java-5.0.7-bin.jar"; //$NON-NLS-1$
	public static final String DRIVER_GET_PATH = "testresources/".replaceAll("//", File.separator) + DRIVER_TEST_NAME; //$NON-NLS-1$ //$NON-NLS-2$
	public static final String DRIVER_PUT_PATH = "res/".replaceAll("//", File.separator) + DRIVER_TEST_NAME; //$NON-NLS-1$ //$NON-NLS-2$
	public static final String PUT_PATH = "res"; //$NON-NLS-1$

	private WeakReference<ExecutionContext> executionContext = null;

	protected ClassLoader getParentClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	protected URL[] getCustomClassPathURLs() {
		URL[] customClassPathURLs = new URL[1];
		File driverJar2;
		try {
			driverJar2 = getResourceItem(DRIVER_PUT_PATH);
			customClassPathURLs[0] = driverJar2.toURL();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return customClassPathURLs;
	}

	protected File getResourceItem(String strResPath) throws IOException {
		IPath resourcePath = new Path(strResPath);
		File resourceFolder = resourcePath.toFile();
		URL entry = HibernateConsoleTestPlugin.getDefault().getBundle().getEntry(
				strResPath);
		URL resProject = FileLocator.resolve(entry);
		String tplPrjLcStr = FileLocator.resolve(resProject).getFile();
		resourceFolder = new File(tplPrjLcStr);
		return resourceFolder;
	}

	public static void copyFile(File src, File dst) {
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
	
	protected void setUp() throws Exception {
		File driverJar = getResourceItem(DRIVER_GET_PATH);
		File driverJarFolder = getResourceItem(PUT_PATH);
		copyFile(driverJar, driverJarFolder);
	}

	public URLClassLoader createClassLoader() {
		final URL[] customClassPathURLs = getCustomClassPathURLs();
		URLClassLoader urlClassLoader = AccessController.doPrivileged(new PrivilegedAction<URLClassLoader>() {
			public URLClassLoader run() {
				return new URLClassLoader( customClassPathURLs, getParentClassLoader() ) {
					protected Class<?> findClass(String name) throws ClassNotFoundException {
						try {
							return super.findClass(name);
						} catch (ClassNotFoundException cnfe) {
							throw cnfe;
						}
					}

					protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
						try {
							return super.loadClass(name, resolve);
						} catch (ClassNotFoundException cnfe) {
							throw cnfe;
						}
					}

					public Class<?> loadClass(String name) throws ClassNotFoundException {
						try {
							return super.loadClass(name);
						} catch (ClassNotFoundException cnfe) {
							throw cnfe;
						}
					}
				};
			}
		});
		return urlClassLoader;
	}
	
	public class StringWriter extends Writer {
		
		public String res = new String();

		@Override
		public void close() throws IOException {
		}

		@Override
		public void flush() throws IOException {
		}

		@Override
		public void write(char[] cbuf, int off, int len) throws IOException {
			res += cbuf.toString() + "\r\n"; //$NON-NLS-1$
		}
	}
	
	@SuppressWarnings("unchecked")
	public void initExecutionContext() {
		/**/
		URLClassLoader urlClassLoader = createClassLoader();
		/**/
		Class<Driver> driverClass = null;
		try {
			driverClass = (Class<Driver>)urlClassLoader.loadClass("com.mysql.jdbc.Driver"); //$NON-NLS-1$
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		assertNotNull(driverClass);
		/**/
		int numRedDrivers = 0;
		StringWriter wr = new StringWriter();
		PrintWriter pw = new PrintWriter(wr);
		DriverManager.setLogWriter(pw);
		Driver driver = null;
		/**/
		try {
			driver = driverClass.newInstance();
			/**/
			DriverManager.registerDriver(driver);
			//DriverManager.deregisterDriver(driver);
			java.util.Enumeration<Driver> drEnum = DriverManager.getDrivers();
			while (drEnum.hasMoreElements()) {
				driver = drEnum.nextElement();
				DriverManager.deregisterDriver(driver);
				numRedDrivers++;
			}
			/**/
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		/**/
		Class<DriverManager> clazz = DriverManager.class;
		Vector drivers = null;
		try {
			Field field = clazz.getDeclaredField("drivers"); //$NON-NLS-1$
			field.setAccessible(true);
			drivers = (Vector)field.get(null);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		if (drivers != null && drivers.size() > 0) {
			drivers.clear();
		}
		System.out.print(wr.res);
		/** /
		DefaultExecutionContext dec = new DefaultExecutionContext(getName(), urlClassLoader);
		executionContext = new WeakReference<ExecutionContext>(dec);
		dec = null;
		/** /
		dec.execute(new ExecutionContext.Command() {

			@SuppressWarnings("unchecked")
			public Object execute() {
				try {
					Class<Driver> driverClass = null;
					//if (driverClass != null) {
						driverClass = ReflectHelper.classForName("com.mysql.jdbc.Driver"); //$NON-NLS-1$
						//if (driverClass == null) {
							DriverManager.registerDriver(driverClass.newInstance());
						//}
					//}
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return null;
			}
		});
		/**/
		//
		clazz = null;
		drivers = null;
		// obligatory in other case gc can't collect these variables!!!
		driver = null;
		driverClass = null;
		urlClassLoader = null;
		forceCollectGarbage();
		/**/
	}
	
	public int forceCollectGarbage() {
		int numGCCall = 0;
		long freeMemory = 0;
		long freeMemoryAfter = freeMemory;
		do {
			freeMemory = Runtime.getRuntime().freeMemory();
			System.gc();
			numGCCall++;
			freeMemoryAfter = Runtime.getRuntime().freeMemory();
		} while (freeMemoryAfter - freeMemory != 0);
		return numGCCall;
	}
	
	public void cleanupExecutionContext() {
		if (executionContext != null && executionContext.get() != null) {
			executionContext.get().execute(new ExecutionContext.Command() {

				@SuppressWarnings("unchecked")
				public Object execute() {
					try {
						Class<Driver> driverClass = ReflectHelper.classForName("com.mysql.jdbc.Driver"); //$NON-NLS-1$
						DriverManager.deregisterDriver(driverClass.newInstance());
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					return null;
				}
			});
			executionContext = null;
		}
		//
		forceCollectGarbage();
	}

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
		assertEquals(true, res);
	}

	protected void tearDown() throws Exception {
		cleanupExecutionContext();
	}

}
