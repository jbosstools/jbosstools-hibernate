package org.jboss.tools.hibernate.spi;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public interface IDatabaseCollector {

	Iterator<Entry<String, List<ITable>>> getQualifierEntries();

}
