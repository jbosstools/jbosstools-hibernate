package org.hibernate.eclipse.console.test;

import bsh.EvalError;
import bsh.Interpreter;
import junit.framework.TestCase;

public class BeanShellIntegrationTest extends TestCase {

	public static class CallBack {
		public void line(int number) {
			System.out.println(number + ":"); //$NON-NLS-1$
		}
	}

	public void testBsh() {

		Interpreter bsh = new Interpreter();

		try {
			bsh.set("callback", new CallBack()); //$NON-NLS-1$

			StringBuffer buf = new StringBuffer();
			buf.append( "int i = 25;\r\n" ); //$NON-NLS-1$
			buf.append( "callback.line(1);\r\n" ); //$NON-NLS-1$
			buf.append( "int j = 23;\r\n" ); //$NON-NLS-1$
			buf.append( "callback.line(2);\r\n" ); //$NON-NLS-1$
			buf.append( "i+j;" ); //$NON-NLS-1$
			buf.append( "callback.line(3);" ); //$NON-NLS-1$
			Object object = bsh.eval( buf.toString() );

			System.out.println(ConsoleTestMessages.BeanShellIntegrationTest_result + object);

		}
		catch (EvalError e) {
			e.printStackTrace();
		}

	}
}
