package org.hibernate.eclipse.console.views;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;

public abstract class AbstractReconcilingStrategy implements IReconcilingStrategy {
		
		private IDocument document;
		
		public AbstractReconcilingStrategy () {
		}

		public void setDocument(IDocument document) {
			this.document = document;
		}
		
		public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
			if (document == null)
				return;
			
			doReconcile(document);
		}
		
		public void reconcile(IRegion partition) {
			if (document == null)
				return;
			doReconcile(document);
		}

		abstract protected void doReconcile(IDocument doc);
	}
