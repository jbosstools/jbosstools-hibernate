/*******************************************************************************
  * Copyright (c) 2007-2008 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.hibernate.eclipse.hqleditor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.hibernate.HibernateException;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.ImageConstants;
import org.hibernate.console.execution.ExecutionContext;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.EclipseImages;

/**
 * @author Dmitry Geraskov
 *
 */
public class LoadConsoleCFGCompletionProposal implements ICompletionProposal {

	private ConsoleConfiguration consoleConfiguration;

	public LoadConsoleCFGCompletionProposal(ConsoleConfiguration consoleConfiguration){
		this.consoleConfiguration = consoleConfiguration;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#apply(org.eclipse.jface.text.IDocument)
	 */
	public void apply(IDocument document) {
		//load console configuration
		if(consoleConfiguration.getConfiguration()==null) {
			try {
				consoleConfiguration.build();
				consoleConfiguration.execute( new ExecutionContext.Command() {

					public Object execute() {
						if(consoleConfiguration.hasConfiguration()) {
							consoleConfiguration.getConfiguration().buildMappings();
						}
						return consoleConfiguration;
					}
				} );
			} catch (HibernateException he) {
				HibernateConsolePlugin.getDefault().showError(HibernateConsolePlugin.getShell(), HibernateConsoleMessages.LoadConsoleCFGCompletionProposal_could_not_load_configuration + ' ' + consoleConfiguration.getName(), he);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getAdditionalProposalInfo()
	 */
	public String getAdditionalProposalInfo() {
		return HibernateConsoleMessages.LoadConsoleCFGCompletionProposal_no_open_console_cfg_found;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getContextInformation()
	 */
	public IContextInformation getContextInformation() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getDisplayString()
	 */
	public String getDisplayString() {
		return HibernateConsoleMessages.LoadConsoleCFGCompletionProposal_load_console_cfg;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getImage()
	 */
	public Image getImage() {
		return EclipseImages.getImage( ImageConstants.CONFIGURATION );
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getSelection(org.eclipse.jface.text.IDocument)
	 */
	public Point getSelection(IDocument document) {
		return null;
	}

}
