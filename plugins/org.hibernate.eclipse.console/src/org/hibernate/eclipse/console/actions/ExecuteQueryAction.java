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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Event;
import org.hibernate.HibernateException;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.QueryEditor;
import org.hibernate.eclipse.console.utils.EclipseImages;

/**
 * @author max
 *
 */
public class ExecuteQueryAction extends Action {

	private QueryEditor editor;

	public ExecuteQueryAction() {
		setImageDescriptor(EclipseImages.getImageDescriptor(ImageConstants.EXECUTE) );
		initTextAndToolTip(HibernateConsoleMessages.ExecuteQueryAction_run_default);
	}


	public ExecuteQueryAction(QueryEditor editor) {
		this();
		setHibernateQueryEditor( editor );
	}

	public void run() {
		execute(editor );
	}

	public void runWithEvent(Event event) {
		super.runWithEvent( event );
	}

	protected void execute(QueryEditor queryEditor) {

		ConsoleConfiguration cfg = queryEditor.getConsoleConfiguration();
		if (cfg != null) {
			if (!cfg.isSessionFactoryCreated()) {
				if (queryEditor.askUserForConfiguration(cfg.getName())) {
					if (cfg.getConfiguration() == null) {
	    				try {
	    					cfg.build();
	    				} catch (HibernateException he) {
	    					HibernateConsolePlugin.getDefault().showError(
	    						HibernateConsolePlugin.getShell(), 
	    						HibernateConsoleMessages.LoadConsoleCFGCompletionProposal_could_not_load_configuration +
	    						' ' + cfg.getName(), he);
	    				}
					}
					if (cfg.getConfiguration() != null) {
						cfg.buildSessionFactory();
						queryEditor.executeQuery(cfg);
					}
				}
			} else {
				queryEditor.executeQuery(cfg);
			}
		}
	}

	public void initTextAndToolTip(String text) {
		setText(text);
		setToolTipText(text);
	}

	public void run(IAction action) {
		run();
	}

	public void setHibernateQueryEditor(QueryEditor editor) {
		this.editor = editor;
	}

}
