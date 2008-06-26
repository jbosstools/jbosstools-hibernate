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
package org.jboss.tools.hibernate.core.hibernate;

import org.jboss.tools.hibernate.core.IOrmModelVisitor;


/**
 * @author alex
 *
 * Visitor interface for all hibernate mappings
 */
public interface IHibernateMappingVisitor extends IOrmModelVisitor {
	//hibernate specific value mappings
	Object visitSimpleValueMapping(ISimpleValueMapping simple, Object argument);
	Object visitAnyMapping(IAnyMapping mapping, Object argument);
	Object visitListMapping(IListMapping column, Object argument);
	Object visitArrayMapping(IArrayMapping kage, Object argument);
	Object visitComponentMapping(IComponentMapping mapping, Object argument);
	Object visitBagMapping(IBagMapping bagMapping, Object argument);
	Object visitIdBagMapping(IIdBagMapping table, Object argument);
	Object visitPrimitiveArrayMapping(IPrimitiveArrayMapping constraint, Object argument);
	Object visitMapMapping(IMapMapping mapping, Object argument);
	Object visitSetMapping(ISetMapping mapping, Object argument);
	Object visitOneToManyMapping(IOneToManyMapping mapping, Object argument);
	//by Nick 22.03.2005
	Object visitManyToManyMapping(IManyToManyMapping mapping, Object argument);
	//by Nick

	//by Nick 28.03.2005
	Object visitManyToAnyMapping(IManyToAnyMapping mapping, Object argument);
	//by Nick
	
	Object visitManyToOneMapping(IManyToOneMapping mapping, Object argument);
	Object visitOneToOneMapping(IOneToOneMapping mapping, Object argument);

	Object visitJoinMapping(IJoinMapping mapping, Object argument);
	
	
	//hibernate specific class mappings
	Object visitRootClassMapping(IRootClassMapping mapping, Object argument);
	Object visitSubclassMapping(ISubclassMapping mapping, Object argument);
	Object visitJoinedSubclassMapping(IJoinedSubclassMapping mapping, Object argument);
	Object visitUnionSubclassMapping(IUnionSubclassMapping mapping, Object argument);
	
	//hibernate specific field mapping
	Object visitPropertyMapping(IPropertyMapping mapping, Object argument);
}
