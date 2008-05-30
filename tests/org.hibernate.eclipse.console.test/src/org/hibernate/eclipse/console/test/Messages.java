package org.hibernate.eclipse.console.test;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.hibernate.eclipse.console.test.messages"; //$NON-NLS-1$
	public static String BEANSHELLINTEGRATIONTEST_RESULT;
	public static String CONSOLECONFIGTEST_FACTORY_ALREADY_EXISTS;
	public static String CONSOLECONFIGTEST_FAKE_PREFS;
	public static String CONSOLEPLUGINALLTESTS_TEST_FOR;
	public static String HIBERNATECONSOLETEST_LONG_RUNNING_TASK_DETECTED;
	public static String KNOWNCONFIGURATIONSTEST_FAKE_PREFS;
	public static String KNOWNCONFIGURATIONSTEST_NEW_TEST;
	public static String KNOWNCONFIGURATIONSTEST_NO_SF_SHOULD_BE_BUILD;
	public static String KNOWNCONFIGURATIONSTEST_NO_SF_SHOULD_BE_CLOSED;
	public static String KNOWNCONFIGURATIONSTEST_TRYING_REMOVE_NON_EXISTING_CONSOLE;
	public static String PERSPECTIVETEST_HIBERNATE;
	public static String REFACTORINGTEST_CATEGORY;
	public static String REFACTORINGTEST_COREEXCEPTION_OCCURED_WORK_WITH_MEMENTO;
	public static String REFACTORINGTEST_EXCEPTION_WHILE_FILENAMECHANGE_REFACTOR;
	public static String REFACTORINGTEST_EXCEPTION_WHILE_FINDCHANGE_LAUNCH_CONFIG_PROCESSING;
	public static String REFACTORINGTEST_EXCEPTION_WHILE_PACKNAMECHANGE_REFACTOR;
	public static String REFACTORINGTEST_EXCEPTION_WHILE_PROJNAMECHANGE_REFACTOR;
	public static String REFACTORINGTEST_EXCEPTION_WHILE_SRCNAMECHANGE_REFACTOR;
	public static String REFACTORINGTEST_METHOD_NOT_TESTED;
	public static String REFACTORINGTEST_SEGMENTNUM_TOO_MATCH;
	public static String REFACTORINGTEST_TEST_LAUNCH_CONFIG;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
