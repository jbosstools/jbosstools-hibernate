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

import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.wst.sse.core.text.IStructuredPartitions;
import org.eclipse.wst.xml.core.text.IXMLPartitions;
import org.eclipse.wst.xml.ui.StructuredTextViewerConfigurationXML;
import org.eclipse.wst.xml.ui.internal.contentassist.NoRegionContentAssistProcessor;
import org.jboss.tools.hibernate.runtime.spi.IService;


public class CFGXMLStructuredTextViewerConfiguration extends StructuredTextViewerConfigurationXML {
	
	public CFGXMLStructuredTextViewerConfiguration() {
		super();
	}
	
	protected IContentAssistProcessor[] getContentAssistProcessors(ISourceViewer sourceViewer, String partitionType) {
		IContentAssistProcessor[] processors = null;
		IService service = StructuredTextViewerConfigurationUtil.getService(sourceViewer);
		if ((IStructuredPartitions.DEFAULT_PARTITION.equals(partitionType)) || (IXMLPartitions.XML_DEFAULT.equals(partitionType))) {
			processors = new IContentAssistProcessor[]{new CFGXMLContentAssistProcessor(service)}; // TODO: return cached one ?
		}
		else if (IStructuredPartitions.UNKNOWN_PARTITION.equals(partitionType)) {
			processors = new IContentAssistProcessor[]{new NoRegionContentAssistProcessor()};
		}
		
		return processors;
	}
	
}
