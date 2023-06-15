package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.tool.ide.completion.HQLCodeAssist;
import org.hibernate.tool.orm.jbt.util.NativeConfiguration;
import org.hibernate.tool.orm.jbt.wrp.WrapperFactory;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.GenericFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IHQLCodeAssist;
import org.jboss.tools.hibernate.runtime.spi.IHQLCompletionHandler;
import org.jboss.tools.hibernate.runtime.spi.IHQLCompletionProposal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IHQLCodeAssistTest {
	
	private IHQLCodeAssist hqlCodeAssistFacade = null;
	private HQLCodeAssist hqlCodeAssistTarget = null;
	
	@BeforeEach
	public void beforeEach() {
		NativeConfiguration configuration = new NativeConfiguration();
		configuration.setProperty("hibernate.connection.url", "jdbc:h2:mem:test");
		hqlCodeAssistFacade = (IHQLCodeAssist)GenericFacadeFactory.createFacade(
				IHQLCodeAssist.class, 
				WrapperFactory.createHqlCodeAssistWrapper(configuration));
		hqlCodeAssistTarget = (HQLCodeAssist)((IFacade)hqlCodeAssistFacade).getTarget();
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(hqlCodeAssistFacade);
		assertNotNull(hqlCodeAssistTarget);
	}
	
	@Test
	public void testCodeComplete() throws Exception {
		TestHQLCompletionHandler completionHandler = new TestHQLCompletionHandler();
		// First test the 'accept' method of the handler
		assertTrue(completionHandler.acceptedProposals.isEmpty());
		hqlCodeAssistFacade.codeComplete("foo", 0, completionHandler);
		assertFalse(completionHandler.acceptedProposals.isEmpty());
		// Now force the 'metadata' field to null to test the 'completionFailure' method of the handler
		Field metadataField = HQLCodeAssist.class.getDeclaredField("metadata");
		metadataField.setAccessible(true);
		metadataField.set(hqlCodeAssistTarget, null);
		assertNull(completionHandler.errorMessage);
		hqlCodeAssistFacade.codeComplete("FROM ", 5, completionHandler);
		assertNotNull(completionHandler.errorMessage);
		
	}

	public class TestHQLCompletionHandler implements IHQLCompletionHandler {
		
		List<IHQLCompletionProposal> acceptedProposals = new ArrayList<>();
		String errorMessage = null;
		
		@Override
		public boolean accept(IHQLCompletionProposal proposal) {
			acceptedProposals.add(proposal);
			return true;
		}
		@Override
		public void completionFailure(String errorMessage) {
			this.errorMessage = errorMessage;
		}		
	}
}
