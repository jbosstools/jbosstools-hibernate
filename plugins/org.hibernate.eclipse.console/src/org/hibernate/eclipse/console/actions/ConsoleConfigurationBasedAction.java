/*
 * Created on 08-Dec-2004
 *
 */
package org.hibernate.eclipse.console.actions;

import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.actions.SelectionListenerAction;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.node.ConfigurationNode;

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

	final protected boolean updateSelection(IStructuredSelection selection) {
		   boolean enabled = false;
		   if(!supportMultiple && selection.size()>1) return false; 
	        for (Iterator i = selection.iterator();
	            i.hasNext();
	            ) {
	            Object object = i.next();
	            if (object instanceof ConfigurationNode) {
	                ConfigurationNode node = (ConfigurationNode) object;
	                ConsoleConfiguration consoleConfiguration = node.getConsoleConfiguration();
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
        	return !consoleConfiguration.isSessionFactoryCreated();
        } else {
        	return consoleConfiguration.isSessionFactoryCreated();
        }
	}

}
