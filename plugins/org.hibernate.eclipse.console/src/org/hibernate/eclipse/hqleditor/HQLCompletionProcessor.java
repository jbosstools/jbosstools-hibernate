package org.hibernate.eclipse.hqleditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
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
    	ICompletionProposal[] result = null;

    	if (doc != null && partition != null && currentOffset >= 0) {
    		
    		List proposalList = new ArrayList();
    		String startWord = null;
    		
    		int startOffset = findNearestWhiteSpace( doc, currentOffset, partition.getOffset() );
    		
    		int wordLength = currentOffset - startOffset;
    		startWord = getWord( doc, startOffset, wordLength );            
    		
    		if(startWord!=null) {
    			findMatchingEntities( currentOffset, proposalList, startWord, editor.getConsoleConfiguration() );
    			findMatchingWords( currentOffset, proposalList, startWord, HQLCodeScanner.getHQLKeywords(), "keyword" );
    			findMatchingWords( currentOffset, proposalList, startWord, HQLCodeScanner.getHQLFunctionNames(), "function");
    		
    			result = (ICompletionProposal[]) proposalList.toArray(new ICompletionProposal[proposalList.size()]);
    			if(result.length==0) {
    				errorMessage = "No HQL completions avaialable.";
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

	private void findMatchingEntities(int documentOffset, List proposalList, String startWord, ConsoleConfiguration lastUsedConfiguration) {
		if(lastUsedConfiguration!=null) {
			Configuration configuration = lastUsedConfiguration.getConfiguration();
			if(configuration!=null) {
				Iterator iterator = configuration.getImports().keySet().iterator();
				while ( iterator.hasNext() ) {
					String entityName = (String) iterator.next();
					if(entityName.startsWith(startWord)) {
						proposalList.add(new CompletionProposal(entityName, documentOffset-startWord.length(), startWord.length(), entityName.length(), null, entityName, null, null));
					}
				}
			}
		}		
	}

	private void findMatchingWords(int documentOffset, List proposalList, String startWord, String[] keywords, String prefix) {
		int i = Arrays.binarySearch(keywords, startWord);
		if(i<0) {
			i = Math.abs(i+1);
		}
		
		for (int cnt = i; cnt < keywords.length; cnt++) {
			String string = keywords[cnt];
			if(string.startsWith(startWord)) {
				proposalList.add(new CompletionProposal(string, documentOffset-startWord.length(), startWord.length(), string.length(), null, string + " (" + prefix + ")", null, null));
			} else {
				break;
			}
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