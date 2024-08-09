package org.jboss.tools.hibernate.orm.runtime.v_6_6;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.tool.orm.jbt.api.factory.WrapperFactory;
import org.hibernate.tool.orm.jbt.api.wrp.Wrapper;
import org.hibernate.tool.orm.jbt.internal.util.MockConnectionProvider;
import org.hibernate.tool.orm.jbt.internal.util.MockDialect;
import org.hibernate.type.CollectionType;
import org.hibernate.type.Type;
import org.hibernate.type.internal.NamedBasicTypeImpl;
import org.jboss.tools.hibernate.orm.runtime.common.GenericFacadeFactory;
import org.jboss.tools.hibernate.orm.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IClassMetadata;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.ISession;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class IClassMetadataTest {

	private static final String TEST_CFG_XML_STRING =
			"<hibernate-configuration>" +
			"  <session-factory>" + 
			"    <property name='" + AvailableSettings.DIALECT + "'>" + MockDialect.class.getName() + "</property>" +
			"    <property name='" + AvailableSettings.CONNECTION_PROVIDER + "'>" + MockConnectionProvider.class.getName() + "</property>" +
			"  </session-factory>" +
			"</hibernate-configuration>";
	
	private static final String TEST_HBM_XML_STRING =
			"<hibernate-mapping package='org.jboss.tools.hibernate.orm.runtime.v_6_6'>" +
			"  <class name='IClassMetadataTest$Foo'>" + 
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
	
	private EntityPersister classMetadataTarget = null;
	private IClassMetadata classMetadataFacade = null;
	
	private ISessionFactory sessionFactoryFacade = null;
	
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
		sessionFactoryFacade = configuration.buildSessionFactory();
		classMetadataFacade = sessionFactoryFacade.getClassMetadata(Foo.class.getName());
		Wrapper wrapper = (Wrapper)((IFacade)classMetadataFacade).getTarget();
		classMetadataTarget = (EntityPersister)wrapper.getWrappedObject();
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(classMetadataTarget);
		assertNotNull(classMetadataFacade);
	}
	
	@Test
	public void testGetEntityName() {
		assertEquals(Foo.class.getName(), classMetadataFacade.getEntityName());
	}
	
	@Test
	public void testGetIdentifierPropertyName() {
		assertEquals("id", classMetadataFacade.getIdentifierPropertyName());
	}
	
	@Test
	public void testGetPropertyNames() {
		String[] propertyNames = classMetadataFacade.getPropertyNames();
 		assertEquals(1, propertyNames.length);
 		assertEquals("bars", propertyNames[0]);
	}
	
	@Test
	public void testGetPropertyTypes() {
		IType[] typeFacades = classMetadataFacade.getPropertyTypes();
		assertEquals(1, typeFacades.length);
		IType typeFacade = typeFacades[0];
		assertTrue(typeFacade instanceof IFacade);
		Object typeWrapper = ((IFacade)typeFacade).getTarget();
		assertTrue(typeWrapper instanceof Wrapper);
		Object wrappedType = ((Wrapper)typeWrapper).getWrappedObject();
		assertTrue(wrappedType instanceof Type);
		assertTrue(((Type)wrappedType).isCollectionType());
		assertEquals(
				"org.jboss.tools.hibernate.orm.runtime.v_6_6.IClassMetadataTest$Foo.bars",
				((CollectionType)wrappedType).getRole());
 	}
	
	@Test
	public void testGetMappedClass() {
		assertSame(Foo.class, classMetadataFacade.getMappedClass());
	}
	
	@Test
	public void testGetIdentifierType() {
		IType typeFacade = classMetadataFacade.getIdentifierType();
		assertNotNull(typeFacade);
		assertTrue(typeFacade instanceof IFacade);
		Object typeWrapper = ((IFacade)typeFacade).getTarget();
		assertNotNull(typeWrapper);
		assertTrue(typeWrapper instanceof Wrapper);
		Object wrappedType = ((Wrapper)typeWrapper).getWrappedObject();
		assertNotNull(wrappedType);
		assertTrue(wrappedType instanceof NamedBasicTypeImpl);
		assertSame("string", ((NamedBasicTypeImpl<?>)wrappedType).getName());
	}
	
	@Test
	public void testGetPropertyValue() {
		Foo foo = new Foo();
		Set<String> foobarSet = new HashSet<String>(Arrays.asList("foo", "bar"));
		foo.bars = foobarSet;
		assertSame(foobarSet, classMetadataFacade.getPropertyValue(foo, "bars"));
	}
	
	@Test
	public void testHasIdentifierProperty() {
		assertTrue(classMetadataFacade.hasIdentifierProperty());
	}
	
	@Test 
	public void testGetIdentifier() {
		ISession sessionFacade = sessionFactoryFacade.openSession();
		Foo foo = new Foo();
		foo.id = "bar";
		Object identifier = classMetadataFacade.getIdentifier(foo, sessionFacade);
		assertSame("bar", identifier);
	}
	
	@Test
	public void testIsInstanceOfAbstractEntityPersister() {
		assertTrue(classMetadataFacade.isInstanceOfAbstractEntityPersister());
	}
	
	@Test
	public void testGetTuplizerPropertyValue() {
		Foo foo = new Foo();
		Set<String> bars = Set.of("foo", "bar");
		foo.bars = bars;
		assertSame(bars, classMetadataFacade.getTuplizerPropertyValue(foo, 0));
	}
	
	@Test
	public void testGetPropertyIndexOrNull() {
		assertSame(0, classMetadataFacade.getPropertyIndexOrNull("bars"));
		assertNull(classMetadataFacade.getPropertyIndexOrNull("foo"));
	}
	
	
}
