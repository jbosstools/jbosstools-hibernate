package org.hibernate.console.execution;

public interface ExecutionContext {

	public interface Command {
		public Object execute();
	}

	public abstract void installLoader();

	public abstract Object execute(Command c);

	public abstract void uninstallLoader();

}