package org.jboss.tools.hibernate.spi.internal;

import java.io.File;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.jboss.tools.hibernate.spi.IArtifactCollector;
import org.jboss.tools.hibernate.spi.ICfg2HbmTool;
import org.jboss.tools.hibernate.spi.IColumn;
import org.jboss.tools.hibernate.spi.IConfiguration;
import org.jboss.tools.hibernate.spi.IDatabaseCollector;
import org.jboss.tools.hibernate.spi.IDialect;
import org.jboss.tools.hibernate.spi.IEnvironment;
import org.jboss.tools.hibernate.spi.IExporter;
import org.jboss.tools.hibernate.spi.IHQLCodeAssist;
import org.jboss.tools.hibernate.spi.IHQLQueryPlan;
import org.jboss.tools.hibernate.spi.IHibernateMappingExporter;
import org.jboss.tools.hibernate.spi.IHibernateMappingGlobalSettings;
import org.jboss.tools.hibernate.spi.IJDBCReader;
import org.jboss.tools.hibernate.spi.IMetaDataDialect;
import org.jboss.tools.hibernate.spi.INamingStrategy;
import org.jboss.tools.hibernate.spi.IOverrideRepository;
import org.jboss.tools.hibernate.spi.IPersistentClass;
import org.jboss.tools.hibernate.spi.IPrimaryKey;
import org.jboss.tools.hibernate.spi.IProperty;
import org.jboss.tools.hibernate.spi.IReverseEngineeringSettings;
import org.jboss.tools.hibernate.spi.IReverseEngineeringStrategy;
import org.jboss.tools.hibernate.spi.ISchemaExport;
import org.jboss.tools.hibernate.spi.IService;
import org.jboss.tools.hibernate.spi.ISessionFactory;
import org.jboss.tools.hibernate.spi.ISettings;
import org.jboss.tools.hibernate.spi.ITable;
import org.jboss.tools.hibernate.spi.ITableFilter;
import org.jboss.tools.hibernate.spi.ITableIdentifier;
import org.jboss.tools.hibernate.spi.ITypeFactory;
import org.jboss.tools.hibernate.spi.IValue;
import org.xml.sax.EntityResolver;

public class TestService implements IService {

	@Override
	public IConfiguration newAnnotationConfiguration() {
		return null;
	}

	@Override
	public IConfiguration newJpaConfiguration(String entityResolver,
			String persistenceUnit, Map<Object, Object> overrides) {
		return null;
	}

	@Override
	public IConfiguration newDefaultConfiguration() {
		return null;
	}

	@Override
	public void setExporterConfiguration(IExporter exporter, IConfiguration hcfg) {
	}

	@Override
	public IHibernateMappingExporter newHibernateMappingExporter(
			IConfiguration hcfg, File file) {
		return null;
	}

	@Override
	public ISchemaExport newSchemaExport(IConfiguration hcfg) {
		return null;
	}

	@Override
	public IHQLCodeAssist newHQLCodeAssist(IConfiguration hcfg) {
		return null;
	}

	@Override
	public IConfiguration newJDBCMetaDataConfiguration() {
		return null;
	}

	@Override
	public IExporter createExporter(String exporterClassName) {
		return null;
	}

	@Override
	public IArtifactCollector newArtifactCollector() {
		return null;
	}

	@Override
	public IHQLQueryPlan newHQLQueryPlan(String query, boolean shallow,
			ISessionFactory sessionFactory) {
		return null;
	}

	@Override
	public ITypeFactory newTypeFactory() {
		return null;
	}

	@Override
	public INamingStrategy newNamingStrategy(String strategyClassName) {
		return null;
	}

	@Override
	public IOverrideRepository newOverrideRepository() {
		return null;
	}

	@Override
	public ITableFilter newTableFilter() {
		return null;
	}

	@Override
	public IReverseEngineeringSettings newReverseEngineeringSettings(
			IReverseEngineeringStrategy res) {
		return null;
	}

	@Override
	public IReverseEngineeringStrategy newDefaultReverseEngineeringStrategy() {
		return null;
	}

	@Override
	public IJDBCReader newJDBCReader(Properties properties, ISettings settings,
			IReverseEngineeringStrategy strategy) {
		return null;
	}

	@Override
	public IReverseEngineeringStrategy newReverseEngineeringStrategy(
			String strategyName, IReverseEngineeringStrategy delegate) {
		return null;
	}

	@Override
	public String getReverseEngineeringStrategyClassName() {
		return null;
	}

	@Override
	public IDatabaseCollector newDatabaseCollector(
			IMetaDataDialect metaDataDialect) {
		return null;
	}

	@Override
	public ICfg2HbmTool newCfg2HbmTool() {
		return null;
	}

	@Override
	public IProperty newProperty() {
		return null;
	}

	@Override
	public ITable newTable(String name) {
		return null;
	}

	@Override
	public IColumn newColumn(String string) {
		return null;
	}

	@Override
	public IDialect newDialect(Properties properties, Connection connection) {
		return null;
	}

	@Override
	public Class<?> getDriverManagerConnectionProviderClass() {
		return null;
	}

	@Override
	public IEnvironment getEnvironment() {
		return null;
	}

	@Override
	public IValue newSimpleValue() {
		return null;
	}

	@Override
	public IValue newPrimitiveArray(IPersistentClass persistentClass) {
		return null;
	}

	@Override
	public IValue newArray(IPersistentClass persistentClass) {
		return null;
	}

	@Override
	public IValue newBag(IPersistentClass persistentClass) {
		return null;
	}

	@Override
	public IValue newList(IPersistentClass persistentClass) {
		return null;
	}

	@Override
	public IValue newMap(IPersistentClass persistentClass) {
		return null;
	}

	@Override
	public IValue newSet(IPersistentClass persistentClass) {
		return null;
	}

	@Override
	public IValue newManyToOne(ITable table) {
		return null;
	}

	@Override
	public IValue newOneToMany(IPersistentClass persistentClass) {
		return null;
	}

	@Override
	public IValue newOneToOne(IPersistentClass persistentClass) {
		return null;
	}

	@Override
	public IPersistentClass newSingleTableSubclass(
			IPersistentClass persistentClass) {
		return null;
	}

	@Override
	public IPersistentClass newJoinedSubclass(IPersistentClass persistentClass) {
		return null;
	}

	@Override
	public IPersistentClass newSpecialRootClass(IProperty ormElement) {
		return null;
	}

	@Override
	public IPersistentClass newRootClass() {
		return null;
	}

	@Override
	public IPrimaryKey newPrimaryKey() {
		return null;
	}

	@Override
	public IHibernateMappingGlobalSettings newHibernateMappingGlobalSettings() {
		return null;
	}

	@Override
	public ITableIdentifier createTableIdentifier(ITable table) {
		return null;
	}

	@Override
	public ITableIdentifier newTableIdentifier(String catalog, String schema,
			String typename) {
		return null;
	}

	@Override
	public boolean isInitialized(Object object) {
		return false;
	}

	@Override
	public List<String> getJPAMappingFilePaths(String persistenceUnitName,
			EntityResolver entityResolver) {
		return null;
	}

	@Override
	public Class<?> getClassWithoutInitializingProxy(Object reflectedObject) {
		return null;
	}

	@Override
	public ClassLoader getClassLoader() {
		return null;
	}

}
