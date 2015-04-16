package org.jboss.tools.hibernate.proxy;

import org.hibernate.tool.ide.completion.HQLCompletionProposal;
import org.jboss.tools.hibernate.runtime.common.AbstractHQLCompletionProposalFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IProperty;

public class HQLCompletionProposalProxy 
extends AbstractHQLCompletionProposalFacade {
	
	public HQLCompletionProposalProxy(
			IFacadeFactory facadeFactory,
			HQLCompletionProposal proposal) {
		super(facadeFactory, proposal);
	}

	public HQLCompletionProposal getTarget() {
		return (HQLCompletionProposal)super.getTarget();
	}

	@Override
	public String getCompletion() {
		return getTarget().getCompletion();
	}

	@Override
	public int getReplaceStart() {
		return getTarget().getReplaceStart();
	}

	@Override
	public int getReplaceEnd() {
		return getTarget().getReplaceEnd();
	}

	@Override
	public String getSimpleName() {
		return getTarget().getSimpleName();
	}

	@Override
	public int getCompletionKind() {
		return getTarget().getCompletionKind();
	}

	@Override
	public String getEntityName() {
		return getTarget().getEntityName();
	}

	@Override
	public String getShortEntityName() {
		return getTarget().getShortEntityName();
	}

	@Override
	public IProperty getProperty() {
		return getTarget().getProperty() != null ? new PropertyProxy(getFacadeFactory(), getTarget().getProperty()) : null;
	}

	@Override
	public int aliasRefKind() {
		return HQLCompletionProposal.ALIAS_REF;
	}

	@Override
	public int entityNameKind() {
		return HQLCompletionProposal.ENTITY_NAME;
	}

	@Override
	public int propertyKind() {
		return HQLCompletionProposal.PROPERTY;
	}

	@Override
	public int keywordKind() {
		return HQLCompletionProposal.KEYWORD;
	}

	@Override
	public int functionKind() {
		return HQLCompletionProposal.FUNCTION;
	}

}
