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
package org.jboss.tools.hibernate.wizard.hibernateconnection;

import java.net.URLClassLoader;
import java.net.URL;
import java.net.URLStreamHandlerFactory;
import java.io.*;
import java.security.CodeSource;


/**
 * @author sushko
 * Url loader for loading any binary files

/**
 * Created by IntelliJ IDEA.
 * User: Mikalaj
 * Date: 15.03.2005
 * Time: 0:42:51
 * To change this template use File | Settings | File Templates.
 */


/**
 * Url loader for loading any binary files
 */
public class TestUrlClassLoader extends URLClassLoader{
	
    /**
	 * Constructor of the  TestUrlClassLoader
	 * @param urls
	 */
    public TestUrlClassLoader(URL[] urls) {
        super(urls);
    }

	/**
	 * Constructor of the  TestUrlClassLoader
	 * @param urls
	 * @param classLoader
	 * 
	 */
    public TestUrlClassLoader(URL[] urls, ClassLoader classLoader) {
        super(urls, classLoader);
        
    }

	/**
	 * Constructo of the  TestUrlClassLoader
	 * @param urls
	 * @param classLoader
	 * @param urlStreamHandlerFactory
	 */
    public TestUrlClassLoader(URL[] urls, ClassLoader classLoader, URLStreamHandlerFactory urlStreamHandlerFactory) {
        super(urls, classLoader, urlStreamHandlerFactory);
    }

	/**
	 * findClass the  TestUrlClassLoader
	 * @param name
	 * 
	 */
	public Class findClass(String name) throws ClassNotFoundException {
		return super.findClass(name);
	}
	
	/**
	 * loadClassFromBinaryFile of the  TestUrlClassLoader
	 * @param fileName
	 * 
	 */
    public Class loadClassFromBinaryFile(String fileName) throws IOException {
        FileInputStream in = null;
        File f = new File(fileName);
        byte[] bytes;
        int fSize = 0;
        in = new FileInputStream(f);
        fSize = (int)f.length();
        bytes = new byte[fSize];
        int length = 0;
        while (length < fSize)
        {
            length += in.read(bytes,length,in.available());
        }
        Class clazz = defineClass(null,bytes,0,length,(CodeSource)null);
        in.close();
        return clazz;
    }
}

