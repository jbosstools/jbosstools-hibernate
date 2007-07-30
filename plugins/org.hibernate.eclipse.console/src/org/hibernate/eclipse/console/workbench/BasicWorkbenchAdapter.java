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
package org.hibernate.eclipse.console.workbench;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.ui.progress.IDeferredWorkbenchAdapter;
import org.eclipse.ui.progress.IElementCollector;
import org.hibernate.eclipse.console.HibernateConsolePlugin;

public abstract class BasicWorkbenchAdapter implements IDeferredWorkbenchAdapter {

	static class MutexRule implements ISchedulingRule {
		
		private final Object mutex;

		public MutexRule(Object mutex) {
			this.mutex = mutex;
		}

		public boolean contains(ISchedulingRule rule) {
			if(rule instanceof MutexRule) {
				return mutex == ((MutexRule)rule).mutex;
			} else {
				return false;
			}
		}

		public boolean isConflicting(ISchedulingRule rule) {
			if(rule instanceof MutexRule) {
				return mutex == ((MutexRule)rule).mutex;
			} else {
				return false;
			}
		}	
	}

	final static Object[] NO_CHILDREN = new Object[0];
	
	protected Object[] toArray(Iterator iterator, Class clazz) {
		List obj = toList( iterator );
		return obj.toArray((Object[]) Array.newInstance(clazz, obj.size()));		
	}

	private List toList(Iterator iterator) {
		List obj = new ArrayList();
		while ( iterator.hasNext() ) {
			obj.add(iterator.next());
		}
		return obj;
	}
	
	protected Object[] toArray(Enumeration enumeration, Class clazz) {
		List obj = new ArrayList();
		while ( enumeration.hasMoreElements() ) {
			obj.add(enumeration.nextElement());
		}
		return obj.toArray((Object[]) Array.newInstance(clazz, obj.size()));
	}


	public Object[] getChildren(Object o, final IProgressMonitor monitor) {
		return getChildren(o);
	}
	
	public void fetchDeferredChildren(Object object,
			IElementCollector collector, IProgressMonitor monitor) {
		try {
			collector.add(getChildren(object, monitor), monitor);
			collector.done();
		} catch(Exception e) {
			handleError(collector,e);			
		} finally {
			collector.done();
			monitor.done();
		}
	}

	protected void handleError(IElementCollector collector, Exception e) {
		HibernateConsolePlugin.getDefault().logMessage(IStatus.WARNING, e.toString(), e);
		HibernateConsolePlugin.openError(null, "Lazy tree error", "Error while fetching children", e, HibernateConsolePlugin.PERFORM_SYNC_EXEC);		
	}

	public boolean isContainer() {
		return true;
	}
	
	final public ISchedulingRule getRule(Object object) {
		//return new MutexRule(object);
		return null;
	}

	

}
