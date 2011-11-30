/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.hibernate.console.ext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Dmitry Geraskov
 *
 */
public class QueryResultImpl implements QueryResult {
	
	private List<Object> list = new ArrayList<Object>();;
	
	private List<String> pathNames = Collections.emptyList();
	
	private long execTime;
	
	private List<Throwable> exceptions = new ArrayList<Throwable>();
	
	public QueryResultImpl(List<Object> list, long execTime){
		this.list = list;
		this.execTime  = execTime;
	}
	
	public QueryResultImpl(List<Object> list, List<String> pathNames, long execTime){
		this(list, execTime);
		this.pathNames = pathNames;
	}
	
	public QueryResultImpl(Throwable e){
		exceptions.add(e);
	}

	@Override
	public List<Object> list() {
		return list;
	}

	@Override
	public List<String> getPathNames() {
		return pathNames;
	}
	
	@Override
	public void setPathNames(List<String> pathNames) {
		this.pathNames = pathNames;
	}

	public long getExecTime() {
		return execTime;
	}

	@Override
	public long getQueryTime() {
		return execTime;
	}
	
	public void addException(Throwable e){
		exceptions.add(e);
	}
	
	public List<Throwable> getExceptions(){
		return this.exceptions;
	}

	@Override
	public boolean hasExceptions() {
		return getExceptions().size() > 0;
	}

}
