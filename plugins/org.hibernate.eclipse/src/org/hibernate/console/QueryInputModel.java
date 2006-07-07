/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.hibernate.console;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Set;

import org.hibernate.Hibernate;

/**
 * Class for managing misc parameters and other inputs to a Query, Criteria etc.  
 * 
 * Currently only holds parameters.
 * 
 * @author Max Rydahl Andersen
 *
 */
public class QueryInputModel extends Observable {

	List parameters;
	boolean ignoreParameters = false;
	
	public QueryInputModel() {
		parameters = new ArrayList();
	}
	
	public int getParameterCount() {
		return parameters.size();
	}
	
	public ConsoleQueryParameter[] getQueryParameters() {
		return (ConsoleQueryParameter[]) parameters.toArray(new ConsoleQueryParameter[parameters.size()]);
	}
	
	/** return a copy of the parameters currently in this model */
	public ConsoleQueryParameter[] getQueryParametersForQuery() {
		if(ignoreParameters) return new ConsoleQueryParameter[0];
		ConsoleQueryParameter[] result = new ConsoleQueryParameter[parameters.size()];
		
		Iterator iterator = parameters.iterator();
		int i = 0;
		while(iterator.hasNext()) {
			ConsoleQueryParameter cqp=(ConsoleQueryParameter) iterator.next(); 
			result[i++] = new ConsoleQueryParameter(cqp);
		}
		return result;
	}

	public void addParameter(ConsoleQueryParameter cqp) {
		parameters.add(cqp);
		setChanged();
		notifyObservers("addParameter");
	}
	
	public void removeParameter(ConsoleQueryParameter cqp) {
		parameters.remove(cqp);
		setChanged();
		notifyObservers("removeParameter");
	}
	
	public void moveUp(ConsoleQueryParameter cqp) {
		move(cqp, 1, parameters);
	}
	
	public void moveDown(ConsoleQueryParameter cqp) {
		move(cqp, 1, parameters);
	}
	
	protected void move(Object tf, int shift, List list) {
		int i = list.indexOf(tf);
		
		if(i>=0) {
			if(i+shift<list.size() && i+shift>=0) { 
				list.remove(i);
				list.add(i+shift, tf);
			}
		}
		setChanged();
		notifyObservers("move");
	}

	/** create a parameter which does not collide with any other parameter */
	public ConsoleQueryParameter createUniqueParameter(String paramName) {
		if(parameters.isEmpty()) {
			return new ConsoleQueryParameter(paramName, Hibernate.STRING, "");
		} else {
			ConsoleQueryParameter cqp = (ConsoleQueryParameter) parameters.get(parameters.size()-1);
			ConsoleQueryParameter c = new ConsoleQueryParameter(cqp);
			c.setName(makeUnique(parameters.iterator(), paramName));
			return c;
		}
	}
	
	private static String makeUnique(Iterator items, String originalPropertyName) {
        int cnt = 0;
        String propertyName = originalPropertyName;
        Set uniqueNames = new HashSet();
        
        while ( items.hasNext() ) {
            ConsoleQueryParameter element = (ConsoleQueryParameter) items.next();
            uniqueNames.add( element.getName() );
        }
        
        while( uniqueNames.contains(propertyName) ) { 
            cnt++;
            propertyName = originalPropertyName + "-" + cnt;
        }
        
        return propertyName;                                
    }


	public void setIgnoreParameters(boolean ignoreParameters) {
		this.ignoreParameters = ignoreParameters;
		setChanged();
		notifyObservers();
	}

	public boolean ignoreParameters() {
		return ignoreParameters;
	}

	public void clear() {
		parameters.clear();
		setChanged();
		notifyObservers("clear");		
	}

}
