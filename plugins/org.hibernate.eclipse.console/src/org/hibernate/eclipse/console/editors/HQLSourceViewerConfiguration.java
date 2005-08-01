package org.hibernate.eclipse.console.editors;

import org.eclipse.jface.text.DefaultAutoIndentStrategy;
import org.eclipse.jface.text.IAutoIndentStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.formatter.ContentFormatter;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.formatter.IFormattingStrategy;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.graphics.RGB;

/**
 * This class defines the editor add-ons; content assist, content formatter,
 * highlighting, auto-indent strategy, double click strategy. 
 */
public class HQLSourceViewerConfiguration extends SourceViewerConfiguration {

    /** The completion processor for completing the user's typing. */
    private HQLCompletionProcessor fCompletionProcessor;
	private HQLEditor fEditor;
    
    /**
     * This class implements a single token scanner.
     */
    static class SingleTokenScanner extends BufferedRuleBasedScanner {
        public SingleTokenScanner( TextAttribute attribute ) {
            setDefaultReturnToken( new Token( attribute ));
        }
    }

    /**
     * Constructs an instance of this class with the given HQLEditor to
     * configure.
     * @param editor 
     * 
     * @param editor the HQLEditor to configure
     */
    public HQLSourceViewerConfiguration(HQLEditor editor) {
    	fEditor = editor;
        fCompletionProcessor = new HQLCompletionProcessor(); 
    }
    
    public HQLEditor getHQLEditor() {
    	return fEditor;
    }
    
    /**
     * Returns the annotation hover which will provide the information to be
     * shown in a hover popup window when requested for the given
     * source viewer.
     * 
     * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getAnnotationHover(org.eclipse.jface.text.source.ISourceViewer)
     */
    public IAnnotationHover getAnnotationHover( ISourceViewer sourceViewer ) {
        return null; //new HQLAnnotationHover();
    }
    
    /**
     * Returns the auto indentation strategy ready to be used with the given source viewer
     * when manipulating text of the given content type.
     * 
     * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getAutoIndentStrategy(org.eclipse.jface.text.source.ISourceViewer, java.lang.String)
     */
    /*
    public IAutoIndentStrategy getAutoIndentStrategy( ISourceViewer sourceViewer, String contentType ) {
        return (IDocument.DEFAULT_CONTENT_TYPE.equals( contentType ) ? new HQLAutoIndentStrategy() : new DefaultAutoIndentStrategy());
    }*/
    
    public final static String HQL_PARTITIONING= "__hql_partitioning";   //$NON-NLS-1$
    
    /**
     * Returns the configured partitioning for the given source viewer. The partitioning is
     * used when the querying content types from the source viewer's input document.
     * 
     * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getConfiguredDocumentPartitioning(org.eclipse.jface.text.source.ISourceViewer)
     */
    public String getConfiguredDocumentPartitioning( ISourceViewer sourceViewer ) {
        return HQL_PARTITIONING;
    }

    /**
     * Creates, initializes, and returns the ContentAssistant to use with this editor.
     * 
     * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getContentAssistant(ISourceViewer)
     */
    public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
        ContentAssistant assistant = new ContentAssistant();
    
        assistant.setDocumentPartitioning( getConfiguredDocumentPartitioning( sourceViewer ));
        
        // Set content assist processors for various content types.
        fCompletionProcessor = new HQLCompletionProcessor();
        assistant.setContentAssistProcessor( fCompletionProcessor, IDocument.DEFAULT_CONTENT_TYPE);
        
        // Configure how content assist information will appear.
        assistant.enableAutoActivation( true );
        assistant.setAutoActivationDelay( 500 );
        assistant.setProposalPopupOrientation( IContentAssistant.PROPOSAL_STACKED );
        assistant.setContextInformationPopupOrientation( IContentAssistant.CONTEXT_INFO_ABOVE );
        //assistant.setContextInformationPopupBackground( HQLEditorPlugin.getDefault().getHQLColorProvider().getColor( new RGB( 150, 150, 0 )));
        //Set to Carolina blue
//        assistant.setContextInformationPopupBackground( HQLEditorPlugin.getDefault().getHQLColorProvider().getColor( new RGB( 0, 191, 255 )));
        
        return assistant;
    }

    /**
     * Creates, configures, and returns the ContentFormatter to use.
     * 
     * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getContentFormatter(ISourceViewer)
     */
    public IContentFormatter getContentFormatter(ISourceViewer sourceViewer) {
        ContentFormatter formatter = new ContentFormatter();
        formatter.setDocumentPartitioning( HQL_PARTITIONING );
               
        return formatter;
    }

 
    /**
     * Creates, configures, and returns a presentation reconciler to help with 
     * document changes.
     * 
     * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getPresentationReconciler(ISourceViewer)
     */
    public IPresentationReconciler getPresentationReconciler( ISourceViewer sourceViewer ) {

        // Get the color provider.
        HQLColorProvider colorProvider = new HQLColorProvider();
        
        // Create a presentation reconciler to handle handle document changes.
        PresentationReconciler reconciler = new PresentationReconciler();
        String docPartitioning = getConfiguredDocumentPartitioning( sourceViewer );
        reconciler.setDocumentPartitioning( docPartitioning );

        // Add a "damager-repairer" for changes in default text (HQL code).
        DefaultDamagerRepairer dr = new DefaultDamagerRepairer( new HQLCodeScanner( colorProvider ) );
        reconciler.setDamager( dr, IDocument.DEFAULT_CONTENT_TYPE );
        reconciler.setRepairer( dr, IDocument.DEFAULT_CONTENT_TYPE );
        
        // Add a "damager-repairer" for changes witin one-line HQL comments.
        dr = new DefaultDamagerRepairer( new SingleTokenScanner( new TextAttribute( colorProvider.getColor( HQLColorProvider.HQL_COMMENT_COLOR ))));
        reconciler.setDamager( dr, HQLPartitionScanner.HQL_COMMENT );
        reconciler.setRepairer( dr, HQLPartitionScanner.HQL_COMMENT );

        // Add a "damager-repairer" for changes witin quoted literals.
        dr = new DefaultDamagerRepairer( new SingleTokenScanner( new TextAttribute( colorProvider.getColor( HQLColorProvider.HQL_QUOTED_LITERAL_COLOR ))));
        reconciler.setDamager( dr, HQLPartitionScanner.HQL_QUOTED_LITERAL );
        reconciler.setRepairer( dr, HQLPartitionScanner.HQL_QUOTED_LITERAL );

        // Add a "damager-repairer" for changes witin delimited identifiers.
        dr = new DefaultDamagerRepairer( new SingleTokenScanner( new TextAttribute( colorProvider.getColor( HQLColorProvider.HQL_DELIMITED_IDENTIFIER_COLOR ))));
        reconciler.setDamager( dr, HQLPartitionScanner.HQL_DELIMITED_IDENTIFIER );
        reconciler.setRepairer( dr, HQLPartitionScanner.HQL_DELIMITED_IDENTIFIER );

        return reconciler;
    }

    
    /**
     * Returns the text hover which will provide the information to be shown
     * in a text hover popup window when requested for the given source viewer and
     * the given content type.
     * 
     * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getTextHover(org.eclipse.jface.text.source.ISourceViewer, java.lang.String)
     */
    public ITextHover getTextHover( ISourceViewer sourceViewer, String contentType ) {
        return new HQLTextHover();
    }

    
} // end class
