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
package org.jboss.tools.hibernate.internal.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.hibernate.core.IConfigurationResource;
import org.jboss.tools.hibernate.core.OrmCore;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.internal.core.properties.PropertySourceBase;

public abstract class AbstractConfigurationResource extends PropertySourceBase implements
		IConfigurationResource {

	private IResource resource;
    private long timeStamp = 0;

	public IResource getResource() {
		return resource;
	}
	
	public abstract IResource createResource()  throws CoreException;
	public abstract IResource findResource()  throws CoreException;

    //added 19.05.2005 by Nick
    private long refreshTimeStamp() {
		IResource resource = this.getResource();
		long result = 0;

		if (resource != null) {
			try {
				resource.refreshLocal(IResource.DEPTH_ZERO, null);
				result = resource.getLocalTimeStamp();
			} catch (CoreException e) {
				ExceptionHandler.logObjectPlugin(
						"Exception refreshing resource timestamp...", OrmCore
								.getDefault().getBundle().getSymbolicName(), e);
			}
		}
		return result;
	}
    // by Nick

    // added 19.05.2005 by Nick
    public boolean resourceChanged() {
		if(isSaveInProgress())return false;
        return (timeStamp != refreshTimeStamp());
    }
    //by Nick
	
	private boolean saveInProgress;
	
	private synchronized boolean isSaveInProgress(){
		return saveInProgress;
	}
	private synchronized void setSaveInProgress(boolean isInProgress){
		saveInProgress=isInProgress;
	}

    public void save() throws IOException, CoreException {
		setSaveInProgress(true);
		try {
			if (resource == null) {
				resource = findResource();
				if (resource == null)
					resource = createResource();
			}

			// added by Nick 27.09.2005
			if (resource != null && resource.getType() == IResource.FILE) {
				resource.refreshLocal(IResource.DEPTH_ZERO, null);
			}
			// by Nick

			if (!((IFile) resource).exists()) {
				if (getProperties().size() == 0)
					return;

				// edit tau 18.01.2006
				// edit tau 10.02.2006 for ESORM-513 Overwrites and changes our
				// Hibernate mapping files, even if they are marked read-only?
				((IFile) resource).create(null, IResource.NONE, null);
			}
			if (resource != null && resource.getType() == IResource.FILE) {
				IFile res = (IFile) resource;
				ByteArrayOutputStream output = null;
				// #changed# by Konstantin Mishin on 23.09.2005 fixed for
				// ESORM-37
				output = new ByteArrayOutputStream();
				this.getProperties().store(output, null);
				HashMap<Integer, String> hm = new HashMap<Integer, String>();
				HashMap<Integer, String> hm2 = new HashMap<Integer, String>();
				StringBuffer out = new StringBuffer();
				// #added# by Konstantin Mishin on 27.09.2005 fixed for
				// ESORM-172
				if (res.isLocal(IFile.DEPTH_ZERO)) {
					// #added#
					InputStream oldInput = res.getContents(false);
					byte[] b = new byte[4096];
					for (int n; (n = oldInput.read(b)) != -1;)
						out.append(new String(b, 0, n));
				}
				int i = 0;
				int j = 0;
				StringTokenizer stringTokenizer1 = new StringTokenizer(out
						.toString(), "\r\n");
				while (stringTokenizer1.hasMoreTokens())
					hm.put(new Integer(i++), stringTokenizer1.nextToken());
				boolean[] flag = new boolean[hm.size()];
				StringTokenizer stringTokenizer2 = new StringTokenizer(
						new String(output.toByteArray()), "\r\n");
				if (stringTokenizer2.hasMoreTokens())
					stringTokenizer2.nextToken();
				while (stringTokenizer2.hasMoreTokens()) {
					String str3 = stringTokenizer2.nextToken();
					String str4 = (new StringTokenizer(str3, "=")).nextToken();
					for (i = 0; i < hm.size(); i++) {
						String str = (String) hm.get(new Integer(i));
						String str2 = str4;
						if (str.startsWith(str2) && "\r\n".indexOf(str) == -1) {
							hm.put(new Integer(i), str3);
							flag[i] = true;
							break;
						}
					}
					if (i == hm.size())
						hm2.put(new Integer(j++), str3);
				}
				i = j = 0;
				String newStrOutput = new String();
				while (hm.size() != 0) {
					String str5 = (String) hm.remove(new Integer(i++)) + '\n';
					if (str5.startsWith("#") || str5.indexOf((int) '=') == -1
							|| flag[i - 1])
						newStrOutput += str5;
				}
				while (hm2.size() != 0)
					newStrOutput += (String) hm2.remove(new Integer(j++)) + '\n';
				res.setContents(new ByteArrayInputStream(newStrOutput
						.getBytes()), IResource.NONE, null);
				// #changed#
			}
			timeStamp = refreshTimeStamp();
		} finally {
			setSaveInProgress(false);
		}
	}
    
    public synchronized void reload() throws IOException, CoreException {
        // added by Nick 15.09.2005
        if(resource!=null && resource.getType()==IResource.FILE){
            resource.refreshLocal(IResource.DEPTH_ZERO,null);
        }
        // by Nick
        if(resource==null || !resource.isLocal(IResource.DEPTH_ZERO)){
			resource=findResource();
		}
		if(resource==null || !resource.isLocal(IResource.DEPTH_ZERO)) {
			// #added# by Konstantin Mishin on 28.11.2005 fixed for ESORM-351
			this.replaceProperties(new Properties());
			// #added#
			return;
		}
		if(resource!=null && resource.getType()==IResource.FILE){
			IFile res = (IFile) resource;
			// added by yk 29.10.2005.
			// edit tau 13.01.2006			
			if (!resource.isSynchronized(IResource.DEPTH_ZERO)){
				resource.refreshLocal(IResource.DEPTH_ZERO, null); // ESORM-266				
			}
			
            InputStream input = res.getContents(false);	
			try {
				Properties properties = new Properties();
				properties.load(input);
				this.replaceProperties(properties);
			} finally {
				if(input != null)
					input.close();
                timeStamp = refreshTimeStamp();
			}
		}
	}

}
