package org.hibernate.eclipse.console;

import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.QueryInputModel;

public interface QueryEditor {

	ConsoleConfiguration getConsoleConfiguration();

	boolean askUserForConfiguration(String name);

	String getQueryString();

	QueryInputModel getQueryInputModel();

	void executeQuery(ConsoleConfiguration cfg);

}
