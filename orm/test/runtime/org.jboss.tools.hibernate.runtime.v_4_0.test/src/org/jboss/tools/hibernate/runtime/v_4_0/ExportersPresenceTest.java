package org.jboss.tools.hibernate.runtime.v_4_0;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class ExportersPresenceTest {
	
	@Test
	public void testDdlExporter() {
		try {
			ClassLoader cl = getClass().getClassLoader();
			Class<?> ddlExporterClass = cl.loadClass("org.hibernate.tool.hbm2x.Hbm2DDLExporter");
			assertNotNull(ddlExporterClass);
		} catch (Throwable t) {
			fail(t);
		}
	}

}
