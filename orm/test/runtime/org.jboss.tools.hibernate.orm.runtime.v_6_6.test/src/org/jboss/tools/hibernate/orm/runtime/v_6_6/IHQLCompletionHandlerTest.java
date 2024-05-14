package org.jboss.tools.hibernate.orm.runtime.v_6_6;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.hibernate.tool.orm.jbt.wrp.WrapperFactory;
import org.jboss.tools.hibernate.orm.runtime.common.GenericFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IHQLCompletionHandler;
import org.jboss.tools.hibernate.runtime.spi.IHQLCompletionProposal;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.junit.jupiter.api.Test;

public class IHQLCompletionHandlerTest {
	
	private IHQLCompletionHandler hqlCompletionHandler = new TestHqlCompletionHandler();
	private TestHqlCompletionInfo hqlCompletionInfo = new TestHqlCompletionInfo();
	
	@Test
	public void testAccept() {
		Object completionProposal = new TestHqlCompletionProposal();
		assertNull(hqlCompletionInfo.completion);
		assertEquals(Integer.MIN_VALUE, hqlCompletionInfo.replaceStart);
		assertEquals(Integer.MIN_VALUE, hqlCompletionInfo.replaceEnd);
		assertNull(hqlCompletionInfo.simpleName);
		assertEquals(Integer.MIN_VALUE, hqlCompletionInfo.completionKind);
		assertNull(hqlCompletionInfo.entityName);
		assertNull(hqlCompletionInfo.shortEntityName);
		assertNull(hqlCompletionInfo.property);
		assertEquals(Integer.MIN_VALUE, hqlCompletionInfo.aliasRefKind);
		assertEquals(Integer.MIN_VALUE, hqlCompletionInfo.entityNameKind);
		assertEquals(Integer.MIN_VALUE, hqlCompletionInfo.propertyKind);
		assertEquals(Integer.MIN_VALUE, hqlCompletionInfo.keywordKind);
		assertEquals(Integer.MIN_VALUE, hqlCompletionInfo.functionKind);
		hqlCompletionHandler.accept(completionProposal);
		assertEquals("foobar", hqlCompletionInfo.completion);
		assertEquals(Integer.MAX_VALUE, hqlCompletionInfo.replaceStart);
		assertEquals(Integer.MAX_VALUE, hqlCompletionInfo.replaceEnd);
		assertEquals("foobar", hqlCompletionInfo.simpleName);
		assertEquals(Integer.MAX_VALUE, hqlCompletionInfo.completionKind);
		assertEquals("foobar", hqlCompletionInfo.entityName);
		assertEquals("foobar", hqlCompletionInfo.shortEntityName);
		assertNotNull(hqlCompletionInfo.property);
		assertEquals(Integer.MAX_VALUE, hqlCompletionInfo.aliasRefKind);
		assertEquals(Integer.MAX_VALUE, hqlCompletionInfo.entityNameKind);
		assertEquals(Integer.MAX_VALUE, hqlCompletionInfo.propertyKind);
		assertEquals(Integer.MAX_VALUE, hqlCompletionInfo.keywordKind);
		assertEquals(Integer.MAX_VALUE, hqlCompletionInfo.functionKind);
	}
	
	class TestHqlCompletionInfo {
		String completion = null;
		int replaceStart = Integer.MIN_VALUE;
		int replaceEnd = Integer.MIN_VALUE;
		String simpleName = null;
		int completionKind = Integer.MIN_VALUE;
		String entityName = null; 
		String shortEntityName = null; 
		IProperty property = null; 
		
		int aliasRefKind = Integer.MIN_VALUE;
		int entityNameKind = Integer.MIN_VALUE;
		int propertyKind = Integer.MIN_VALUE;
		int keywordKind = Integer.MIN_VALUE;
		int functionKind = Integer.MIN_VALUE;		
	}
	
	class TestHqlCompletionHandler implements IHQLCompletionHandler {
		
		@Override
		public boolean accept(IHQLCompletionProposal proposal) {
			hqlCompletionInfo.completion = proposal.getCompletion();
			hqlCompletionInfo.replaceStart = proposal.getReplaceStart();
			hqlCompletionInfo.replaceEnd = proposal.getReplaceEnd();
			hqlCompletionInfo.simpleName = proposal.getSimpleName();
			hqlCompletionInfo.completionKind = proposal.getCompletionKind();
			hqlCompletionInfo.entityName = proposal.getEntityName();
			hqlCompletionInfo.shortEntityName = proposal.getShortEntityName();
			hqlCompletionInfo.property = proposal.getProperty();
			hqlCompletionInfo.aliasRefKind = proposal.aliasRefKind();
			hqlCompletionInfo.entityNameKind = proposal.entityNameKind();
			hqlCompletionInfo.propertyKind = proposal.propertyKind();
			hqlCompletionInfo.keywordKind = proposal.keywordKind();
			hqlCompletionInfo.functionKind = proposal.functionKind();
			return false;
		}

		@Override
		public void completionFailure(String errorMessage) {}
		
	}
	
	public class TestHqlCompletionProposal {
		public String getCompletion() { return "foobar"; }
		public int getReplaceStart() { return Integer.MAX_VALUE; }
		public int getReplaceEnd() { return Integer.MAX_VALUE; }
		public String getSimpleName() { return "foobar"; }
		public int getCompletionKind() { return Integer.MAX_VALUE; }
		public String getEntityName() { return "foobar"; }
		public String getShortEntityName() { return "foobar"; }
		public IProperty getProperty() { 
			return (IProperty)GenericFacadeFactory.createFacade(
				IProperty.class, 
				WrapperFactory.createPropertyWrapper()); 
		}
		
		public int aliasRefKind() { return Integer.MAX_VALUE; }
		public int entityNameKind() { return Integer.MAX_VALUE; }
		public int propertyKind() { return Integer.MAX_VALUE; }
		public int keywordKind() { return Integer.MAX_VALUE; }
		public int functionKind() { return Integer.MAX_VALUE; }
	}
	
	

}
