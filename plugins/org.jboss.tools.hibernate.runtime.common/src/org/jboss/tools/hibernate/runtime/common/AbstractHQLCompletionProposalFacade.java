package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IHQLCompletionProposal;

public abstract class AbstractHQLCompletionProposalFacade 
extends AbstractFacade 
implements IHQLCompletionProposal {

	public AbstractHQLCompletionProposalFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

	@Override
	public String getCompletion() {
		return (String)Util.invokeMethod(
				getTarget(), 
				"getCompletion", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public int getReplaceStart() {
		return (int)Util.invokeMethod(
				getTarget(), 
				"getReplaceStart", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public int getReplaceEnd() {
		return (int)Util.invokeMethod(
				getTarget(), 
				"getReplaceEnd", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public String getSimpleName() {
		return (String)Util.invokeMethod(
				getTarget(), 
				"getSimpleName", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public int getCompletionKind() {
		return (int)Util.invokeMethod(
				getTarget(), 
				"getCompletionKind", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public String getEntityName() {
		return (String)Util.invokeMethod(
				getTarget(), 
				"getEntityName", 
				new Class[] {}, 
				new Object[] {});
	}

}
