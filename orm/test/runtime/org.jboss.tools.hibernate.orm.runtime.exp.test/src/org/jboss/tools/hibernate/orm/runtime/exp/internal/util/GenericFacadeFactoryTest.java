package org.jboss.tools.hibernate.orm.runtime.exp.internal.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.junit.jupiter.api.Test;

public class GenericFacadeFactoryTest {
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateFacade() {
		ArrayList<String> list = new ArrayList<String>();
		List<String> listFacade = (List<String>)GenericFacadeFactory.createFacade( List.class, list);
		assertTrue(listFacade.isEmpty());
		assertTrue(listFacade instanceof IFacade);
		assertSame(list, ((IFacade)listFacade).getTarget());
		list.add("foo");
		assertFalse(listFacade.isEmpty());
	}

}
