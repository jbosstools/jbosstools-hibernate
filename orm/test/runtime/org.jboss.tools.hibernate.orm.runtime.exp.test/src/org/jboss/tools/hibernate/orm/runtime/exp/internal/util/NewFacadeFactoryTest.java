package org.jboss.tools.hibernate.orm.runtime.exp.internal.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.hibernate.mapping.Array;
import org.hibernate.mapping.Bag;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.JoinedSubclass;
import org.hibernate.mapping.List;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.Map;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.OneToOne;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.PrimitiveArray;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Set;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.ide.completion.HQLCompletionProposal;
import org.hibernate.tool.internal.export.common.GenericExporter;
import org.hibernate.tool.internal.reveng.strategy.DelegatingStrategy;
import org.hibernate.tool.internal.reveng.strategy.TableFilter;
import org.hibernate.tool.orm.jbt.util.SpecialRootClass;
import org.hibernate.tool.orm.jbt.wrp.EnvironmentWrapper;
import org.hibernate.tool.orm.jbt.wrp.HbmExporterWrapper;
import org.hibernate.tool.orm.jbt.wrp.HqlCodeAssistWrapper;
import org.hibernate.tool.orm.jbt.wrp.PersistentClassWrapper;
import org.hibernate.tool.orm.jbt.wrp.SchemaExportWrapper;
import org.hibernate.tool.orm.jbt.wrp.TypeFactoryWrapper;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;
import org.hibernate.tool.orm.jbt.wrp.WrapperFactory;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IEnvironment;
import org.jboss.tools.hibernate.runtime.spi.IExporter;
import org.jboss.tools.hibernate.runtime.spi.IHQLCodeAssist;
import org.jboss.tools.hibernate.runtime.spi.IHQLCompletionProposal;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingExporter;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.ISchemaExport;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.jboss.tools.hibernate.runtime.spi.ITableFilter;
import org.jboss.tools.hibernate.runtime.spi.ITypeFactory;
import org.jboss.tools.hibernate.runtime.spi.IValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NewFacadeFactoryTest {

	private NewFacadeFactory facadeFactory;

	@BeforeEach
	public void beforeEach() throws Exception {
		facadeFactory = NewFacadeFactory.INSTANCE;
	}
		
	@Test
	public void testCreateJoinedTableSubclass() {
		IPersistentClass rootClassFacade = (IPersistentClass)GenericFacadeFactory.createFacade(
				IPersistentClass.class, 
				WrapperFactory.createRootClassWrapper());
		Object rootClassTarget = ((IFacade)rootClassFacade).getTarget();
		IPersistentClass joinedTableSubclassFacade = 
				facadeFactory.createJoinedTableSubclass(rootClassFacade);
		Object joinedTableSubclassWrapper = ((IFacade)joinedTableSubclassFacade).getTarget();
		assertNotNull(joinedTableSubclassWrapper);
		assertTrue(joinedTableSubclassWrapper instanceof PersistentClassWrapper);
		Object joinedTableSubclassTarget = ((PersistentClassWrapper)joinedTableSubclassWrapper).getWrappedObject();
		assertTrue(joinedTableSubclassTarget instanceof JoinedSubclass);
		assertSame(
				((JoinedSubclass)joinedTableSubclassTarget).getRootClass(), 
				((PersistentClassWrapper)rootClassTarget).getWrappedObject());
	}
	
	@Test
	public void testCreateSpecialRootClass() {
		IProperty propertyFacade = facadeFactory.createProperty();
		IPersistentClass specialRootClassFacade = facadeFactory.createSpecialRootClass(propertyFacade);
		Object specialRootClassWrapper = ((IFacade)specialRootClassFacade).getTarget();
		assertNotNull(specialRootClassWrapper);
		assertTrue(specialRootClassWrapper instanceof PersistentClassWrapper);
		Object specialRootClassTarget = ((PersistentClassWrapper)specialRootClassWrapper).getWrappedObject();
		assertTrue(specialRootClassTarget instanceof SpecialRootClass);
		assertSame(
				((Wrapper)((SpecialRootClass)specialRootClassTarget).getProperty()).getWrappedObject(), 
				((Wrapper)((IFacade)propertyFacade).getTarget()).getWrappedObject());
	}
	
	@Test
	public void testCreateProperty() {
		IProperty propertyFacade = facadeFactory.createProperty();
		assertNotNull(propertyFacade);
		Object propertyWrapper = ((IFacade)propertyFacade).getTarget();
		assertNotNull(propertyWrapper);
		assertTrue(propertyWrapper instanceof Wrapper);
		Object propertyTarget = ((Wrapper)propertyWrapper).getWrappedObject();
		assertNotNull(propertyTarget);
		assertTrue(propertyTarget instanceof Property);
	}
	
	@Test
	public void testCreateHQLCompletionProposal() {
		HQLCompletionProposal hqlCompletionProposalTarget = new HQLCompletionProposal(0, 0);
		IHQLCompletionProposal hqlCompletionProposalFacade = 
				facadeFactory.createHQLCompletionProposal(hqlCompletionProposalTarget);
		assertNotNull(hqlCompletionProposalFacade);
		Object hqlCompletionProposalWrapper = ((IFacade)hqlCompletionProposalFacade).getTarget();
		assertTrue(hqlCompletionProposalWrapper instanceof Wrapper);
		assertSame(((Wrapper)hqlCompletionProposalWrapper).getWrappedObject(), hqlCompletionProposalTarget);
	}
	
	@Test
	public void testCreateArray() {
		IPersistentClass rootClassFacade = (IPersistentClass)GenericFacadeFactory.createFacade(
				IPersistentClass.class, 
				WrapperFactory.createRootClassWrapper());
		PersistentClass rootClass = (PersistentClass)((Wrapper)((IFacade)rootClassFacade).getTarget()).getWrappedObject();
		IValue arrayFacade = 
				facadeFactory.createArray(rootClassFacade);
		Object arrayWrapper = ((IFacade)arrayFacade).getTarget();
		assertNotNull(arrayWrapper);
		assertTrue(arrayWrapper instanceof Wrapper);
		Object wrappedArray = ((Wrapper)arrayWrapper).getWrappedObject();
		assertNotNull(wrappedArray);
		assertTrue(wrappedArray instanceof Array);
		assertSame(rootClass, ((Array)wrappedArray).getOwner());
	}
	
	@Test
	public void testCreateBag() {
		IPersistentClass rootClassFacade = (IPersistentClass)GenericFacadeFactory.createFacade(
				IPersistentClass.class, 
				WrapperFactory.createRootClassWrapper());
		PersistentClass rootClass = (PersistentClass)((Wrapper)((IFacade)rootClassFacade).getTarget()).getWrappedObject();
		IValue bagFacade = 
				facadeFactory.createBag(rootClassFacade);
		Object bagWrapper = ((IFacade)bagFacade).getTarget();
		assertNotNull(bagWrapper);
		assertTrue(bagWrapper instanceof Wrapper);
		Object wrappedBag = ((Wrapper)bagWrapper).getWrappedObject();
		assertTrue(wrappedBag instanceof Bag);
		assertSame(rootClass, ((Bag)wrappedBag).getOwner());
	}
	
	@Test
	public void testCreateList() {
		IPersistentClass rootClassFacade = (IPersistentClass)GenericFacadeFactory.createFacade(
				IPersistentClass.class, 
				WrapperFactory.createRootClassWrapper());
		PersistentClass rootClass = (PersistentClass)((Wrapper)((IFacade)rootClassFacade).getTarget()).getWrappedObject();
		IValue listFacade = 
				facadeFactory.createList(rootClassFacade);
		Object listWrapper = ((IFacade)listFacade).getTarget();
		assertNotNull(listWrapper);
		assertTrue(listWrapper instanceof Wrapper);
		Object wrappedList = ((Wrapper)listWrapper).getWrappedObject();
		assertTrue(wrappedList instanceof List);
		assertSame(rootClass, ((List)wrappedList).getOwner());
	}
	
	@Test
	public void testCreateTable() {
		ITable tableFacade = facadeFactory.createTable("foo");
		assertNotNull(tableFacade);
		Object tableWrapper = ((IFacade)tableFacade).getTarget();
		assertNotNull(tableWrapper);
		assertTrue(tableWrapper instanceof Table);
		Table tableTarget = (Table)tableWrapper;
		assertEquals("foo", tableTarget.getName());
		assertSame(((Wrapper)tableTarget).getWrappedObject(), tableTarget.getPrimaryKey().getTable());
	}
	
	@Test
	public void testCreateManyToOne() {
		ITable tableFacade = facadeFactory.createTable("foo");
		Object tableTarget = ((IFacade)tableFacade).getTarget();
		IValue manyToOneFacade = facadeFactory.createManyToOne(tableFacade);
		assertNotNull(manyToOneFacade);
		Object manyToOneWrapper = ((IFacade)manyToOneFacade).getTarget();
		assertNotNull(manyToOneWrapper);
		assertTrue(manyToOneWrapper instanceof Wrapper);
		Object wrappedManyToOne = ((Wrapper)manyToOneWrapper).getWrappedObject();
		assertTrue(wrappedManyToOne instanceof ManyToOne);
		assertSame(((ManyToOne)wrappedManyToOne).getTable(), tableTarget);
	}
	
	@Test
	public void testCreateMap() {
		IPersistentClass rootClassFacade = (IPersistentClass)GenericFacadeFactory.createFacade(
				IPersistentClass.class, 
				WrapperFactory.createRootClassWrapper());
		PersistentClass rootClass = (PersistentClass)((Wrapper)((IFacade)rootClassFacade).getTarget()).getWrappedObject();
		IValue mapFacade = 
				facadeFactory.createMap(rootClassFacade);
		Object mapWrapper = ((IFacade)mapFacade).getTarget();
		assertNotNull(mapWrapper);
		assertTrue(mapWrapper instanceof Wrapper);
		Object wrappedMap = ((Wrapper)mapWrapper).getWrappedObject();
		assertTrue(wrappedMap instanceof Map);
		assertSame(rootClass, ((Map)wrappedMap).getOwner());
	}
	
	@Test
	public void testCreateOneToMany() {
		IPersistentClass rootClassFacade = (IPersistentClass)GenericFacadeFactory.createFacade(
				IPersistentClass.class, 
				WrapperFactory.createRootClassWrapper());
		PersistentClass rootClass = (PersistentClass)((Wrapper)((IFacade)rootClassFacade).getTarget()).getWrappedObject();
		Table table = new Table("", "foo");
		((RootClass)rootClass).setTable(table);
		IValue oneToManyFacade = 
				facadeFactory.createOneToMany(rootClassFacade);
		Object oneToManyWrapper = ((IFacade)oneToManyFacade).getTarget();
		assertNotNull(oneToManyWrapper);
		assertTrue(oneToManyWrapper instanceof Wrapper);
		Object wrappedOneToMany = ((Wrapper)oneToManyWrapper).getWrappedObject();
		assertTrue(wrappedOneToMany instanceof OneToMany);
		assertSame(table, ((OneToMany)wrappedOneToMany).getTable());
	}
	
	@Test
	public void testCreateOneToOne() {
		IPersistentClass rootClassFacade = (IPersistentClass)GenericFacadeFactory.createFacade(
				IPersistentClass.class, 
				WrapperFactory.createRootClassWrapper());
		PersistentClass rootClass = (PersistentClass)((Wrapper)((IFacade)rootClassFacade).getTarget()).getWrappedObject();
		Table table = new Table("", "foo");
		((RootClass)rootClass).setTable(table);
		rootClass.setEntityName("bar");
		IValue oneToOneFacade = 
				facadeFactory.createOneToOne(rootClassFacade);
		Object oneToOneWrapper = ((IFacade)oneToOneFacade).getTarget();
		assertNotNull(oneToOneWrapper);
		assertTrue(oneToOneWrapper instanceof Wrapper);
		Object wrappedOneToOne = ((Wrapper)oneToOneWrapper).getWrappedObject();
		assertTrue(wrappedOneToOne instanceof OneToOne);
		assertSame("bar", ((OneToOne)wrappedOneToOne).getEntityName());
		assertSame(table, ((OneToOne)wrappedOneToOne).getTable());
	}
	
	@Test
	public void testCreatePrimitiveArray() {
		IPersistentClass rootClassFacade = (IPersistentClass)GenericFacadeFactory.createFacade(
				IPersistentClass.class, 
				WrapperFactory.createRootClassWrapper());
		PersistentClass rootClass = (PersistentClass)((Wrapper)((IFacade)rootClassFacade).getTarget()).getWrappedObject();
		IValue primitiveArrayFacade = 
				facadeFactory.createPrimitiveArray(rootClassFacade);
		Object primitiveArrayWrapper = ((IFacade)primitiveArrayFacade).getTarget();
		assertNotNull(primitiveArrayWrapper);
		assertTrue(primitiveArrayWrapper instanceof Wrapper);
		Object wrappedPrimitiveArray = ((Wrapper)primitiveArrayWrapper).getWrappedObject();
		assertTrue(wrappedPrimitiveArray instanceof PrimitiveArray);
		assertSame(rootClass, ((PrimitiveArray)wrappedPrimitiveArray).getOwner());
	}
	
	@Test
	public void testCreateSet() {
		IPersistentClass rootClassFacade = (IPersistentClass)GenericFacadeFactory.createFacade(
				IPersistentClass.class, 
				WrapperFactory.createRootClassWrapper());
		PersistentClass rootClass = (PersistentClass)((Wrapper)((IFacade)rootClassFacade).getTarget()).getWrappedObject();
		IValue setFacade = 
				facadeFactory.createSet(rootClassFacade);
		Object setWrapper = ((IFacade)setFacade).getTarget();
		assertNotNull(setWrapper);
		assertTrue(setWrapper instanceof Wrapper);
		Object wrappedSet = ((Wrapper)setWrapper).getWrappedObject();
		assertTrue(wrappedSet instanceof Set);
		assertSame(rootClass, ((Set)wrappedSet).getOwner());
	}
	
	@Test
	public void testCreateSimpleValue() {
		IValue simpleValueFacade = facadeFactory.createSimpleValue();
		Object simpleValueWrapper = ((IFacade)simpleValueFacade).getTarget();
		assertNotNull(simpleValueWrapper);
		assertTrue(simpleValueWrapper instanceof Wrapper);
		Object wrappedSimpleValue = ((Wrapper)simpleValueWrapper).getWrappedObject();
		assertTrue(wrappedSimpleValue instanceof SimpleValue);
	}
	
	@Test
	public void testCreateComponentValue() {
		IPersistentClass rootClassFacade = (IPersistentClass)GenericFacadeFactory.createFacade(
				IPersistentClass.class, 
				WrapperFactory.createRootClassWrapper());
		PersistentClass rootClass = (PersistentClass)((Wrapper)((IFacade)rootClassFacade).getTarget()).getWrappedObject();
		IValue componentFacade = facadeFactory.createComponent(rootClassFacade);
		Object componentWrapper = ((IFacade)componentFacade).getTarget();
		assertNotNull(componentWrapper);
		assertTrue(componentWrapper instanceof Wrapper);
		Object wrappedComponent = ((Wrapper)componentWrapper).getWrappedObject();
		assertTrue(wrappedComponent instanceof Component);
		assertSame(rootClass, ((Component)wrappedComponent).getOwner());
	}
	
	@Test
	public void testCreateTableFilter() {
		ITableFilter tableFilterFacade = facadeFactory.createTableFilter();
		Object tableFilterWrapper = ((IFacade)tableFilterFacade).getTarget();
		assertTrue(tableFilterWrapper instanceof TableFilter);
	}
	
	@Test
	public void testCreateTypeFactory() {
		ITypeFactory typeFactoryFacade = facadeFactory.createTypeFactory();
		assertSame(TypeFactoryWrapper.INSTANCE, ((IFacade)typeFactoryFacade).getTarget());
	}
	
	@Test
	public void testCreateEnvironment() {
		IEnvironment environmentFacade = facadeFactory.createEnvironment();
		assertNotNull(environmentFacade);
		Object environmentWrapper = ((IFacade)environmentFacade).getTarget();
		assertTrue(environmentWrapper instanceof EnvironmentWrapper);
	}
	
	@Test
	public void testCreateSchemaExport() {
		IConfiguration configurationFacade = (IConfiguration)GenericFacadeFactory.createFacade(
				IConfiguration.class, 
				WrapperFactory.createNativeConfigurationWrapper());
		ISchemaExport schemaExportFacade = facadeFactory.createSchemaExport(configurationFacade);
		Object schemaExportWrapper = ((IFacade)schemaExportFacade).getTarget();
		assertTrue(schemaExportWrapper instanceof SchemaExportWrapper);
	}
	
	@Test
	public void testCreateHibernateMappingExporter() {
		File file = new File("foo");
		IConfiguration configurationFacade = (IConfiguration)GenericFacadeFactory.createFacade(
				IConfiguration.class, 
				WrapperFactory.createNativeConfigurationWrapper());
		IHibernateMappingExporter hibernateMappingExporterFacade = 
				facadeFactory.createHibernateMappingExporter(configurationFacade, file);
		Object hibernateMappingExporterWrapper = ((IFacade)hibernateMappingExporterFacade).getTarget();
		assertTrue(hibernateMappingExporterWrapper instanceof HbmExporterWrapper);
		assertSame(
				((HbmExporterWrapper)hibernateMappingExporterWrapper)
					.getProperties().get(ExporterConstants.OUTPUT_FILE_NAME),
				file);
	}
	
	@Test
	public void testCreateExporter() {
		IExporter exporterFacade = facadeFactory.createExporter(GenericExporter.class.getName());
		assertNotNull(exporterFacade);
		Object exporterWrapper = ((IFacade)exporterFacade).getTarget();
		assertNotNull(exporterWrapper);
		Exporter wrappedExporter = (Exporter)((Wrapper)exporterWrapper).getWrappedObject();
		assertNotNull(wrappedExporter);
		assertTrue(wrappedExporter instanceof GenericExporter);
	}
	
	@Test
	public void testCreateHqlCodeAssist() {
		IConfiguration configuration = (IConfiguration)GenericFacadeFactory.createFacade(
				IConfiguration.class, 
				WrapperFactory.createNativeConfigurationWrapper());
		configuration.setProperty("hibernate.connection.url", "jdbc:h2:mem:test");
		IHQLCodeAssist hqlCodeAssistFacade = facadeFactory.createHQLCodeAssist(configuration);
		assertNotNull(hqlCodeAssistFacade);
		Object hqlCodeAssistWrapper = ((IFacade)hqlCodeAssistFacade).getTarget();
		assertNotNull(hqlCodeAssistWrapper);
		assertTrue(hqlCodeAssistWrapper instanceof HqlCodeAssistWrapper);
	}
	
	public static class TestRevengStrategy extends DelegatingStrategy {
		public TestRevengStrategy(RevengStrategy delegate) {
			super(delegate);
		}
	}
	
}
