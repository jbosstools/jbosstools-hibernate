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
package org.jboss.tools.hibernate.internal.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.jboss.tools.hibernate.core.ICodeRendererService;
import org.jboss.tools.hibernate.core.OrmCore;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;


/**
 * @author Nick - mailto:n.belaevski@exadel.com
 * created: 26.09.2005
 * 
 */
public class ExtensionBinder {

    public static final String CODE_RENDERER_SERVICE = "codeRenderers";
    
    private static IExtension[] getExtensions(String simpleName)
    {
        List<IExtension> list = new ArrayList<IExtension>();
        
        if (simpleName != null)
        {
            IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint(OrmCore.PLUGIN_ID+"."+simpleName);
            IExtension[] elements = point.getExtensions();
            for (int i = 0; i < elements.length; i++) {
                IExtension element = elements[i];
                
                IConfigurationElement[] configs = element.getConfigurationElements();
                for (int j = 0; j < configs.length; j++) {
                    IConfigurationElement element2 = configs[j];
                    
                    try {
                    Object obj;
                        obj = element2.createExecutableExtension("class");
                        if (obj instanceof ICodeRendererService) {
                            list.add(element);
                        }
                    } catch (CoreException e) {
                        ExceptionHandler.logThrowableWarning(e,e.getMessage());
                    }
                }
            }
        }
        return (IExtension[]) list.toArray(new IExtension[0]);
    }

    public static String[][] getExtensionsParameters(String simpleName)
    {
        List<String> names = new ArrayList<String>();
        List<String> ids = new ArrayList<String>();

        String[][] result = new String[2][];
        
        IExtension[] extensions = getExtensions(simpleName);
    
        for (int i = 0; i < extensions.length; i++) {
            IExtension extension = extensions[i];
            
            IConfigurationElement[] elements = extension.getConfigurationElements();
            for (int j = 0; j < elements.length; j++) {
                IConfigurationElement element = elements[j];
                
                if (!ids.contains(element.getAttribute("id")))
                {
                    names.add(element.getAttribute("name"));
                    ids.add(element.getAttribute("id"));
                }
            }
        }
        
        result[0] = (String[]) names.toArray(new String[0]);
        result[1] = (String[]) ids.toArray(new String[0]);
        
        return result;
    }

    public static String getDefaultImplementationId(String simpleName)
    {
        IExtension[] extensions = getExtensions(simpleName);
        for (int i = 0; i < extensions.length; i++) {
            IExtension extension = extensions[i];
            
            if (OrmCore.PLUGIN_ID.equals(extension.getNamespace()))
            {
                IConfigurationElement[] elements = extension.getConfigurationElements();
                for (int j = 0; j < elements.length; j++) {
                    IConfigurationElement element = elements[j];
                    
                    return element.getAttribute("id");
                }
            }
        }
        
        return null;
    }

    public static ICodeRendererService getCodeRendererService(String identifier)
    {
        if (identifier == null)
        {
            String implId = getDefaultImplementationId(CODE_RENDERER_SERVICE);
            if (implId != null)
                return getCodeRendererService(implId);
            else
                return null;
        }
        
        IExtension[] extensions = getExtensions(CODE_RENDERER_SERVICE);
        for (int i = 0; i < extensions.length; i++) {
            IExtension extension = extensions[i];
            
            String id = null;
            IConfigurationElement[] elt = extension.getConfigurationElements();
            for (int j = 0; j < elt.length; j++) {
                IConfigurationElement element = elt[j];
                
                id = element.getAttribute("id");
                
                if (identifier.equals(id))
                {
                    Object obj;
                    try {
                        obj = element.createExecutableExtension("class");
                        if (obj instanceof ICodeRendererService) {
                            
                            return (ICodeRendererService) obj;
                        }
                    } catch (CoreException e) {
                        ExceptionHandler.logThrowableWarning(e,e.getMessage());
                    }
                }
            }
        }
        return null;
    }

}
