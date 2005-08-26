package org.hibernate.console;

public class QueryInputs {

	private static QueryInputs instance;

	public static synchronized QueryInputs getInstance() {
		if (instance == null) {
			instance = new QueryInputs();
		}
		return instance;
	}

	QueryInputModel queryParameterModel;
	
	
	protected QueryInputs() {
		queryParameterModel = new QueryInputModel();
	}

	public QueryInputModel getQueryInputModel() {
		return queryParameterModel;
	}

}
