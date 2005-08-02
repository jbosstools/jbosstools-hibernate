package org.hibernate.eclipse.console.editors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationPresenter;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

/**
 * content assist processor for HQL code.
 */
public class HQLCompletionProcessor implements IContentAssistProcessor {

    private char[] fProposalAutoActivationSet;

    /**
     * Simple content assist tip closer. The tip is valid in a range of 5
     * characters around its popup location.
     */
    protected static class Validator implements IContextInformationValidator, IContextInformationPresenter {
        protected int fInstallOffset;

        public boolean isContextInformationValid( int offset ) {
            return Math.abs( fInstallOffset - offset ) < 5;
        }

        public void install( IContextInformation info, ITextViewer viewer, int offset ) {
            fInstallOffset = offset;
        }

        public boolean updatePresentation( int position, TextPresentation presentation ) {
            return true;
        }
    };

    private static class CompletionProposalComparator implements Comparator {
        public int compare( Object o1, Object o2 ) {
            ICompletionProposal c1 = (ICompletionProposal) o1;
            ICompletionProposal c2 = (ICompletionProposal) o2;
            return c1.getDisplayString().compareTo( c2.getDisplayString() );
        }
    };

    protected IContextInformationValidator fValidator = new Validator();

    private Comparator fComparator;
    
    public HQLCompletionProcessor() {
        super();

        // activation/trigger to invoke content assist
        char[] completionChars = { '.' };
        setCompletionProposalAutoActivationCharacters( completionChars );

        fComparator = new CompletionProposalComparator();
    }

    /**
     * Tells this processor to order the proposals alphabetically.
     * 
     * @param order <code>true</code> if proposals should be ordered.
     */
    public void orderProposalsAlphabetically( boolean order ) {
        fComparator = order ? new CompletionProposalComparator() : null;
    }

    /**
     * Returns a list of proposed content completions based on the specified
     * location within the document that corresponds to the current cursor
     * position within the text-editor control.
     * 
     * @param viewer the viewer whose document is used to compute the proposals
     * @param documentPosition a location within the document
     * @return an array of content-assist proposals
     */
    public ICompletionProposal[] computeCompletionProposals( ITextViewer viewer, int documentOffset ) {
        ICompletionProposal[] result = null;
        
        try {
            IDocument doc = viewer.getDocument();
            ITypedRegion partition = null;

            if (documentOffset > 0) {
                if (doc.getChar( documentOffset - 1 ) == ';')
                    partition = viewer.getDocument().getPartition( documentOffset );
                else
                    // for incomplete statement.
                    partition = viewer.getDocument().getPartition( documentOffset - 1 );
            }
            else
                partition = viewer.getDocument().getPartition( documentOffset );

            result = computeProposals( doc, partition, documentOffset );            
        }
        catch (BadLocationException x) {
        }
        ;
        
        if (result != null)
            result = order( result );        
        
        return result;
    }

