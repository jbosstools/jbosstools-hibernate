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
package org.jboss.tools.hibernate.internal.core.hibernate.automapping;

import java.util.ArrayList;
import java.util.Iterator;

import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IPersistentValueMapping;
import org.jboss.tools.hibernate.core.hibernate.IManyToManyMapping;
import org.jboss.tools.hibernate.core.hibernate.IManyToOneMapping;
import org.jboss.tools.hibernate.core.hibernate.IOneToManyMapping;
import org.jboss.tools.hibernate.core.hibernate.IOneToOneMapping;
import org.jboss.tools.hibernate.internal.core.BaseMappingVisitor;
import org.jboss.tools.hibernate.internal.core.hibernate.CollectionMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.SimpleValueMapping;

/**
 * @author Nick - mailto:n.belaevski@exadel.com
 * created: 02.08.2005
 * 
 */
public class LinkedFieldsSearcher extends BaseMappingVisitor {
    private IMapping mapping;
    private ColumnTrack track;
    private ArrayList<IPersistentValueMapping> results;
    
    public LinkedFieldsSearcher(IMapping mapping) {
        this.mapping = mapping;
    }
    
    public ArrayList<IPersistentValueMapping> findOppositeSide(Iterator columns) {
        results = new ArrayList<IPersistentValueMapping>();
        track = new ColumnTrack(new ColumnSet(columns));
        mapping.accept(this,null);        
        return results;
    }
    
    public ArrayList<IPersistentValueMapping> findOppositeSide(IPersistentValueMapping value) {
        results = new ArrayList<IPersistentValueMapping>();
        SimpleValueMapping svm = null;
//        IPersistentClassMapping target = null;
        
        if (value instanceof CollectionMapping) {
            CollectionMapping collection = (CollectionMapping) value;
            
            if (collection.getKey() instanceof SimpleValueMapping) {
                svm = (SimpleValueMapping) collection.getKey();
            }
        } else if (value instanceof SimpleValueMapping) {
            svm = (SimpleValueMapping) value;
        }
    
        if (svm == null) {
            return null;
        }
        
        track = new ColumnTrack(new ColumnSet(svm.getColumnIterator()));

        mapping.accept(this,null);
        
        return results;
    }

    public Object visitManyToManyMapping(IManyToManyMapping mapping, Object argument) {
        if (argument instanceof CollectionMapping) {
            CollectionMapping collection = (CollectionMapping) argument;
            
            if (new ColumnTrack(new ColumnSet(mapping.getColumnIterator())).equals(track)) {
                results.add(collection);
            } else if (collection.getKey() != null && new ColumnTrack(new ColumnSet(collection.getKey().getColumnIterator())).equals(track)) {
                results.add(collection);
            }
        }
        return super.visitManyToManyMapping(mapping, argument);
    }

    public Object visitManyToOneMapping(IManyToOneMapping mapping, Object argument) {
        if (new ColumnTrack(new ColumnSet(mapping.getColumnIterator())).equals(track))
        {
            results.add(mapping);
        }
        return super.visitManyToOneMapping(mapping, argument);
    }

    public Object visitOneToManyMapping(IOneToManyMapping mapping, Object argument) {
        if (argument instanceof CollectionMapping) {
            CollectionMapping collection = (CollectionMapping) argument;
            
            if (new ColumnTrack(new ColumnSet(mapping.getColumnIterator())).equals(track)) {
                results.add(collection);
            } else if (collection.getKey() != null && new ColumnTrack(new ColumnSet(collection.getKey().getColumnIterator())).equals(track)) {
                results.add(collection);
            }
        }
        return super.visitOneToManyMapping(mapping, argument);
    }

    public Object visitOneToOneMapping(IOneToOneMapping mapping, Object argument) {
        if (new ColumnTrack(new ColumnSet(mapping.getColumnIterator())).equals(track)) {
            results.add(mapping);
        }
        return super.visitOneToOneMapping(mapping, argument);
    }
    
}
