package org.hibernate.eclipse.console.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchRequestor;

public class CollectingSearchRequestor extends SearchRequestor {
	private List found;

	public CollectingSearchRequestor() {
		found= new ArrayList();
	}
	
	public void acceptSearchMatch(SearchMatch match) {
		found.add(match);
	}

	public List getResults() {
		return found;
	}
}


