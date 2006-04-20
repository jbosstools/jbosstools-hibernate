package org.hibernate.eclipse.criteriaeditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.hibernate.eclipse.hqleditor.CompletionHelper;
import org.hibernate.eclipse.hqleditor.DisplayStringProposalComparator;
import org.hibernate.eclipse.hqleditor.HibernateResultCollector;
import org.hibernate.eclipse.hqleditor.MinimalDiffContextInformationValidator;
import org.hibernate.eclipse.hqleditor.HibernateResultCollector.Settings;

public class CriteriaCompletionProcessor implements IContentAssistProcessor {

    private char[] autoActivationChars = { '.' };
    protected IContextInformationValidator validator = new MinimalDiffContextInformationValidator(5);
    private final Comparator completionComparator;
	private final CriteriaEditor editor;
	private String errorMessage;

    public CriteriaCompletionProcessor(CriteriaEditor editor) {
        super();
		this.editor = editor;
        completionComparator = DisplayStringProposalComparator.INSTANCE;
    }

    public ICompletionProposal[] computeCompletionProposals( ITextViewer viewer, int documentOffset ) {
        ICompletionProposal[] result = null;
        
        try {
            IDocument doc = viewer.getDocument();
            ITypedRegion partition = null;

            if (documentOffset > 0) {
                partition = viewer.getDocument().getPartition( documentOffset - 1 );
            }
            else {
            	partition = viewer.getDocument().getPartition( documentOffset );
            }

            result = computeProposals( doc, partition, documentOffset );            
        }
        catch (BadLocationException x) {
        }       
        
        if (result != null)
            result = sort( result );        
        
        return result;
    }

    private ICompletionProposal[] computeProposals(IDocument doc, ITypedRegion partition, final int currentOffset) {
    	ICompletionProposal[] result = new ICompletionProposal[0];

    	if (doc != null && partition != null && currentOffset >= 0) {
    		
    		List proposalList = new ArrayList();
    		String startWord = null;
    		
    		int startOffset = findNearestWhiteSpace( doc, currentOffset, partition.getOffset() );
    		
    		int wordLength = currentOffset - startOffset;
    		startWord = getWord( doc, startOffset, wordLength );            
    		
    		if(startWord!=null) {
    			/*Settings settings = new Settings();
    	        settings.setAcceptClasses(true);
    	        settings.setAcceptInterfaces(true);
    	        settings.setAcceptPackages(true);
    	        settings.setAcceptTypes(true);
    		    return CompletionHelper.completeOnJavaTypes(null, settings,"", startWord, startOffset);*/
    			    			
    		} else {
    			errorMessage = "No start word found.";
    		}
    	}
    	
    	return result;
	}

	private String getWord(IDocument doc, int startOffset, int wordLength) {
		try {
			return doc.get( startOffset, wordLength );
		}
		catch (BadLocationException e) {
			return null; // no word found.
		}
	}
	
    public IContextInformation[] computeContextInformation( ITextViewer viewer, int documentOffset ) { return null; }

    public char[] getCompletionProposalAutoActivationCharacters() { return autoActivationChars; }

    public char[] getContextInformationAutoActivationCharacters() { return new char[0]; }

    public IContextInformationValidator getContextInformationValidator() { return validator; }

    public String getErrorMessage() { 
    	return errorMessage; 
    }

    private ICompletionProposal[] sort( ICompletionProposal[] proposals ) {
    	Arrays.sort( proposals, completionComparator );
    	return proposals;
    }

    public int findNearestWhiteSpace( IDocument doc, int documentOffset, int offset ) {
    	boolean loop = true;
    	
    	int tmpOffset = documentOffset - 1;
    	try {
    		while (loop && offset <= tmpOffset) {
    			if(Character.isWhitespace(doc.getChar(tmpOffset))) {
    				loop = false;
    			} else {
    				tmpOffset--;
    			}                        
    		}            
    	}
    	catch (BadLocationException x) {
    	}
    	
    	offset = tmpOffset + 1;
    	
    	return offset;
    }

  }