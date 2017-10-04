package org.jboss.tools.hibernate.search.test.db;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class TestDbStarter {

	private static Process dbProcess;
	
	private static class LazyHolder {
        static final TestDbStarter INSTANCE = new TestDbStarter();
    }
	
	private TestDbStarter() {
		starDBInternal();
	};

	public static boolean startDB() {
		if (checkStarted()) {
			return true;
		}
		starDBInternal(); //try once again
		return checkStarted();
	}
	
	private static boolean checkStarted() {
		if (dbProcess != null && dbProcess.isAlive()) {
			return true;
		}
		return false;
	}
	
	private static void starDBInternal() {
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.directory(new File("../org.jboss.tools.hibernate.search.test/resources/db"));
		processBuilder.command("java", "-cp", "h2-1.3.161.jar", "org.h2.tools.Server", "-ifExists", "-tcp", "-web");
		try {
			dbProcess = processBuilder.start();
		} catch (IOException io) {
			io.printStackTrace();
		}
	}
	
	public static void stopDB() {
		if (dbProcess != null) {
			dbProcess.destroy();
		}
	}
}
