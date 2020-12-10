package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.hibernate.mapping.Table;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.internal.reveng.RevengMetadataCollector;
import org.hibernate.tool.internal.reveng.dialect.AbstractMetaDataDialect;
import org.hibernate.tool.internal.reveng.reader.DatabaseReader;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IDatabaseReader;
import org.jboss.tools.hibernate.runtime.spi.IProgressListener;
import org.jboss.tools.hibernate.runtime.spi.ITable;

public class DatabaseReaderFacadeImpl implements IDatabaseReader, IFacade {

	@Override
	public Map<String, List<ITable>> collectDatabaseTables(IProgressListener progressListener) {
		return null;
	}

	@Override
	public Object getTarget() {
		return this;
	}
	
	
	private Properties properties = null;
	private RevengStrategy revengStrategy = null;
	private AbstractMetaDataDialect mdd = null;
	private ServiceRegistry serviceRegistry = null;
	
	public void readDatabase() {
		RevengMetadataCollector revengMetadataCollector = new RevengMetadataCollector();
		DatabaseReader reader = DatabaseReader.create(properties, revengStrategy, mdd, serviceRegistry);
		reader.readDatabaseSchema(revengMetadataCollector);
		for (Table table : revengMetadataCollector.getTables()) {
			System.out.println(table.getCatalog() + "." + table.getSchema() + "." + table.getName());
		}
	}

}
