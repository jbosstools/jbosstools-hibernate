package org.hibernate.eclipse.console.views;

import java.util.List;

import org.hibernate.console.ConsoleQueryParameter;
import org.hibernate.eclipse.console.workbench.TableModelList;

public class QueryParametersModelList extends TableModelList {

	public QueryParametersModelList(List queryParameterList) {
		super(queryParameterList);
	}

	public void addParameter(ConsoleQueryParameter cqp) {
		add(cqp);
	}
	
	public void removeParameter(ConsoleQueryParameter cqp) {
		remove(cqp);
	}
	
	
}
