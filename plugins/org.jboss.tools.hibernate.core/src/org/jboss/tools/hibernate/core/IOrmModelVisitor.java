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
package org.jboss.tools.hibernate.core;

/**
 * @author alex
 *
 * OrmProject visitor interface
 */
public interface IOrmModelVisitor {
    Object visitOrmProject(IOrmProject project, Object argument);
    Object visitDatabaseSchema(IDatabaseSchema schema, Object argument);
    Object visitDatabaseTable(IDatabaseTable table, Object argument);
    Object visitDatabaseColumn(IDatabaseColumn column, Object argument);
    Object visitDatabaseConstraint(IDatabaseConstraint constraint, Object argument);
    Object visitPackage(IPackage pakage, Object argument);
    Object visitMapping(IMapping mapping, Object argument);
    Object visitMappingStorage(IMappingStorage storage, Object argument);
    Object visitPersistentClass(IPersistentClass clazz, Object argument);
    Object visitPersistentField(IPersistentField field, Object argument);
    Object visitPersistentClassMapping(IPersistentClassMapping mapping, Object argument);
    Object visitPersistentFieldMapping(IPersistentFieldMapping mapping, Object argument);
    Object visitPersistentValueMapping(IPersistentValueMapping mapping, Object argument);
    // add tau 27.07.2005
    Object visitNamedQueryMapping(INamedQueryMapping mapping, Object argument);    

}
