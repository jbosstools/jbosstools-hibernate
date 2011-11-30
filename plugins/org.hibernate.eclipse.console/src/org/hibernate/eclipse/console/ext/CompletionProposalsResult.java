/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.hibernate.eclipse.console.ext;

import java.util.List;

import org.eclipse.jface.text.contentassist.ICompletionProposal;

/**
 * @author Dmitry Geraskov
 *
 */
public class CompletionProposalsResult {
	
	private final List<ICompletionProposal> result;
	
	private final String errorMessage;
	
	public CompletionProposalsResult(List<ICompletionProposal> result, String errorMessage){
		this.result = result;
		this.errorMessage = errorMessage;
	}
	
	public List<ICompletionProposal> getCompletionProposals() {		
		return result;
	}
	
	public String getErrorMessage() {		
		return errorMessage;
	}

}
