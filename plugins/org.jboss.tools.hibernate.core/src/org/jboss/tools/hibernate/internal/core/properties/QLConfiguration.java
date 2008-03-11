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
package org.jboss.tools.hibernate.internal.core.properties;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.graphics.RGB;

/**
 * @author Konstantin Mishin
 * 
 * 
 */
public class QLConfiguration extends SourceViewerConfiguration {
	
	//private HQLDoubleClickStrategy doubleClickStrategy;
	private QLScanner scanner;
	private ColorManager colorManager;
//	akuzmin 14.09.2005	
	private String qlseparators;
	private String[] qlwords;
	public QLConfiguration(ColorManager colorManager,String[] words,String separators) {
		this.colorManager = colorManager;
		qlseparators=separators;
		qlwords=words;
	}
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] {IDocument.DEFAULT_CONTENT_TYPE};
	}

	protected QLScanner getSQLScanner() {
		if (scanner == null) {
//			akuzmin 14.09.2005
			scanner = new QLScanner(colorManager,qlwords,qlseparators);
			scanner.setDefaultReturnToken(
				new Token(
					new TextAttribute(
						colorManager.getColor(new RGB(0,0,0)))));
		}
		return scanner;
	}

	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();

		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getSQLScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		return reconciler;
	}


}
