package org.jboss.tools.hibernate.runtime.v_4_3.internal;

import org.hibernate.tool.ide.completion.HQLCompletionProposal;
import org.jboss.tools.hibernate.runtime.common.AbstractHQLCompletionProposalFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IProperty;

public class HQLCompletionProposalFacadeImpl 
extends AbstractHQLCompletionProposalFacade {
	
	public HQLCompletionProposalFacadeImpl(
			IFacadeFactory facadeFactory,
			HQLCompletionProposal proposal) {
		super(facadeFactory, proposal);
	}

	public HQLCompletionProposal getTarget() {
		return (HQLCompletionProposal)super.getTarget();
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
		return getTarget().getProperty() != null ? 
				getFacadeFactory().createProperty(getTarget().getProperty()) :
					null;
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
