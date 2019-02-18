package org.jboss.tools.hibernate.orm.test;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import bsh.EvalError;
import bsh.Interpreter;

public class BeanShellIntegrationTest {
	
	private Set<Integer> lines = new HashSet<Integer>(3);

	public class CallBack {
		public void line(int number) {
			lines.add(number);
		}
	}

	@Test
	public void testBsh() {

		Interpreter bsh = new Interpreter();

		try {
			bsh.set("callback", new CallBack()); //$NON-NLS-1$

			StringBuffer buf = new StringBuffer();
			buf.append( "int i = 25;\r\n" ); //$NON-NLS-1$
			buf.append( "callback.line(1);\r\n" ); //$NON-NLS-1$
			buf.append( "int j = 23;\r\n" ); //$NON-NLS-1$
			buf.append( "callback.line(2);\r\n" ); //$NON-NLS-1$
			buf.append( "int k = i+j;" ); //$NON-NLS-1$
			buf.append( "callback.line(3);" ); //$NON-NLS-1$
			buf.append( "k;");

			Assert.assertTrue(lines.isEmpty());
			
			Object object = bsh.eval( buf.toString() );

			Assert.assertEquals(48, object);
			Assert.assertTrue(lines.contains(1));
			Assert.assertTrue(lines.contains(2));
			Assert.assertTrue(lines.contains(3));

		}
		catch (EvalError e) {
			e.printStackTrace();
		}

	}
}
