package org.hibernate.eclipse.hqleditor;

import java.util.Comparator;

import org.eclipse.jface.text.contentassist.ICompletionProposal;

/** 
 * Sort a comparator according to its display string.
 * Use the INSTANCE variable to save memory/time.
 */ 
public class DisplayStringProposalComparator implements Comparator {
	
	static public Comparator INSTANCE = new DisplayStringProposalComparator();
	
	public int compare( Object o1, Object o2 ) {
        ICompletionProposal c1 = (ICompletionProposal) o1;
        ICompletionProposal c2 = (ICompletionProposal) o2;
        return c1.getDisplayString().compareTo( c2.getDisplayString() );
    }
}