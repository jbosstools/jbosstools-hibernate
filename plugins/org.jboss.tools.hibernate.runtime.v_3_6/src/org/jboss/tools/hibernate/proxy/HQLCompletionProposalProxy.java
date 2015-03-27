package org.jboss.tools.hibernate.proxy;

import org.hibernate.tool.ide.completion.HQLCompletionProposal;
import org.jboss.tools.hibernate.runtime.common.AbstractHQLCompletionProposalFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IProperty;

public class HQLCompletionProposalProxy 
extends AbstractHQLCompletionProposalFacade {
	
	private HQLCompletionProposal target = null;

	public HQLCompletionProposalProxy(
			IFacadeFactory facadeFactory,
			HQLCompletionProposal proposal) {
		super(facadeFactory, proposal);
		target = proposal;
	}

	@Override
	public String getCompletion() {
		return target.getCompletion();
	}

	@Override
	public int getReplaceStart() {
		return target.getReplaceStart();
	}

	@Override
	public int getReplaceEnd() {
		return target.getReplaceEnd();
	}

	@Override
	public String getSimpleName() {
		return target.getSimpleName();
	}

	@Override
	public int getCompletionKind() {
		return target.getCompletionKind();
	}

	@Override
	public String getEntityName() {
		return target.getEntityName();
	}

	@Override
	public String getShortEntityName() {
		return target.getShortEntityName();
	}

	@Override
	public IProperty getProperty() {
		return target.getProperty() != null ? new PropertyProxy(target.getProperty()) : null;
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
