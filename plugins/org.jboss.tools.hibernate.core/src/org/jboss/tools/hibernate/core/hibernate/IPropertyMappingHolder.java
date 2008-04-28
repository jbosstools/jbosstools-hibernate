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

import java.util.Iterator;

/**
 * @author Nick - mailto:n.belaevski@exadel.com
 * created: 09.06.2005
 * This interface is designed to provide PropertyMapping with a possibility 
 * to add/remove themselves from their owner-holder, eg. ClassMapping, ComponentMapping, JoinMapping
 */
public interface IPropertyMappingHolder {
    public void removeProperty(IPropertyMapping propertyMapping);
    public void addProperty(IPropertyMapping propertyMapping);
    public Iterator<IPropertyMapping> getPropertyIterator();
    public void renameProperty(IPropertyMapping prop, String newName);
}
