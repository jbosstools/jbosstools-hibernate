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

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.hibernate.eclipse.console.HibernateConsolePlugin;

/**
 * Generic completion proposal to be used as is, or as base for other completion proposals.
 * 
 * @author Max Rydahl Andersen
 *
 */
public class CompletionProposal implements ICompletionProposal, ICompletionProposalExtension {

    private String displayString;
    private String replacementString;
    private int replacementOffset;
    private int documentOffset = -1;
    private int replacementLength;
    private int cursorPosition;
    private Image image;
    private IContextInformation contextInformation;
    private int contextInformationPosition;
    private char[] triggerCharacters;
    private String additionalProposalInfo;

    /**
     * Creates a new completion proposal. All fields are initialized based on
     * the provided information.
     * 
     * @param additionalInfo additional information about the completion proposal
     * @param replacementString the actual string to be inserted into the document
     * @param replacementOffset offset of the text to be replaced
     * @param documentOffset offset of the document
     * @param replacementLength length of the text to be replaced
     * @param image the image to display for this proposal
     * @param displayString the string to be displayed for the proposal If set
     *            to <code>null</code>, the replacement string will be taken
     *            as display string.
     */
    public CompletionProposal( String additionalInfo, String replacementString, int replacementOffset,
            int documentOffset, int replacementLength, Image image, String displayString,
            IContextInformation contextInformation ) {

        this.replacementString = replacementString;
        this.replacementOffset = replacementOffset;
        this.documentOffset = documentOffset;
        this.replacementLength = replacementLength;
        this.image = image;
        this.displayString = displayString != null ? displayString : replacementString;

        this.cursorPosition = replacementString.length();

        this.contextInformation = contextInformation;

        this.triggerCharacters = null;
        this.additionalProposalInfo = additionalInfo;
    }

    /**
     * Creates a new completion proposal. All fields are initialized based on
     * the provided information.
     * 
     * @param replacementString the actual string to be inserted into the document
     * @param replacementOffset the offset of the text to be replaced
     * @param replacementLength the length of the text to be replaced
     * @param cursorPosition the position of the cursor following the insert
     *            relative to replacementOffset
     * @param image the image to display for this proposal
     * @param displayString the string to be displayed for the proposal
     * @param contentInformation the context information associated with this proposal
     * @param additionalProposalInfo the additional information associated with
     *            this proposal
     */
    public CompletionProposal( String replacementString, int replacementOffset, int replacementLength,
            int cursorPosition, Image image, String displayString, IContextInformation contextInformation,
            String additionalProposalInfo ) {

        this.replacementString = replacementString;
        this.replacementOffset = replacementOffset;
        this.documentOffset = replacementOffset;
        this.replacementLength = replacementLength;
        this.cursorPosition = cursorPosition;
        this.image = image;
        this.displayString = displayString;
        this.contextInformation = contextInformation;
        this.additionalProposalInfo = additionalProposalInfo;

    }

    public void setContextInformation( IContextInformation contextInformation ) {
        this.contextInformation = contextInformation;
        contextInformationPosition = (contextInformation != null ? cursorPosition : -1);
    }

    /**
     * Sets the trigger characters.
     * 
     * @param triggerCharacters The set of characters which can trigger the
     *            application of this completion proposal
     */
    public void setTriggerCharacters( char[] triggerCharacters ) {
        this.triggerCharacters = triggerCharacters;
    }

    /**
     * Sets the cursor position relative to the insertion offset. By default
     * this is the length of the completion string (Cursor positioned after the
     * completion)
     * 
     * @param cursorPosition The cursorPosition to set
     */
    public void setCursorPosition( int cursorPosition ) {

        if (cursorPosition >= 0) {
            this.cursorPosition = cursorPosition;
            this.contextInformationPosition = (contextInformation != null ? cursorPosition : -1);
        }
    }

    public void apply( IDocument document, char trigger, int offset ) {
        try {
            // patch replacement length

            int delta = offset - (documentOffset + replacementLength);
            if (delta > 0)
                replacementLength += delta;

            if (trigger == (char) 0) {
                replace( document, documentOffset, replacementLength, replacementString );
            }
            else {
                StringBuffer buffer = new StringBuffer( replacementString );

                if ((replacementLength < buffer.length() && buffer.charAt( replacementLength ) != trigger)) {
                    buffer.insert( cursorPosition, trigger );
                    ++cursorPosition;
                }

                replace( document, documentOffset, replacementLength, buffer.toString() );
            }

            int oldLen = document.getLength();
            documentOffset += document.getLength() - oldLen;
        }
        catch (BadLocationException e) {
        	HibernateConsolePlugin.getDefault().log( e );
        }
    }

    /**
     * Replaces the document content at the specified offset and length with the
     * specified string.
     * 
     * @param document the document opened in the editor
     * @param offset offset to the document content to be replaced
     * @param length length of text to be replaced
     * @param string replacement string
     * @throws BadLocationException
     */
    private void replace( IDocument document, int offset, int length, String string ) throws BadLocationException {
        if (document != null && string != null && offset >= 0 && length >= 0) {
            if (!document.get( offset, length ).equals( string ))
                document.replace( offset, length, string );
        }
    }

    public void apply( IDocument document ) {
        apply( document, (char) 0, documentOffset + replacementLength );
    }

    public Point getSelection( IDocument document ) {
        return new Point( documentOffset + cursorPosition, 0 );
    }

    public IContextInformation getContextInformation() {
        return contextInformation;
    }

    public Image getImage() {
        return image;
    }

    public String getDisplayString() {
        return displayString;
    }

    public String getAdditionalProposalInfo() {
        return additionalProposalInfo;
    }

    public char[] getTriggerCharacters() {
        return triggerCharacters;
    }

    public int getContextInformationPosition() {
        return replacementOffset + contextInformationPosition;
    }

    public int getReplacementOffset() {
        return replacementOffset;
    }

    public void setReplacementOffset( int replacementOffset ) {

        if (replacementOffset >= 0) {
            this.replacementOffset = replacementOffset;
        }
    }

    public int getReplacementLength() {
        return replacementLength;
    }

    public void setReplacementLength( int replacementLength ) {

        if (replacementLength >= 0) {
            this.replacementLength = replacementLength;
        }
    }

    public String getReplacementString() {
        return replacementString;
    }

    public void setReplacementString( String replacementString ) {
        this.replacementString = replacementString;
    }

    public void setImage( Image image ) {
        this.image = image;
    }

    public boolean isValidFor( IDocument document, int offset ) {
        if (offset < replacementOffset)
            return false;

        int replacementLength = replacementString == null ? 0 : replacementString.length();
        if (offset >= replacementOffset + replacementLength)
            return false;

        try {
            int length = offset - replacementOffset;
            String start = document.get( replacementOffset, length );
            return replacementString.substring( 0, length ).equalsIgnoreCase( start );
        }
        catch (BadLocationException x) {
        }

        return false;
    }

}