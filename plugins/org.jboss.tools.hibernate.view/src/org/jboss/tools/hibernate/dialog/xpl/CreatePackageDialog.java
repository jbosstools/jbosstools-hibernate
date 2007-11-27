/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Exadel, Inc.
 *     Red Hat, Inc.
 *******************************************************************************/
package org.jboss.tools.hibernate.dialog.xpl;

import org.eclipse.ui.dialogs.SelectionDialog;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.jface.util.Assert;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaElementLabelProvider;

/**
 *  
 */

public class CreatePackageDialog {

 static ArrayList tmp=new ArrayList();
	public CreatePackageDialog(){
		
	}

	public static SelectionDialog createPackageDialog(Shell parent, IJavaProject project, int style, String filter) throws JavaModelException {
		tmp.clear();
		List packages= new ArrayList();
		String namepack;
		Assert.isTrue((style | IJavaElementSearchConstants.CONSIDER_BINARIES | IJavaElementSearchConstants.CONSIDER_REQUIRED_PROJECTS) ==
			(IJavaElementSearchConstants.CONSIDER_BINARIES | IJavaElementSearchConstants.CONSIDER_REQUIRED_PROJECTS));

		IPackageFragmentRoot[] roots= null;
		if ((style & IJavaElementSearchConstants.CONSIDER_REQUIRED_PROJECTS) != 0) {
		    roots= project.getAllPackageFragmentRoots();
		} else {	
			roots= project.getPackageFragmentRoots();	
		}
		
		List consideredRoots= null;
		if ((style & IJavaElementSearchConstants.CONSIDER_BINARIES) != 0) {
			consideredRoots= Arrays.asList(roots);
		} else {
			consideredRoots= new ArrayList(roots.length);
			for (int i= 0; i < roots.length; i++) {
				IPackageFragmentRoot root= roots[i];
				if (root.getKind() != IPackageFragmentRoot.K_BINARY){
					//if(!consideredRoots.contains(root))
					consideredRoots.add(root);
				}
					
			}
		}
		
		int flags= JavaElementLabelProvider.SHOW_DEFAULT;
		if (consideredRoots.size() > 1)
			flags= flags | JavaElementLabelProvider.SHOW_ROOT;

		
		Iterator iter= consideredRoots.iterator();
		while(iter.hasNext()) {
			IPackageFragmentRoot root= (IPackageFragmentRoot)iter.next();
			IJavaElement[] h=root.getChildren();
			for( int i=0;i<h.length;i++){
				namepack=h[i].getElementName();
			
			if(!namepack.equals(""))
				if( tmp.indexOf(h[i])<0 )
				tmp.add(h[i]);
			}
			
		}	
		packages.addAll(Arrays.asList(tmp.toArray()));
		ElementListSelectionDialog dialog= new ElementListSelectionDialog(parent, new JavaElementLabelProvider(flags));
		
		dialog.setIgnoreCase(false);
		dialog.setElements(packages.toArray()); 
		
		dialog.setFilter(filter);
		return dialog;
	}

}
