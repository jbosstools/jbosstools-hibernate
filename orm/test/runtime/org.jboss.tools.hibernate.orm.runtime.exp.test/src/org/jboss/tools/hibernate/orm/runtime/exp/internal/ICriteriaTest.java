package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.tool.orm.jbt.util.MockConnectionProvider;
import org.hibernate.tool.orm.jbt.util.MockDialect;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.NewFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.ICriteria;
import org.jboss.tools.hibernate.runtime.spi.ISession;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import jakarta.persistence.Query;

public class ICriteriaTest {

	private static final String TEST_CFG_XML_STRING =
			"<hibernate-configuration>" +
			"  <session-factory>" + 
			"    <property name='" + AvailableSettings.DIALECT + "'>" + MockDialect.class.getName() + "</property>" +
			"    <property name='" + AvailableSettings.CONNECTION_PROVIDER + "'>" + MockConnectionProvider.class.getName() + "</property>" +
			"  </session-factory>" +
			"</hibernate-configuration>";
	
	private static final String TEST_HBM_XML_STRING =
			"<hibernate-mapping package='org.jboss.tools.hibernate.orm.runtime.exp.internal'>" +
			"  <class name='ICriteriaTest$Foo'>" + 
			"    <id name='id' access='field' />" +
			"    <set name='bars' access='field' >" +
			"      <key column='barId' />" +
			"      <element column='barVal' type='string' />" +
			"    </set>" +
			"  </class>" +
			"</hibernate-mapping>";
	
	static class Foo {
		public String id;
		public Set<String> bars = new HashSet<String>();
	}
	
	private static final NewFacadeFactory FACADE_FACTORY = NewFacadeFactory.INSTANCE;
	
	@TempDir
	public File tempDir;
	
	private ICriteria criteriaFacade = null;
	private Query criteriaTarget = null;
		
	@BeforeEach
	public void beforeEach() throws Exception {
		tempDir = Files.createTempDirectory("temp").toFile();
		File cfgXmlFile = new File(tempDir, "hibernate.cfg.xml");
		FileWriter fileWriter = new FileWriter(cfgXmlFile);
		fileWriter.write(TEST_CFG_XML_STRING);
		fileWriter.close();
		File hbmXmlFile = new File(tempDir, "Foo.hbm.xml");
		fileWriter = new FileWriter(hbmXmlFile);
		fileWriter.write(TEST_HBM_XML_STRING);
		fileWriter.close();
		IConfiguration configuration = FACADE_FACTORY.createNativeConfiguration();
		configuration.addFile(hbmXmlFile);
		configuration.configure(cfgXmlFile);
		ISessionFactory sessionFactoryFacade = configuration.buildSessionFactory();
		ISession sessionFacade = sessionFactoryFacade.openSession();
		criteriaFacade = sessionFacade.createCriteria(Foo.class);
		criteriaTarget = (Query)((IFacade)criteriaFacade).getTarget();
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(criteriaFacade);
		assertNotNull(criteriaTarget);
	}
	
	@Test
	public void testSetMaxResults() {
		assertNotEquals(1, criteriaTarget.getMaxResults());
		criteriaFacade.setMaxResults(1);
		assertEquals(1, criteriaTarget.getMaxResults());
	}
	
	@Test
	public void testList() throws Exception {
		final List<String> list = Arrays.asList("foo", "bar");
		Class<?> criteriaExtensionClass = Class.forName(
				"org.hibernate.tool.orm.jbt.wrp.CriteriaWrapperFactory$CriteriaExtension");
		Query query = (Query)Proxy.newProxyInstance(
				getClass().getClassLoader(),
				new Class[] { criteriaExtensionClass }, 
				new InvocationHandler() {					
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						if ("getResultList".equals(method.getName())) {
							return list;
						}
						return null;
					}
				});
		InvocationHandler invocationHandler = Proxy.getInvocationHandler(criteriaTarget);
		Field targetField = invocationHandler.getClass().getDeclaredField("target");
		targetField.setAccessible(true);
		targetField.set(invocationHandler, query);
		List<?> l1 = criteriaTarget.getResultList();
		assertSame(l1, list);
		List<?> l2 = criteriaFacade.list();
		assertSame(l1, l2);
	}
	
}
