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
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.sql.Driver;
import java.security.PrivilegedAction;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.jar.JarFile;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.apt.core.internal.JarClassLoader;
import org.hibernate.console.ConsoleConfigClassLoader;
import org.hibernate.console.execution.DefaultExecutionContext;
import org.hibernate.console.execution.ExecutionContext;
import org.hibernate.eclipse.console.test.HibernateConsoleTestPlugin;
import org.hibernate.eclipse.console.test.utils.GarbageCollectionUtil;
import org.hibernate.util.ReflectHelper;

import com.mysql.jdbc.Messages;

//import sun.reflect.FieldAccessor;

import junit.framework.TestCase;

/**
 * The class intended to reproduce the reason of 
 * http://opensource.atlassian.com/projects/hibernate/browse/HBX-936
 * and road map how to fix it.
 *  
 * @author Vitali Yemialyanchyk
 */
@SuppressWarnings("restriction")
public class DriverDeleteTest extends TestCase {

	public static final String DRIVER_TEST_NAME = "mysql-connector-java-5.0.7-bin.jar"; //$NON-NLS-1$
	public static final String DRIVER_GET_PATH = "testresources/".replaceAll("//", File.separator) + DRIVER_TEST_NAME; //$NON-NLS-1$ //$NON-NLS-2$
	public static final String DRIVER_PUT_PATH = "res/".replaceAll("//", File.separator) + DRIVER_TEST_NAME; //$NON-NLS-1$ //$NON-NLS-2$
	public static final String PUT_PATH = "res"; //$NON-NLS-1$

	private WeakReference<ExecutionContext> executionContext = null;
	private ClassLoader prevClassLoader = null;

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

