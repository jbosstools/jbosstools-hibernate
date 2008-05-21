/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.hibernate.wizard.generateDDL;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.jar.*;

import org.osgi.framework.Bundle;
import org.eclipse.core.runtime.*;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.jboss.tools.hibernate.view.ViewPlugin;




public class HibernateDDLGenerator implements IHibernateDDLGenerator {

	public String[] getDialects() {
		List<String> l = new ArrayList<String>();
		try {
			Bundle bundle = Platform.getBundle("org.jboss.tools.hibernate.hblibs");
			String prefix = "org/hibernate/dialect/";
			String suffix = "Dialect.class";
			URL url = FileLocator.resolve(bundle.getResource(prefix));
			String n = url.toString();
			if(n.startsWith("jar:file:/")) {
				n = (n.charAt(11) == ':') ? n.substring(10) : n.substring(9);
			} else if(n.startsWith("jar:file:")) {
				n = (n.charAt(10) == ':') ? n.substring(9) : n.substring(8);
			}
			int i = n.indexOf("!");
			n = n.substring(0, i);
			JarFile jf = new JarFile(new File(n));
			Enumeration es = jf.entries();
			while(es.hasMoreElements()) {
				JarEntry je = (JarEntry)es.nextElement();
				n = je.getName();
				if(!n.startsWith(prefix)) continue;
				n = n.substring(prefix.length());
				if(!n.endsWith(suffix)) continue;
				n = n.substring(0, n.length() - suffix.length());
				if(n.length() > 0) l.add(n);
			}
		} catch (Throwable e) {
			ViewPlugin.getPluginLog().logError(e);
		}
		return l.toArray(new String[0]);
	}

	public ClassLoader getClassLoader() {


		return Configuration.class.getClassLoader();//Url
	}
	


	public void generate(Properties p) throws Exception {
		
		String filename = p.getProperty("filename");
		String dialect = p.getProperty("dialect");
		String classname = "org.hibernate.dialect." + dialect + "Dialect";
		ClassLoader cl = (ClassLoader)p.get("classloader");
		Configuration cfg = new Configuration();
	
		ClassLoader current = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(cl);
		try {
			//cfg.setProperty(Environment.DIALECT, classname);
			cfg.setNamingStrategy(ImprovedNamingStrategy.INSTANCE);
			cfg.configure(new java.io.File(p.getProperty("url")));
			cfg.setProperty(Environment.DIALECT, classname);
			SchemaExport tool = new SchemaExport(cfg);
			String delimiter = decodeDelimiter(p.getProperty("delimiter"));
			if(delimiter != null && delimiter.length() > 0) {
				tool.setDelimiter(delimiter);
			}
			tool.setOutputFile(filename);
			boolean drop = "true".equals(p.getProperty("drop"));
			if(drop) { 
				tool.drop(false, false);
			} else {
				tool.create(false, false);
			}
		} finally {
			//Thread.currentThread().getThreadGroup().getParent().
			Thread.currentThread().setContextClassLoader(current);
			
		}
		
	}
	
	private String decodeDelimiter(String delimiter) {
		if(delimiter == null || delimiter.length() == 0) return null;
		StringBuffer sb = new StringBuffer();
		boolean slash = false;
		for (int i = 0; i < delimiter.length(); i++) {
			char ch = delimiter.charAt(i);
			if(slash) {
				if(ch == '\\') {
					sb.append('\\');
				} else if(ch == 'r') {
					sb.append('\r');
				} else if(ch == 'n') {
					sb.append('\n');
				} else {
					sb.append('\\').append(ch);
				}
				slash = false;
			} else {
				if(ch == '\\') {
					slash = true;
				} else {
					sb.append(ch);
				}
			}
		}
		return sb.toString();
	}
	

	
}
 