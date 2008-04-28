/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.hibernate.eclipse.mapper.editors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredPartitionTypes;
import org.eclipse.wst.xml.core.internal.provisional.text.IXMLPartitions;
import org.eclipse.wst.xml.ui.StructuredTextViewerConfigurationXML;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.eclipse.wst.xml.ui.internal.contentassist.NoRegionContentAssistProcessor;
import org.hibernate.eclipse.mapper.MapperPlugin;


public class CFGXMLStructuredTextViewerConfiguration extends StructuredTextViewerConfigurationXML {
	
	public CFGXMLStructuredTextViewerConfiguration() {
		super();
	}
	
	static public IJavaProject findJavaProject(ITextViewer viewer) {
		
		if(viewer==null) return null;
		
		IStructuredModel existingModelForRead = StructuredModelManager.getModelManager().getExistingModelForRead(viewer.getDocument());
	
		if(existingModelForRead==null) return null;
		
		IJavaProject javaProject = null;
		try {
			String baseLocation = existingModelForRead.getBaseLocation();
			// 20041129 (pa) the base location changed for XML model
			// because of FileBuffers, so this code had to be updated
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=79686
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IPath filePath = new Path(baseLocation);
			IProject project = null;
			if (filePath.segmentCount() > 0) {
				project = root.getProject(filePath.segment(0));
			}
			if (project != null) {
				javaProject = JavaCore.create(project);
			}
		}
		catch (Exception ex) {
			MapperPlugin.getDefault().logException(ex); //$NON-NLS-1$
		}
		return javaProject;
	}
	
	static public IJavaProject findJavaProject(IDocument doc) {
		IResource resource = getProject(doc);
		if(resource!=null) {
			IProject project = resource.getProject();
			if(project!=null) {
				return JavaCore.create(project);
			}
		}
		return null;
	}
	
	static public IJavaProject findJavaProject(ContentAssistRequest request) {
		IResource resource = getProject(request);
		if(resource!=null) {
			IProject project = resource.getProject();
			if(project!=null) {
				return JavaCore.create(project);
			}
		}
		return null;
	}
	
	static public IProject getProject(ContentAssistRequest request) {
		
		if (request != null) {
			IStructuredDocumentRegion region = request.getDocumentRegion();
			if (region != null) {
				IDocument document = region.getParentDocument();
				return getProject( document );
			}
		}
		return null;
	}

	static public IProject getProject(IDocument document) {
		IStructuredModel model = null;
		try {
			model = StructuredModelManager.getModelManager().getExistingModelForRead(document);
			if (model != null) {
				String baselocation = model.getBaseLocation();
				if (baselocation != null) {
					// copied from JSPTranslationAdapter#getJavaProject
					IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
					IPath filePath = new Path(baselocation);
					if (filePath.segmentCount() > 0) {
						return root.getProject(filePath.segment(0));								
					}
				}
			} 
			return null;
		}
		finally {
			if (model != null)
				model.releaseFromRead();
		}
	}
	
	protected IContentAssistProcessor[] getContentAssistProcessors(ISourceViewer sourceViewer, String partitionType) {
		IContentAssistProcessor[] processors = null;
		
		if ((partitionType == IStructuredPartitionTypes.DEFAULT_PARTITION) || (partitionType == IXMLPartitions.XML_DEFAULT)) {
			processors = new IContentAssistProcessor[]{new CFGXMLContentAssistProcessor()}; // TODO: return cached one ?
		}
		else if (partitionType == IStructuredPartitionTypes.UNKNOWN_PARTITION) {
			processors = new IContentAssistProcessor[]{new NoRegionContentAssistProcessor()};
		}
		
		return processors;
	}	
}
