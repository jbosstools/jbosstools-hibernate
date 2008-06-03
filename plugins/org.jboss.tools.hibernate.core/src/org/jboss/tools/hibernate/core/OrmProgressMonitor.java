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
package org.jboss.tools.hibernate.core;



import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;


/**
 * Progress monitor class stores an IProgressMonitor for the current thread. 
 * Every method can use it without any additional parameters.
 * <br>Example: <pre>
 
	IRunnableWithProgress operation = new IRunnableWithProgress(){
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			OrmProgressMonitor.setMonitor(monitor);
			try{
				call long-running methods here
			} finally{
				OrmProgressMonitor.setMonitor(null);
			}
		}
	};
  
 * </pre>
 * */
public class OrmProgressMonitor {

    // added by Nick 01.07.2005
    private float oneStepWeight = 1;
    private int percents = 100;
    private int items = 1;
    private float current = 0;
    private IProgressMonitor int_monitor = null;
    // by Nick
    
	private static ThreadLocal<IProgressMonitor> monitor = new ThreadLocal<IProgressMonitor>();
	private static IProgressMonitor nullMonitor = new NullProgressMonitor();
    
    // added by Nick 05.07.2005
    public final static OrmProgressMonitor NULL_MONITOR = new OrmProgressMonitor(nullMonitor);
    // by Nick

    public static void setMonitor(IProgressMonitor m) {
		monitor.set(m);
	}
    
    public OrmProgressMonitor(IProgressMonitor monitor) {
        int_monitor = monitor;
    }
    
	public static IProgressMonitor getMonitor() {
		IProgressMonitor pm = (IProgressMonitor)monitor.get();
		return pm == null ? nullMonitor : pm;
	}
    
    // added by Nick 01.07.2005
    private void calculate() {
        if (this.items != 0) {
            oneStepWeight = ((float)percents) / items;
        }
    }
    
    public void setTaskParameters(int percents, int items) {
        this.percents = percents;
        this.items = items;
        calculate();
        if (this.items == 0)
            int_monitor.worked(this.percents);
    }
    
    public void worked() {
        current += oneStepWeight;
        int int_progress = (int)Math.floor(current);
        if (int_progress != 0) {
            int_monitor.worked(int_progress);
            current -= int_progress;
        }
    }
    
    public void worked(int work) {
        int_monitor.worked(work);
    }
    // by Nick
}
