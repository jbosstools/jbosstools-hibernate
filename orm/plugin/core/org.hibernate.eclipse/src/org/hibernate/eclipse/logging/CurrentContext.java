package org.hibernate.eclipse.logging;

import java.util.Hashtable;
import java.util.Stack;

/**
 * CurrentContext is here to have one place where we can keep track on the
 * "current" console configuration to allow logging to be seperated per config.
 * 
 */

public class CurrentContext {

	static Hashtable<Thread, Stack<Object>> map = new Hashtable<Thread, Stack<Object>>();

	private CurrentContext() {
	}

	public static int getDepth() {
		Stack<Object> stack = map.get( Thread.currentThread() );
		if ( stack == null )
			return 0;
		else
			return stack.size();
	}

	public static Object pop() {
		Thread key = Thread.currentThread();
		Stack<Object> stack = map.get( key );
		if ( stack != null && !stack.isEmpty() )
			return (stack.pop() );
		else
			return null;
	}

	public static Object peek() {
		Thread key = Thread.currentThread();
		Stack<Object> stack = map.get( key );
		if ( stack != null && !stack.isEmpty() )
			return stack.peek();
		else
			return null;
	}

	public static void push(Object message) {
		Thread key = Thread.currentThread();
		Stack<Object> stack = map.get( key );

		if ( stack == null ) {
			stack = new Stack<Object>();
			map.put( key, stack );
			stack.push( message );
		}
		else {
			stack.push( message );
		}
	}

	static public void remove() {
		map.remove( Thread.currentThread() );
	}
}
