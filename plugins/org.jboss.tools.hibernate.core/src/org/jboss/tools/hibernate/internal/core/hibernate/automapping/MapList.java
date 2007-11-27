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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Nick - mailto:n.belaevski@exadel.com
 * created: 13.07.2005
 * 
 */
public class MapList {
    
    private Map map = new HashMap();
    
    private final Iterator EMPTY_ITERATOR = new ArrayList().iterator();
    
    public void add(Object key, Object value)
    {
        List holder;
        if (map.containsKey(key))
        {
            holder = (List) map.get(key);
        }
        else
        {
            holder = new ArrayList();
            map.put(key,holder);
        }
        holder.add(value);
    }
    
    public Iterator getKeys()
    {
        return map.keySet().iterator();
    }
    
    public Iterator get(Object key)
    {
        List list = (List) map.get(key);
        if (list == null)
        {
            return EMPTY_ITERATOR;
        }
        else
        {
            return list.iterator();
        }
    }
}
