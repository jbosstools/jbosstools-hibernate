package org.hibernate.eclipse.console.editors;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

public class HQLConfiguration extends SourceViewerConfiguration {
	
	private HQLTagScanner tagScanner;
	private HQLScanner scanner;
	private ColorManager colorManager;

	public HQLConfiguration(ColorManager colorManager) {
		this.colorManager = colorManager;
	}
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] {
			IDocument.DEFAULT_CONTENT_TYPE,
			HQLPartitionScanner.HQL_COMMENT,
			HQLPartitionScanner.HQL_TAG };
	}
	

	protected HQLScanner getXMLScanner() {
		if (scanner == null) {
			scanner = new HQLScanner(colorManager);
			scanner.setDefaultReturnToken(
				new Token(
					new TextAttribute(
						colorManager.getColor(IHQLColorConstants.DEFAULT))));
		}
		return scanner;
	}
	protected HQLTagScanner getXMLTagScanner() {
		if (tagScanner == null) {
			tagScanner = new HQLTagScanner(colorManager);
			tagScanner.setDefaultReturnToken(
				new Token(
					new TextAttribute(
						colorManager.getColor(IHQLColorConstants.TAG))));
		}
		return tagScanner;
	}

	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();

		DefaultDamagerRepairer dr =
			new DefaultDamagerRepairer(getXMLTagScanner());
		reconciler.setDamager(dr, HQLPartitionScanner.HQL_TAG);
		reconciler.setRepairer(dr, HQLPartitionScanner.HQL_TAG);

		dr = new DefaultDamagerRepairer(getXMLScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		NonRuleBasedDamagerRepairer ndr =
			new NonRuleBasedDamagerRepairer(
				new TextAttribute(
					colorManager.getColor(IHQLColorConstants.XML_COMMENT)));
		reconciler.setDamager(ndr, HQLPartitionScanner.HQL_COMMENT);
		reconciler.setRepairer(ndr, HQLPartitionScanner.HQL_COMMENT);

		return reconciler;
	}

}