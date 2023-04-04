package org.jboss.tools.hibernate.orm.runtime.exp.internal.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.Properties;

import org.hibernate.cfg.DefaultNamingStrategy;
import org.hibernate.cfg.NamingStrategy;
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
import org.hibernate.mapping.SingleTableSubclass;
import org.hibernate.mapping.Table;
import org.hibernate.tool.api.reveng.RevengSettings;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.ide.completion.HQLCompletionProposal;
import org.hibernate.tool.internal.export.common.DefaultArtifactCollector;
import org.hibernate.tool.internal.export.hbm.Cfg2HbmTool;
import org.hibernate.tool.internal.reveng.strategy.DefaultStrategy;
import org.hibernate.tool.internal.reveng.strategy.DelegatingStrategy;
import org.hibernate.tool.internal.reveng.strategy.OverrideRepository;
import org.hibernate.tool.internal.reveng.strategy.TableFilter;
import org.hibernate.tool.orm.jbt.util.JpaConfiguration;
import org.hibernate.tool.orm.jbt.util.NativeConfiguration;
import org.hibernate.tool.orm.jbt.util.RevengConfiguration;
import org.hibernate.tool.orm.jbt.util.SpecialRootClass;
import org.hibernate.tool.orm.jbt.wrp.ColumnWrapper;
import org.hibernate.tool.orm.jbt.wrp.PersistentClassWrapper;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.IDatabaseReader;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;
import org.jboss.tools.hibernate.runtime.spi.ICfg2HbmTool;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IHQLCompletionProposal;
import org.jboss.tools.hibernate.runtime.spi.INamingStrategy;
import org.jboss.tools.hibernate.runtime.spi.IOverrideRepository;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringSettings;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.jboss.tools.hibernate.runtime.spi.ITableFilter;
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
	public void testCreateArtifactCollector() {
		IArtifactCollector facade = facadeFactory.createArtifactCollector();
		Object target = ((IFacade)facade).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof DefaultArtifactCollector);
	}
	
	@Test
	public void testCreateCfg2HbmTool() {
		ICfg2HbmTool facade = facadeFactory.createCfg2HbmTool();
		Object target = ((IFacade)facade).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof Cfg2HbmTool);
	}
	
	@Test
	public void testCreateNamingStrategy() {
		INamingStrategy facade = facadeFactory.createNamingStrategy(DefaultNamingStrategy.class.getName());
		Object target = ((IFacade)facade).getTarget();
		assertNotNull(target);
		assertTrue(NamingStrategy.class.isAssignableFrom(target.getClass()));
	}
	
	@Test
	public void testCreateOverrideRepository() {
		IOverrideRepository facade = facadeFactory.createOverrideRepository();
		Object target = ((IFacade)facade).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof OverrideRepository);
	}
	
	@Test
	public void testCreateRevengStrategy() throws Exception {
		IReverseEngineeringStrategy facade = facadeFactory.createReverseEngineeringStrategy();
		Object firstTarget = ((IFacade)facade).getTarget();
		assertNotNull(firstTarget);
		assertTrue(firstTarget instanceof DefaultStrategy);
		facade = facadeFactory.createReverseEngineeringStrategy(TestRevengStrategy.class.getName(), firstTarget);
		Object secondTarget = ((IFacade)facade).getTarget();
		assertNotNull(secondTarget);
		assertTrue(secondTarget instanceof DelegatingStrategy);
		Field delegateField = DelegatingStrategy.class.getDeclaredField("delegate");
		delegateField.setAccessible(true);
		assertSame(delegateField.get(secondTarget), firstTarget);
		facade = facadeFactory.createReverseEngineeringStrategy(DefaultStrategy.class.getName(), secondTarget);
		Object thirdTarget = ((IFacade)facade).getTarget();
		assertNotNull(thirdTarget);
		assertTrue(thirdTarget instanceof DefaultStrategy);
	}
	
	@Test 
	public void testCreateRevengSettings() {
		IReverseEngineeringStrategy strategyFacade = facadeFactory.createReverseEngineeringStrategy();
		Object strategyTarget = ((IFacade)strategyFacade).getTarget();
		IReverseEngineeringSettings settingsFacade = facadeFactory.createReverseEngineeringSettings(strategyTarget);
		assertNotNull(settingsFacade);
		Object settingsTarget = ((IFacade)settingsFacade).getTarget();
		assertNotNull(settingsTarget);
		assertTrue(settingsTarget instanceof RevengSettings);
		assertSame(strategyTarget, ((RevengSettings)settingsTarget).getRootStrategy());
	}
	
	@Test
	public void testCreateNativeConfiguration() {
		IConfiguration nativeConfigurationFacade = facadeFactory.createNativeConfiguration();
		assertNotNull(nativeConfigurationFacade);
		Object nativeConfigurationTarget = ((IFacade)nativeConfigurationFacade).getTarget();
		assertNotNull(nativeConfigurationTarget);
		assertTrue(nativeConfigurationTarget instanceof NativeConfiguration);
	}
	
	@Test
	public void testCreateRevengConfiguration() {
		IConfiguration revengConfigurationFacade = facadeFactory.createRevengConfiguration();
		assertNotNull(revengConfigurationFacade);
		Object revengConfigurationTarget = ((IFacade)revengConfigurationFacade).getTarget();
		assertNotNull(revengConfigurationTarget);
		assertTrue(revengConfigurationTarget instanceof RevengConfiguration);
	}
	
	@Test
	public void testCreateJpaConfiguration() {
		IConfiguration jpaConfigurationFacade = facadeFactory.createJpaConfiguration(null, null);
		assertNotNull(jpaConfigurationFacade);
		Object jpaConfigurationTarget = ((IFacade)jpaConfigurationFacade).getTarget();
		assertNotNull(jpaConfigurationTarget);
		assertTrue(jpaConfigurationTarget instanceof JpaConfiguration);
	}
	
	@Test
	public void testCreateColumn() {
		IColumn columnFacade = facadeFactory.createColumn(null);
		assertNotNull(columnFacade);
		Object columnTarget = ((IFacade)columnFacade).getTarget();
		assertNotNull(columnTarget);
		assertTrue(columnTarget instanceof ColumnWrapper);
	}
	
	@Test
	public void testCreateRootClass() {
		IPersistentClass rootClassFacade = facadeFactory.createRootClass();
		assertNotNull(rootClassFacade);
		Object rootClassWrapper = ((IFacade)rootClassFacade).getTarget();
		assertNotNull(rootClassWrapper);
		assertTrue(rootClassWrapper instanceof PersistentClassWrapper);
		assertTrue(((PersistentClassWrapper)rootClassWrapper).getWrappedObject() instanceof RootClass);
	}
	
	@Test
	public void testCreateSingleTableSubclass() {
		IPersistentClass rootClassFacade = facadeFactory.createRootClass();
		Object rootClassTarget = ((IFacade)rootClassFacade).getTarget();
		IPersistentClass singleTableSubclassFacade = 
				facadeFactory.createSingleTableSubclass(rootClassFacade);
		Object singleTableSubclassWrapper = ((IFacade)singleTableSubclassFacade).getTarget();
		assertNotNull(singleTableSubclassWrapper);
		assertTrue(singleTableSubclassWrapper instanceof PersistentClassWrapper);
		Object singleTableSubclassTarget = ((PersistentClassWrapper)singleTableSubclassWrapper).getWrappedObject();
		assertTrue(singleTableSubclassTarget instanceof SingleTableSubclass);
		assertSame(
				((SingleTableSubclass)singleTableSubclassTarget).getRootClass(), 
				((PersistentClassWrapper)rootClassTarget).getWrappedObject());
	}
	
	@Test
	public void testCreateJoinedTableSubclass() {
		IPersistentClass rootClassFacade = facadeFactory.createRootClass();
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
				((SpecialRootClass)specialRootClassTarget).getProperty(), 
				((IFacade)propertyFacade).getTarget());
	}
	
	@Test
	public void testCreateProperty() {
		IProperty propertyFacade = facadeFactory.createProperty();
		assertNotNull(propertyFacade);
		Object propertyTarget = ((IFacade)propertyFacade).getTarget();
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
		IPersistentClass rootClassFacade = facadeFactory.createRootClass();
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
		IPersistentClass rootClassFacade = facadeFactory.createRootClass();
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
		IPersistentClass rootClassFacade = facadeFactory.createRootClass();
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
	public void testCreateDatabaseReader() {
		Properties properties = new Properties();
		properties.put("hibernate.connection.url", "jdbc:h2:mem:test");
		IReverseEngineeringStrategy revengStrategyFacade = 
				facadeFactory.createReverseEngineeringStrategy();
		IDatabaseReader databaseReaderFacade = 
				facadeFactory.createDatabaseReader(properties, revengStrategyFacade);
		assertNotNull(databaseReaderFacade);
		Object databaseReaderWrapper = ((IFacade)databaseReaderFacade).getTarget();
		assertEquals(
				"org.hibernate.tool.orm.jbt.wrp.DatabaseReaderWrapperFactory$DatabaseReaderWrapperImpl",
				databaseReaderWrapper.getClass().getName());
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
		assertSame(tableTarget, tableTarget.getPrimaryKey().getTable());
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
		IPersistentClass rootClassFacade = facadeFactory.createRootClass();
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
		IPersistentClass rootClassFacade = facadeFactory.createRootClass();
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
		IPersistentClass rootClassFacade = facadeFactory.createRootClass();
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
		IPersistentClass rootClassFacade = facadeFactory.createRootClass();
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
		IPersistentClass rootClassFacade = facadeFactory.createRootClass();
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
		IPersistentClass rootClassFacade = facadeFactory.createRootClass();
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
	
	public static class TestRevengStrategy extends DelegatingStrategy {
		public TestRevengStrategy(RevengStrategy delegate) {
			super(delegate);
		}
	}
	
}
