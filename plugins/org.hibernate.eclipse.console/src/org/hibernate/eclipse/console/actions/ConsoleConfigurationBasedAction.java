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
package org.hibernate.eclipse.console.actions;

import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.actions.SelectionListenerAction;
import org.hibernate.HibernateException;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.HibernateConsolePlugin;

/**
 * @author max
 *
 */
public abstract class ConsoleConfigurationBasedAction extends SelectionListenerAction {

	boolean enabledWhenNoSessionFactory = false;
	boolean supportMultiple = true;

	protected void setEnabledWhenNoSessionFactory(
			boolean enabledWhenNoSessionFactory) {
		this.enabledWhenNoSessionFactory = enabledWhenNoSessionFactory;
	}

	/**
	 * @param supportMultiple The supportMultiple to set.
	 */
	public void setSupportMultiple(boolean supportMultiple) {
		this.supportMultiple = supportMultiple;
	}

	/**
	 * @param text
	 */
	protected ConsoleConfigurationBasedAction(String text) {
		super(text);
	}

	final public void run() {

		try {
			doRun();
		} catch(HibernateException he) {
			String out = NLS.bind(HibernateConsoleMessages.ConsoleConfigurationBasedAction_problem_while_executing, getText(), he);
			HibernateConsolePlugin.getDefault().showError(null, out, he);
		}
	}

	abstract protected void doRun();

	final protected boolean updateSelection(IStructuredSelection selection) {
		   boolean enabled = false;
		   if(!supportMultiple && selection.size()>1) return false;
	        for (Iterator i = selection.iterator();
	            i.hasNext();
	            ) {
	            Object object = i.next();
	            if (object instanceof ConsoleConfiguration) {
	                ConsoleConfiguration consoleConfiguration = (ConsoleConfiguration) object;
	                enabled |= updateState(consoleConfiguration);

	            } else {
	                enabled = false;
	            }
	        }
	        return enabled;
	}

	/**
	 * @param consoleConfiguration
	 */
	protected boolean updateState(ConsoleConfiguration consoleConfiguration) {
		if(enabledWhenNoSessionFactory) {
        	return true;
        } else {
        	return consoleConfiguration.isSessionFactoryCreated();
        }
	}

}
