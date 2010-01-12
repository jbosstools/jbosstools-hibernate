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
import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.execution.ExecutionContext;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.QueryEditor;
import org.hibernate.tool.ide.completion.HQLCodeAssist;
import org.hibernate.tool.ide.completion.IHQLCodeAssist;

/**
 * content assist processor for HQL code.
 */
public class HQLCompletionProcessor implements IContentAssistProcessor {

    private char[] autoActivationChars = { '.' };
    protected IContextInformationValidator validator = new MinimalDiffContextInformationValidator(5);
    private final Comparator<ICompletionProposal> completionComparator;
	//private final HQLEditor editor;
    private QueryEditor hqlEditor;
	private String errorMessage;

    public HQLCompletionProcessor(QueryEditor editor) {
        super();
		this.hqlEditor = editor;
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
            	result = computeProposals( doc, partition.getOffset(), documentOffset, hqlEditor.getConsoleConfiguration() );
            }
        }
        catch (BadLocationException x) {
        }
        catch (RuntimeException re) {
        	HibernateConsolePlugin.getDefault().logErrorMessage( HibernateConsoleMessages.HQLCompletionProcessor_error_while_performing_hql_completion, re );
        }

        if (result != null)
            result = sort( result );

        return result;
    }

    ICompletionProposal[] computeProposals(IDocument doc, int lineStart, final int currentOffset, final ConsoleConfiguration consoleConfiguration) {
    	ICompletionProposal[] result = null;
    	errorMessage = null;
    	if (doc != null && currentOffset >= 0) {

    		List<ICompletionProposal> proposalList = new ArrayList<ICompletionProposal>();
    		String startWord = null;

    		int startOffset = findNearestWhiteSpace( doc, currentOffset, lineStart );

    		int wordLength = currentOffset - startOffset;
    		startWord = getWord( doc, startOffset, wordLength );

    		if(startWord!=null) {
				try {
					doc.get(0,doc.getLength()).toCharArray();
				}
				catch (BadLocationException e) {
					errorMessage = HibernateConsoleMessages.HQLCompletionProcessor_could_not_get_document_contents;
					return result;
				}

				if(consoleConfiguration != null && consoleConfiguration.getConfiguration()==null) {
					try{
					 	consoleConfiguration.build();
					 	consoleConfiguration.execute( new ExecutionContext.Command() {
					 		public Object execute() {
					 			if(consoleConfiguration.hasConfiguration()) {
					 			consoleConfiguration.getConfiguration().buildMappings();
					 		}
					 			return consoleConfiguration;
					 		}
						});
					} catch (HibernateException e){
					}
				}
				
				Configuration configuration = consoleConfiguration!=null?consoleConfiguration.getConfiguration():null;
				IHQLCodeAssist hqlEval = new HQLCodeAssist(configuration);
				EclipseHQLCompletionRequestor eclipseHQLCompletionCollector = new EclipseHQLCompletionRequestor();
				hqlEval.codeComplete(doc.get(), currentOffset, eclipseHQLCompletionCollector);
				proposalList.addAll(eclipseHQLCompletionCollector.getCompletionProposals());
				errorMessage = eclipseHQLCompletionCollector.getLastErrorMessage();

				/*if(configuration == null && consoleConfiguration!=null) {
					proposalList.add(new LoadConsoleCFGCompletionProposal(consoleConfiguration));
				}*/

    			//findMatchingWords( currentOffset, proposalList, startWord, HQLCodeScanner.getHQLKeywords(), "keyword" );
    			//findMatchingWords( currentOffset, proposalList, startWord, HQLCodeScanner.getHQLFunctionNames(), "function");

    			result = proposalList.toArray(new ICompletionProposal[proposalList.size()]);
    			if(result.length==0 && errorMessage==null) {
    				errorMessage = HibernateConsoleMessages.HQLCompletionProcessor_no_hql_completions_available;
    			}

    		} else {
    			errorMessage = HibernateConsoleMessages.HQLCompletionProcessor_no_start_word_found;
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

    public char[] getContextInformationAutoActivationCharacters() { return null; }

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