package org.hibernate.eclipse.console.test;

import bsh.EvalError;
import bsh.Interpreter;
import junit.framework.TestCase;

public class BeanShellIntegrationTest extends TestCase {

	public static class CallBack {
		public void line(int number) {
			System.out.println(number + ":");
		}	
	}
	
	public void testBsh() {
		
		Interpreter bsh = new Interpreter();
		
		try {
			bsh.set("callback", new CallBack());
			
			StringBuffer buf = new StringBuffer();
			buf.append( "int i = 25;\r\n" );
			buf.append( "callback.line(1);\r\n" );
			buf.append( "int j = 23;\r\n" );
			buf.append( "callback.line(2);\r\n" );
			buf.append( "i+j;" );
			buf.append( "callback.line(3);" );
			Object object = bsh.eval( buf.toString() );
			
			System.out.println("Result: " + object);
				
		}
		catch (EvalError e) {
			e.printStackTrace();
		}
		
	}
}
