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
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;

/**
 * The HQLPartitionScanner is a RulesBasedPartitionScanner.  The HQL document 
 * partitions are computed dynamically as events signal that the document has 
 * changed. The document partitions are based on tokens that represent comments
 * and HQL code sections.
 */
public class HQLPartitionScanner extends RuleBasedPartitionScanner {
    // Define constants for HQL comments, literals, and identifiers.
    public final static String HQL_DEFAULT              = "__default_hql_block__"; //$NON-NLS-1$
    public final static String HQL_COMMENT              = "__hql_comment__"; //$NON-NLS-1$
    public final static String HQL_QUOTED_LITERAL       = "__hql_quoted_literal__"; //$NON-NLS-1$
    public final static String HQL_DELIMITED_IDENTIFIER = "__hql_delimited_identifier__"; //$NON-NLS-1$

    // Define constants for HQL DML Statements.
    public final static String HQL_SELECT = "__hql_select_statement__"; //$NON-NLS-1$
    public final static String HQL_INSERT = "__hql_insert_statement__"; //$NON-NLS-1$
    public final static String HQL_UPDATE = "__hql_update_statement__"; //$NON-NLS-1$
    public final static String HQL_DELETE = "__hql_delete_statement__"; //$NON-NLS-1$
    public final static String HQL_MERGE  = "__hql_merge_statement__"; //$NON-NLS-1$

    // Define constants for HQL DDL Statements.
    public final static String HQL_CREATE = "__hql_create_statement__"; //$NON-NLS-1$
    public final static String HQL_DROP   = "__hql_drop_statement__"; //$NON-NLS-1$
    public final static String HQL_ALTER  = "__hql_alter_statement__"; //$NON-NLS-1$

    // Define constants for HQL access control statements.
    public final static String HQL_GRANT  = "__hql_grant_statement__"; //$NON-NLS-1$
    public final static String HQL_REVOKE = "__hql_revoke_statement__"; //$NON-NLS-1$

    // Define constants for HQL transaction control statements.
    public final static String HQL_COMMIT   = "__hql_commit_statement__"; //$NON-NLS-1$
    public final static String HQL_ROLLBACK = "__hql_rollback_statement__"; //$NON-NLS-1$
    public final static String HQL_SET      = "__hql_set_statement__"; //$NON-NLS-1$
    
    // Define constants for HQL connection statements.
    public final static String HQL_CONNECT    = "__hql_connect_statement__"; //$NON-NLS-1$
    public final static String HQL_DISCONNECT = "__hql_disconnect_statement__"; //$NON-NLS-1$
    
    // Define constants for HQL miscellaneous statements.
    public final static String HQL_COMMENT_ON = "__hql_comment_on_statement__"; //$NON-NLS-1$
    public final static String HQL_TERMINATE  = "__hql_terminate_statement__"; //$NON-NLS-1$
    public final static String HQL_CATALOG    = "__hql_catalog_statement__"; //$NON-NLS-1$
    public final static String HQL_UNCATALOG  = "__hql_uncatalog_statement__"; //$NON-NLS-1$
    public final static String HQL_SIGNAL     = "__hql_signal_statement__"; //$NON-NLS-1$
    
    // Define a constant for HQL not otherwise covered.
    public final static String HQL_UNKNOWNHQL = "__hql__unknownhql_statement__"; //$NON-NLS-1$

    public final static String[] HQL_PARTITION_TYPES= new String[] {
        HQL_COMMENT,
        HQL_QUOTED_LITERAL,
        HQL_DELIMITED_IDENTIFIER,
    };
    
    /**
     * Gets the partitions of the given document as an array of 
     * <code>ITypedRegion</code> objects.  There is a distinct non-overlapping partition
     * for each comment line, string literal, delimited identifier, and "everything else"
     * (that is, HQL code other than a string literal or delimited identifier).
     * 
     * @param doc the document to parse into partitions
     * @return an array containing the document partion regions
     */
    public static ITypedRegion[] getDocumentRegions( IDocument doc ) {
        ITypedRegion[] regions = null;
        try {
            regions = TextUtilities.computePartitioning( doc, HQLSourceViewerConfiguration.HQL_PARTITIONING, 0, doc.getLength(), false );
        }
        catch ( BadLocationException e ) {
            // ignore
        }
        
        return regions;
    }

    /**
     * Constructs an instance of this class.  Creates rules to parse comment 
     * partitions in an HQL document.  This is the default constructor.
     */
    public HQLPartitionScanner() {
        super();
        
        List<IPredicateRule> rules= new ArrayList<IPredicateRule>();

        // Add rules for comments, quoted literals, and delimited identifiers.
        rules.add( new EndOfLineRule( "--", new Token( HQL_COMMENT ))); //$NON-NLS-1$
        rules.add( new SingleLineRule( "'", "'", new Token( HQL_QUOTED_LITERAL ), '\\' ));  //$NON-NLS-1$//$NON-NLS-2$
        rules.add( new SingleLineRule( "\"", "\"", new Token( HQL_DELIMITED_IDENTIFIER ), '\\' ));  //$NON-NLS-1$//$NON-NLS-2$

        IPredicateRule[] result= new IPredicateRule[ rules.size() ];
        rules.toArray( result );
        setPredicateRules( result );
    }
        
} // end class
