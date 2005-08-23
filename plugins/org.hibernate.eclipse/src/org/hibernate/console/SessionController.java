/*
 * Created on 13-08-2004
 *
 */
package org.hibernate.console;

import org.hibernate.Session;

/**
 * @author MAX
 *
 */
public interface SessionController {

	void selectObject(Session session, Object o);

	void executeHQLQuery(String text);

	void executeJavaQuery(String text);

}
