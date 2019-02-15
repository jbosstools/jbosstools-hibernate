/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.hibernate.xml.model.impl;

import org.jboss.tools.common.model.filesystems.impl.SimpleFileImpl;
import org.jboss.tools.common.model.impl.OrderedByEntityChildren;
import org.jboss.tools.common.model.impl.RegularChildren;

public class FileHibernateImpl extends SimpleFileImpl {
    private static final long serialVersionUID = 4331754550950040902L;

    public FileHibernateImpl() {}

    protected RegularChildren createChildren() {
        return new OrderedByEntityChildren();
    }

}

