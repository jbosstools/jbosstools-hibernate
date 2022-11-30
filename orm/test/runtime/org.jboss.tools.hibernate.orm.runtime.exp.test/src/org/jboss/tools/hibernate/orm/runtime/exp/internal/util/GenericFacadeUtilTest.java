package org.jboss.tools.hibernate.orm.runtime.exp.internal.util;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.junit.jupiter.api.Test;

public class GenericFacadeUtilTest {
	
	@Test
	public void testSetTarget() throws Exception {
		TargetObject firstTarget = new TargetObject();
		IFacade genericFacadeObject = GenericFacadeFactory.createFacade(FacadeInterface.class, firstTarget);
		assertSame(firstTarget, genericFacadeObject.getTarget());
		TargetObject secondTarget = new TargetObject();
		assertNotSame(secondTarget, genericFacadeObject.getTarget());
		GenericFacadeUtil.setTarget(genericFacadeObject, secondTarget);
		assertSame(secondTarget, genericFacadeObject.getTarget());
	}
	
	public interface FacadeInterface {
		public void testMethod();
	}
	
	public class TargetObject {
		public void testMethod() {}
	}

}
