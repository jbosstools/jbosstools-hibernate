package org.hibernate.eclipse.console.model.impl;

import org.hibernate.cfg.reveng.TableFilter;
import org.hibernate.eclipse.console.model.ITableFilter;

public class TableFilterImpl implements ITableFilter {

	TableFilter tf = new TableFilter();
	
	public void setExclude(Boolean exclude) {
		tf.setExclude(exclude);
	}

	public void setMatchCatalog(String catalog) {
		tf.setMatchCatalog(catalog);
	}

	public void setMatchSchema(String schema) {
		tf.setMatchSchema(schema);
	}

	public void setMatchName(String name) {
		tf.setMatchName(name);
	}

	public Boolean getExclude() {
		return tf.getExclude();
	}

	public String getMatchCatalog() {
		return tf.getMatchCatalog();
	}

	public String getMatchSchema() {
		return tf.getMatchSchema();
	}

	public String getMatchName() {
		return tf.getMatchName();
	}

}
