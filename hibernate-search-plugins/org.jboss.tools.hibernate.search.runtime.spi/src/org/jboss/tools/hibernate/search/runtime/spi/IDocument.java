package org.jboss.tools.hibernate.search.runtime.spi;

import java.util.List;

public interface IDocument {
	List<IField> getFields();
}