	protected List<File> getCustomClassPathFiles() {
		final List<File> files = new ArrayList<File>();
		File driverJar2;
		try {
			driverJar2 = getResourceItem(DRIVER_PUT_PATH);
			files.add(driverJar2);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return files;
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
	
	public JarClassLoader createJarClassLoader() {
		final List<File> files = getCustomClassPathFiles();
		JarClassLoader urlClassLoader = AccessController.doPrivileged(new PrivilegedAction<JarClassLoader>() {
			public JarClassLoader run() {
				return new JarClassLoader(files, getParentClassLoader()) {
				};
			}
		});
		return urlClassLoader;
	}
	
	public ConsoleConfigClassLoader createJarClassLoader2() {
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
	public void forceDeleteDrivers() {
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
		clazz = null;
		drivers = null;
	}
	
	static Object testStaticObj;
	
	@SuppressWarnings({"unchecked", "unused", "nls"})
	public void initExecutionContext() {
		/**/
		//URLClassLoader urlClassLoader = createClassLoader();
		//JarClassLoader urlClassLoader = createJarClassLoader();
		final ConsoleConfigClassLoader urlClassLoader = createJarClassLoader2();
		/** /
		prevClassLoader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(urlClassLoader);
		ClassLoader prevClassLoader22 = Thread.currentThread().getContextClassLoader();
		//
		Class<Driver> driverClass22 = null;
		Class<Driver> driverClass = null;
		try {
			driverClass = (Class<Driver>)urlClassLoader.loadClass("com.mysql.jdbc.Driver"); //$NON-NLS-1$
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		assertNotNull(driverClass);
		/**/
		int numRedDrivers = 0;
		//StringWriter wr = new StringWriter();
		//PrintWriter pw = new PrintWriter(wr);
		//DriverManager.setLogWriter(pw);
		Driver driver = null;
		/** /
		Connection connection = null;
		String test = ""; //$NON-NLS-1$
		try {
			driver = driverClass.newInstance();
			//DriverManager.registerDriver(driver);
			//driverClass = ReflectHelper.classForName("com.mysql.jdbc.Driver"); //$NON-NLS-1$
			//driver = driverClass.newInstance();
			//DriverManager.registerDriver(driver);
			//DriverManager.deregisterDriver(driver);
			java.util.Enumeration<Driver> drEnum = DriverManager.getDrivers();
			while (drEnum.hasMoreElements()) {
				driver = drEnum.nextElement();
//				DriverManager.deregisterDriver(driver);
				numRedDrivers++;
			}
			java.util.Properties info = new java.util.Properties();
		    info.put("user", "root");
		    info.put("password", "p@ssw0rd2");
			connection = driver.connect("jdbc:mysql://localhost:3306/jpa", info);
			//connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/jpa", "root", "p@ssw0rd");
			test = connection.getCatalog();
			connection.close();
			DriverManager.deregisterDriver(driver);
		//} catch (ClassNotFoundException e) {
		//	e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		System.out.print(test);
		System.out.print(numRedDrivers);
		/** /
		System.out.print(wr.res);
		/**/
		DefaultExecutionContext dec = new DefaultExecutionContext(getName(), urlClassLoader);
		executionContext = new WeakReference<ExecutionContext>(dec);
		/**/
		ExecutionContext.Command command = new ExecutionContext.Command() {
			public Object execute() {
				try {
					Class<Driver> driverClass = null;
					//Class.forName("com.mysql.jdbc.Driver"); //$NON-NLS-1$
					//if (driverClass != null) {
						driverClass = ReflectHelper.classForName("com.mysql.jdbc.Driver"); //$NON-NLS-1$
						Driver driver2 = driverClass.newInstance();
						//DriverManager.registerDriver(driver2);
						ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
						if (contextClassLoader != null) {
							driverClass = (Class<Driver>)contextClassLoader.loadClass("com.mysql.jdbc.Driver"); //$NON-NLS-1$
						}
						//if (driverClass == null) {
						//driverClass.newInstance();
							//DriverManager.registerDriver(driverClass.newInstance());
						//}
						java.util.Properties info = new java.util.Properties();
					    info.put("user", "root");
					    info.put("password", "p@ssw0rd2");
					    
						/** /
					    try {
					    	Connection connection = driver2.connect("jdbc:mysql://localhost:3306/jpa", info);
					    	//Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/jpa", "root", "p@ssw0rd");  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
					    	//String test = connection.getCatalog();

					    	//System.out.println(test);
					    	//System.out.println(test);
					    	connection.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
						/**/
						
						Object obj = null;
						Field f = null;
						
						/**/
						Class<com.mysql.jdbc.Connection> connClass = ReflectHelper.classForName("com.mysql.jdbc.Connection"); //$NON-NLS-1$
						f = connClass.getDeclaredField("cancelTimer");
						f.setAccessible(true);
						java.util.Timer timer = (java.util.Timer)f.get(null);
						if (timer != null) {
							timer.cancel();
							timer.purge();
						}
						f.set(null, null);
						
						Class<com.mysql.jdbc.LoadBalancingConnectionProxy> classLoadBalancingConnectionProxyClass = ReflectHelper.classForName("com.mysql.jdbc.LoadBalancingConnectionProxy"); //$NON-NLS-1$
						f = classLoadBalancingConnectionProxyClass.getDeclaredField("getLocalTimeMethod");
						f.setAccessible(true);
						obj = f.get(null);
						f.set(null, null);
						
						Class<com.mysql.jdbc.StandardSocketFactory> classStandardSocketFactory = ReflectHelper.classForName("com.mysql.jdbc.StandardSocketFactory"); //$NON-NLS-1$
						f = classStandardSocketFactory.getDeclaredField("setTraficClassMethod");
						f.setAccessible(true);
						obj = f.get(null);
						f.set(null, null);
						
						Class<com.mysql.jdbc.StringUtils> classStringUtils = ReflectHelper.classForName("com.mysql.jdbc.StringUtils"); //$NON-NLS-1$
						f = classStringUtils.getDeclaredField("toPlainStringMethod");
						f.setAccessible(true);
						obj = f.get(null);
						f.set(null, null);
						/**/
						
						Class<com.mysql.jdbc.Util> classUtil = ReflectHelper.classForName("com.mysql.jdbc.Util"); //$NON-NLS-1$
						f = classUtil.getDeclaredField("systemNanoTimeMethod");
						f.setAccessible(true);
						obj = f.get(null);
						f.set(null, null);
						/** /
						//
						f = classUtil.getDeclaredField("DEFAULT_TIMEZONE");
						f.setAccessible(true);
						obj = f.get(null);
						setStaticFinalField(f, null);
						//
						f = classUtil.getDeclaredField("enclosingInstance");
						f.setAccessible(true);
						obj = f.get(null);
						f.set(null, null);
						/** /
						testStaticObj = Locale.getDefault();
						/**/
						ResourceBundle temp = ResourceBundle.getBundle("com.mysql.jdbc.LocalizedErrorMessages", Locale.getDefault(),
								urlClassLoader);
						/**/
				        final String resName = "com.mysql.jdbc.LocalizedErrorMessages".replace('.', '/') + ".properties";
				        InputStream stream = (InputStream)java.security.AccessController.doPrivileged(
				            new java.security.PrivilegedAction() {
				                public Object run() {
				                    if (urlClassLoader != null) {
				                        return urlClassLoader.getResourceAsStream(resName);
				                    } else {
				                        return ClassLoader.getSystemResourceAsStream(resName);
				                    }
				                }
				            }
				        );

				        if (stream != null) {
				            // make sure it is buffered
				            stream = new java.io.BufferedInputStream(stream);
							java.util.PropertyResourceBundle prb = new PropertyResourceBundle(stream);
				            stream.close();
				        } 
						//
						/**/
						//
						Class<com.mysql.jdbc.Messages> classMessages = ReflectHelper.classForName("com.mysql.jdbc.Messages"); //$NON-NLS-1$
						f = classMessages.getDeclaredField("BUNDLE_NAME");
						f.setAccessible(true);
						obj = f.get(null);
						setStaticFinalField(f, null);
						//
						f = classMessages.getDeclaredField("RESOURCE_BUNDLE");
						f.setAccessible(true);
						obj = f.get(null);
						setStaticFinalField(f, null);
						/**/
						//Class<java.util.ResourceBundle> classResourceBundle = ReflectHelper.classForName("java.util.ResourceBundle"); //$NON-NLS-1$
						//Class<java.util.PropertyResourceBundle> classPropertyResourceBundle = (Class<java.util.PropertyResourceBundle>)obj.getClass();
						Class<java.util.PropertyResourceBundle> classPropertyResourceBundle = (Class<java.util.PropertyResourceBundle>)temp.getClass();
						Class<java.util.ResourceBundle> classResourceBundle = (Class<ResourceBundle>)classPropertyResourceBundle.getSuperclass(); //$NON-NLS-1$
						f = classResourceBundle.getDeclaredField("cacheKey");
						f.setAccessible(true);
						obj = f.get(null);
						setStaticFinalField(f, null);
						//
						f = classResourceBundle.getDeclaredField("underConstruction");
						f.setAccessible(true);
						obj = f.get(null);
						setStaticFinalField(f, null);
						//
						f = classResourceBundle.getDeclaredField("NOT_FOUND");
						f.setAccessible(true);
						obj = f.get(null);
						setStaticFinalField(f, null);
						//
						f = classResourceBundle.getDeclaredField("cacheList");
						f.setAccessible(true);
						obj = f.get(null);
						setStaticFinalField(f, null);
						//
						f = classResourceBundle.getDeclaredField("referenceQueue");
						f.setAccessible(true);
						obj = f.get(null);
						setStaticFinalField(f, null);
						/**/
						
				        /**/
						//Class<sun.net.www.protocol.jar.JarURLConnection> classJarURLConnection = ReflectHelper.classForName("sun.net.www.protocol.jar.JarURLConnection"); //$NON-NLS-1$
						//f = classJarURLConnection.getDeclaredField("factory");
						//f.setAccessible(true);
						//obj = f.get(null);
						//f.set(null, null);
						//
						Class classJarFileFactory = obj.getClass();
						f = classJarFileFactory.getDeclaredField("fileCache");
						f.setAccessible(true);
						obj = f.get(null);
						HashMap fileCache = (HashMap)obj;
						f.set(null, null);
						//
						f = classJarFileFactory.getDeclaredField("urlCache");
						f.setAccessible(true);
						obj = f.get(null);
						HashMap urlCache = (HashMap)obj;
						f.set(null, null);
						//
						Iterator it = urlCache.keySet().iterator();
						while (it.hasNext()) {
							JarFile jarFile = (JarFile)it.next();
							if (jarFile.getName().equals(DRIVER_TEST_NAME)) {
								jarFile.close();
							}
						}
				        /**/
						//DriverManager.deregisterDriver(driver2);
					//}
					//contextClassLoader = null;
					//driverClass = null;
				} catch (IOException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				//} catch (SQLException e) {
				//	e.printStackTrace();
				}
				return null;
			}
		};
		dec.execute(command);
		command = null;
		dec = null;
		/** /
		forceDeleteDrivers();
		//
		// obligatory in other case gc can't collect these variables!!!
		driver = null;
		driverClass = null;
		urlClassLoader = null;
		forceCollectGarbage();
		/**/
		urlClassLoader.close();
	}
	
	private static final String MODIFIERS_FIELD = "modifiers";

	public static void setStaticFinalField(Field field, Object value)
			throws NoSuchFieldException, IllegalAccessException {
		/** /
		field.setAccessible(true);
		Field modifiersField = Field.class.getDeclaredField(MODIFIERS_FIELD);
		modifiersField.setAccessible(true);
		int modifiers = modifiersField.getInt(field);
		modifiers &= ~Modifier.FINAL;
		modifiersField.setInt(field, modifiers);
		sun.reflect.ReflectionFactory reflection =
			sun.reflect.ReflectionFactory.getReflectionFactory();
		FieldAccessor fa = reflection.newFieldAccessor(field, false);
		fa.set(null, value);
		/**/
	}
	
	public void cleanupExecutionContext() {
		/** /
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
		/**/
		//
		if (prevClassLoader != null) {
			Thread.currentThread().setContextClassLoader(prevClassLoader);
		}
		//
		GarbageCollectionUtil.forceCollectGarbage();
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
