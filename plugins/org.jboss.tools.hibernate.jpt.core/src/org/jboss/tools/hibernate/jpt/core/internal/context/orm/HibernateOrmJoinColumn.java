/*******************************************************************************
 * Copyright (c) 2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.hibernate.jpt.core.internal.context.orm;

import org.eclipse.jpt.jpa.core.context.orm.OrmSpecifiedJoinColumn;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateJoinColumn;

/**
 * Combines 2 interfaces.
 * @author Dmitry Geraskov
 *
 */
public interface HibernateOrmJoinColumn extends HibernateJoinColumn,
		OrmSpecifiedJoinColumn {

}
