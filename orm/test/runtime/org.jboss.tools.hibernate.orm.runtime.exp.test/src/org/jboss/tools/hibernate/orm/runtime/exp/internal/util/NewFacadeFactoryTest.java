package org.jboss.tools.hibernate.orm.runtime.exp.internal.util;

import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.internal.reveng.strategy.DelegatingStrategy;

public class NewFacadeFactoryTest {

	public static class TestRevengStrategy extends DelegatingStrategy {
		public TestRevengStrategy(RevengStrategy delegate) {
			super(delegate);
		}
	}
	
}
