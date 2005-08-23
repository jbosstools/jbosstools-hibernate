/*
 * Created on 2004-10-30 by max
 * 
 */
package org.hibernate.console.views;

import org.hibernate.console.QueryPage;

/**
 * @author max
 *
 */
public interface QueryListener {
	public void queryPageCreated(QueryPage qp);
}
