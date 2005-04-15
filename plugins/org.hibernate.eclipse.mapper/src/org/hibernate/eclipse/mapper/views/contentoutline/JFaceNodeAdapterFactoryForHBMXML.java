/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Jens Lukowski/Innoopract - initial API and implementation
 *     
 *******************************************************************************/
package org.hibernate.eclipse.mapper.views.contentoutline;



import org.eclipse.wst.sse.core.INodeAdapter;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.sse.ui.internal.contentoutline.IJFaceNodeAdapter;
import org.eclipse.wst.xml.ui.internal.contentoutline.JFaceNodeAdapterFactory;


/**
 * An adapter factory to create JFaceNodeAdapters.  Use this
 * adapter factory with a JFaceAdapterContentProvider to display
 * DOM nodes in a tree.
 */
public class JFaceNodeAdapterFactoryForHBMXML extends JFaceNodeAdapterFactory {

	public JFaceNodeAdapterFactoryForHBMXML() {
		this(IJFaceNodeAdapter.class, true);
	}

	public JFaceNodeAdapterFactoryForHBMXML(Object adapterKey, boolean registerAdapters) {
		super(adapterKey, registerAdapters);
	}

	/**
	 * Create a new JFace adapter for the DOM node passed in
	 */
	protected INodeAdapter createAdapter(INodeNotifier node) {
		if (singletonAdapter == null) {
			// create the JFaceNodeAdapter
			singletonAdapter = new JFaceNodeAdapterForHBMXML(this);
			initAdapter(singletonAdapter, node);
		}
		return singletonAdapter;
	}

}
