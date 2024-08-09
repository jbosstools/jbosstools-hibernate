package org.jboss.tools.hibernate.orm.runtime.v_6_6;

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
import org.hibernate.query.Query;
import org.hibernate.tool.orm.jbt.api.factory.WrapperFactory;
import org.hibernate.tool.orm.jbt.api.wrp.Wrapper;
import org.hibernate.tool.orm.jbt.internal.util.MockConnectionProvider;
import org.hibernate.tool.orm.jbt.internal.util.MockDialect;
import org.jboss.tools.hibernate.orm.runtime.common.GenericFacadeFactory;
import org.jboss.tools.hibernate.orm.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.ICriteria;
import org.jboss.tools.hibernate.runtime.spi.ISession;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class ICriteriaTest {

	private static final String TEST_CFG_XML_STRING =
			"<hibernate-configuration>" +
			"  <session-factory>" + 
			"    <property name='" + AvailableSettings.DIALECT + "'>" + MockDialect.class.getName() + "</property>" +
			"    <property name='" + AvailableSettings.CONNECTION_PROVIDER + "'>" + MockConnectionProvider.class.getName() + "</property>" +
			"  </session-factory>" +
			"</hibernate-configuration>";
	
	private static final String TEST_HBM_XML_STRING =
			"<hibernate-mapping package='org.jboss.tools.hibernate.orm.runtime.v_6_6'>" +
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
	
	@TempDir
	public File tempDir;
	
	private ICriteria criteriaFacade = null;
	private Wrapper criteriaWrapper = null;
	private Query<?> criteriaTarget = null;
		
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
		IConfiguration configuration = (IConfiguration)GenericFacadeFactory.createFacade(
				IConfiguration.class, 
				WrapperFactory.createNativeConfigurationWrapper());
		configuration.addFile(hbmXmlFile);
		configuration.configure(cfgXmlFile);
		ISessionFactory sessionFactoryFacade = configuration.buildSessionFactory();
		ISession sessionFacade = sessionFactoryFacade.openSession();
		criteriaFacade = sessionFacade.createCriteria(Foo.class);
		criteriaWrapper = (Wrapper)((IFacade)criteriaFacade).getTarget();
		criteriaTarget = (Query<?>)criteriaWrapper.getWrappedObject();
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
		Query<?> query = (Query<?>)Proxy.newProxyInstance(
				getClass().getClassLoader(),
				new Class[] { Query.class }, 
				new InvocationHandler() {					
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						if ("list".equals(method.getName())) {
							return list;
						}
						return null;
					}
				});		
		Field queryField = criteriaWrapper.getClass().getDeclaredField("query");
		queryField.setAccessible(true);
		queryField.set(criteriaWrapper, query);
		List<?> l1 = query.list();
		assertSame(l1, list);
		List<?> l2 = criteriaFacade.list();
		assertSame(l1, l2);
	}
	
}
