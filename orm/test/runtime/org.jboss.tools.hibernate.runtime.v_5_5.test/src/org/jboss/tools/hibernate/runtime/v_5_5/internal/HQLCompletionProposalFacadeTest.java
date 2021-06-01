package org.jboss.tools.hibernate.runtime.v_5_5.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.lang.reflect.Field;

import org.hibernate.tool.ide.completion.HQLCompletionProposal;
import org.jboss.tools.hibernate.runtime.common.AbstractHQLCompletionProposalFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IHQLCompletionProposal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HQLCompletionProposalFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();

	private IHQLCompletionProposal hqlCompletionProposalFacade = null; 
	private HQLCompletionProposal hqlCompletionProposalTarget = null;
	
	@BeforeEach
	public void before() {
		hqlCompletionProposalTarget = new HQLCompletionProposal(HQLCompletionProposal.PROPERTY, Integer.MAX_VALUE);
		hqlCompletionProposalFacade = new AbstractHQLCompletionProposalFacade(FACADE_FACTORY, hqlCompletionProposalTarget) {};
	}
	
	@Test
	public void testGetCompletion() throws Exception {
		Field completionField = HQLCompletionProposal.class.getDeclaredField("completion");
		completionField.setAccessible(true);
		assertNotEquals("foo", hqlCompletionProposalFacade.getCompletion());
		completionField.set(hqlCompletionProposalTarget, "foo");
		assertEquals("foo", hqlCompletionProposalFacade.getCompletion());
	}
	
	@Test
	public void testGetReplaceStart() throws Exception {
		Field replaceStartField = HQLCompletionProposal.class.getDeclaredField("replaceStart");
		replaceStartField.setAccessible(true);
		assertNotEquals(Integer.MAX_VALUE, hqlCompletionProposalFacade.getReplaceStart());
		replaceStartField.set(hqlCompletionProposalTarget, Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, hqlCompletionProposalFacade.getReplaceStart());
	}
	
	@Test
	public void testGetReplaceEnd() throws Exception {
		Field replaceEndField = HQLCompletionProposal.class.getDeclaredField("replaceEnd");
		replaceEndField.setAccessible(true);
		assertNotEquals(Integer.MIN_VALUE, hqlCompletionProposalFacade.getReplaceEnd());
		replaceEndField.set(hqlCompletionProposalTarget, Integer.MIN_VALUE);
		assertEquals(Integer.MIN_VALUE, hqlCompletionProposalFacade.getReplaceEnd());
	}
	
	@Test
	public void testGetSimpleName() throws Exception {
		Field simpleNameField = HQLCompletionProposal.class.getDeclaredField("simpleName");
		simpleNameField.setAccessible(true);
		assertNotEquals("foo", hqlCompletionProposalFacade.getSimpleName());
		simpleNameField.set(hqlCompletionProposalTarget, "foo");
		assertEquals("foo", hqlCompletionProposalFacade.getSimpleName());
	}
	
	@Test
	public void testGetCompletionKind() throws Exception {
		Field completionKindField = HQLCompletionProposal.class.getDeclaredField("completionKind");
		completionKindField.setAccessible(true);
		assertEquals(HQLCompletionProposal.PROPERTY, hqlCompletionProposalFacade.getCompletionKind());
		completionKindField.set(hqlCompletionProposalTarget, HQLCompletionProposal.KEYWORD);
		assertEquals(HQLCompletionProposal.KEYWORD, hqlCompletionProposalFacade.getCompletionKind());
	}
	
}