    private ICompletionProposal[] computeProposals(IDocument doc, ITypedRegion partition, int documentOffset) {
    	ICompletionProposal[] result = null;

    	try {
        if (doc != null && partition != null && documentOffset >= 0) {

            List proposalList = new ArrayList();
            String replacementStr = null;
            String displayStr = null;
            String startWord = null;
            char docOffsetChar;
            
            // Get document offset to the start of "word" where content assist
            // is requested.
            int offset = getPartitionOffset( doc, partition, documentOffset, partition.getOffset() );

            try {
                startWord = doc.get( offset, documentOffset - offset );            
           
                /*
                docOffsetChar = doc.getChar( documentOffset - 1 );
                
                if (docOffsetChar == '.') {
                    StringTokenizer tokenizer = new StringTokenizer(startWord, "."); //$NON-NLS-1$
                    List tokenList = new ArrayList();
                    while (tokenizer.hasMoreTokens()) {
                        tokenList.add(tokenizer.nextToken());
                    }
                }  
                */    
                
                findMatchingWords( documentOffset, proposalList, startWord, HQLCodeScanner.getHQLKeywords(), "keyword" );
				findMatchingWords( documentOffset, proposalList, startWord, HQLCodeScanner.getHQLFunctionNames(), "function");
				
				result = (ICompletionProposal[]) proposalList.toArray(new ICompletionProposal[proposalList.size()]);
                
            }
            catch (BadLocationException exc) {
            }
        }
    	} catch (Throwable te) {
    		te.printStackTrace();
    	}
        
		return result;
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

	/**
     * Returns a list of content-assist tips based on the specified location
     * within the document that corresponds to the current cursor position
     * within the text-editor control.
     * 
     * @param viewer the viewer whose document is used to compute the tips
     * @param documentPosition a location within the document
     * @return an array of content-assist tips
     */

    public IContextInformation[] computeContextInformation( ITextViewer viewer, int documentOffset ) {
        IContextInformation[] result = null;
        return result;
    }

    /**
     * Returns a string of characters which when pressed should automatically
     * display content-assist proposals.
     * 
     * @see IContentAssistProcessor.getCompletionProposalAutoActivationCharacters()
     * 
     * @return string of characters
     */
    public char[] getCompletionProposalAutoActivationCharacters() {
        return fProposalAutoActivationSet;
    }

    /**
     * Returns a string of characters which when pressed should automatically
     * display a content-assist tip.
     * 
     * @return string of characters
     */
    public char[] getContextInformationAutoActivationCharacters() {
        return new char[] { '#' };
    }

    /**
     * Returns a delegate used to determine when a displayed tip should be
     * dismissed.
     * 
     * @return a tip closer
     */
    public IContextInformationValidator getContextInformationValidator() {
        return fValidator;
    }


    /**
     * Returns the reason why the content-assist processor was unable to produce
     * any proposals or tips.
     * 
     * @return an error message or null if no error occurred
     */
    public String getErrorMessage() {
        return null;
    }

    /**
     * Orders the given proposals.
     * 
     * @params ICompletionProposal[] List of proposals to be ordered
     */
    private ICompletionProposal[] order( ICompletionProposal[] proposals ) {
        if (fComparator != null)
            Arrays.sort( proposals, fComparator );
        return proposals;
    }

    /**
     * Sets this processor's set of characters triggering the activation of the
     * completion proposal computation.
     * 
     * @param activationSet the activation set
     */
    public void setCompletionProposalAutoActivationCharacters( char[] activationSet ) {
        fProposalAutoActivationSet = activationSet;
    }

    /**
     * Returns the document offset to the start of the "word" where content
     * assist is requested.
     * 
     * @param doc the current document
     * @param partition document partition region. A region consists of offset,
     *            length, and type.
     * @param documentOffset offset into the document
     * @param offset offset in the document to start of the name preceeding the
     *            activation character
     */
    public int getPartitionOffset( IDocument doc, ITypedRegion partition, int documentOffset, int offset ) {
        boolean loop = true;

        int offset1 = documentOffset - 1;
        try {
            while (loop && offset <= offset1) {
                switch (doc.getChar( offset1 )) {
                    case 10: // linefeed
                    case 32: // space
                    case 13: // return
                    case 9: { // tab
                        loop = false;
                    }
                        break;
                    default: {
                        offset1--;
                    }
                        break;
                }
            }
        }
        catch (BadLocationException x) {
        }

        offset = offset1 + 1;

        return offset;
    }

    /**
     * Returns the document offset to the start of the "word" where content
     * assist is requested.
     * 
     * @param doc the current document
     * @param partition document partition region. A region consists of offset,
     *            length, and type.
     * @param documentOffset offset into the document
     * @param leadingString
     * @param position <code>1</code> from current position <code>0</code>
     *            after leadingString
     * @return the partition offset
     */
    public int getPartitionOffset( IDocument doc, ITypedRegion partition, int documentOffset, String leadingString,
            int position ) {
        int offset = partition.getOffset() + leadingString.length();

        if (documentOffset <= offset)
            return offset;

        switch (position) {
            case 0: {
            }
                break;
            default: {
                boolean loop = true;
                int offset1 = documentOffset - 1;
                try {
                    while (loop && offset <= offset1) {
                        switch (doc.getChar( offset1 )) {
                            case 10: // linefeed
                            case 32: // space
                            case 13: // return
                            case 9: { // tab
                                loop = false;
                            }
                                break;
                            default: {
                                offset1--;
                            }
                                break;
                        }
                    }
                }
                catch (BadLocationException x) {
                }
                ;

                offset = offset1 + 1;
            }
                break;
        }

        return getPartitionOffset( doc, partition, documentOffset, offset );
    }

}