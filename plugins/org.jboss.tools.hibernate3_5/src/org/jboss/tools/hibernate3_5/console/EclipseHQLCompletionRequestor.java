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
package org.jboss.tools.hibernate3_5.console;

import java.util.List;

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.hibernate.tool.ide.completion.HQLCompletionProposal;
import org.hibernate.tool.ide.completion.IHQLCompletionRequestor;
import org.jboss.tools.hibernate.proxy.HQLCompletionProposalProxy;
import org.jboss.tools.hibernate.spi.IHQLCompletionHandler;

public class EclipseHQLCompletionRequestor implements IHQLCompletionRequestor, org.jboss.tools.hibernate.spi.IHQLCompletionRequestor {

	private IHQLCompletionHandler handler;
	
	public EclipseHQLCompletionRequestor(IHQLCompletionHandler helper) {
		this.handler = helper;
	}
	
	public List<ICompletionProposal> getCompletionProposals() {		
		return handler.getCompletionProposals();
	}

	public boolean accept(HQLCompletionProposal proposal) {
		return handler.accept(new HQLCompletionProposalProxy(proposal));
	}

	public void completionFailure(String errorMessage) {
		handler.completionFailure(errorMessage);		
	}
	
	public String getLastErrorMessage() {
		return handler.getLastErrorMessage();
	}
	
	public void clear() {
		handler.clear();;
	}

}
