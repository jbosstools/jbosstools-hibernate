package org.hibernate.eclipse.launch;

import java.util.HashSet;
import java.util.Set;

/**
 * Do not remove this file! 
 * This file is necessary for SeamGenerateEnitiesWizard.java
 * org.jboss.tools.seam.ui
 * src.org.jboss.tools.seam.ui.wizard.SeamGenerateEnitiesWizard
 * for more information: JBIDE-4531
 * This is "hack" to get seam gen code generation work 
 * without launching in a separate process. 
 */
public class SeamUtil {
	public String lower(String name)
	{
		return name.substring(0, 1).toLowerCase() + name.substring(1);
	}
	public String upper(String name)
	{
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}
	public Set set()
	{
		return new HashSet();
	}
}
