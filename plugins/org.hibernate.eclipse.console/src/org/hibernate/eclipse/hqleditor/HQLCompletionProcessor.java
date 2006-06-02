package org.hibernate.eclipse.hqleditor;

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
import org.hibernate.cfg.Configuration;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.tool.ide.completion.HQLCodeAssist;
import org.hibernate.tool.ide.completion.IHQLCodeAssist;

/**
 * content assist processor for HQL code.
 */
public class HQLCompletionProcessor implements IContentAssistProcessor {

    private char[] autoActivationChars = { '.' };
    protected IContextInformationValidator validator = new MinimalDiffContextInformationValidator(5);
    private final Comparator completionComparator;
	private final HQLEditor editor;
	private String errorMessage;

    public HQLCompletionProcessor(HQLEditor editor) {
        super();
		this.editor = editor;
        completionComparator = DisplayStringProposalComparator.INSTANCE;
    }

    public ICompletionProposal[] computeCompletionProposals( ITextViewer viewer, int documentOffset ) {
        ICompletionProposal[] result = new ICompletionProposal[0];
        
        try {
            IDocument doc = viewer.getDocument();
            ITypedRegion partition = null;

            if (documentOffset > 0) {
                partition = viewer.getDocument().getPartition( documentOffset - 1 );
            }
            else {
            	partition = viewer.getDocument().getPartition( documentOffset );
            }

            if(partition!=null) {
            	result = computeProposals( doc, partition.getOffset(), documentOffset, editor.getConsoleConfiguration() );
            }
        }
        catch (BadLocationException x) {
        }       
        catch (RuntimeException re) {
        	HibernateConsolePlugin.getDefault().logErrorMessage( "Error while performing HQL completion", re );
        }
        
        if (result != null)
            result = sort( result );        
        
        return result;
    }

    ICompletionProposal[] computeProposals(IDocument doc, int lineStart, final int currentOffset, ConsoleConfiguration consoleConfiguration) {
    	ICompletionProposal[] result = null;
    	errorMessage = null;
    	if (doc != null && currentOffset >= 0) {
    		
    		List proposalList = new ArrayList();
    		String startWord = null;
    		
    		int startOffset = findNearestWhiteSpace( doc, currentOffset, lineStart );
    		
    		int wordLength = currentOffset - startOffset;
    		startWord = getWord( doc, startOffset, wordLength );            
    		
    		if(startWord!=null) {
    			char[] cs = new char[0];
				try {
					cs = doc.get(0,doc.getLength()).toCharArray();
				}
				catch (BadLocationException e) {
					errorMessage = "Could not get document contents";
					return result;
				}
				
				Configuration configuration = consoleConfiguration!=null?consoleConfiguration.getConfiguration():null;
				
				IHQLCodeAssist hqlEval = new HQLCodeAssist(configuration);
				EclipseHQLCompletionRequestor eclipseHQLCompletionCollector = new EclipseHQLCompletionRequestor();
				hqlEval.codeComplete(doc.get(), currentOffset, eclipseHQLCompletionCollector);
				proposalList.addAll(eclipseHQLCompletionCollector.getCompletionProposals());
				errorMessage = eclipseHQLCompletionCollector.getLastErrorMessage();
    			//findMatchingWords( currentOffset, proposalList, startWord, HQLCodeScanner.getHQLKeywords(), "keyword" );
    			//findMatchingWords( currentOffset, proposalList, startWord, HQLCodeScanner.getHQLFunctionNames(), "function");
    			
    			result = (ICompletionProposal[]) proposalList.toArray(new ICompletionProposal[proposalList.size()]);
    			if(result.length==0 && errorMessage==null) {
    				errorMessage = "No HQL completions available.";
    			}
    	
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
    			char c = doc.getChar(tmpOffset);
				if(c=='"' || Character.isWhitespace(c)) {
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