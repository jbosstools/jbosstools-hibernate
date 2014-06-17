package org.jboss.tools.hibernate.spi;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.hibernate.mapping.Table;

public interface IDatabaseCollector {

	Iterator<Entry<String, List<Table>>> getQualifierEntries();

}
