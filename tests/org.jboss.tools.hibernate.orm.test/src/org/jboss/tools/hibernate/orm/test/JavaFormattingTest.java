package org.jboss.tools.hibernate.orm.test;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.junit.Assert;
import org.junit.Test;

public class JavaFormattingTest {

	@Test
	public void testJavaFormatting() throws JavaModelException, MalformedTreeException, BadLocationException {
		CodeFormatter codeFormatter = ToolFactory.createCodeFormatter(null);
		String contents = "java.lang.String str; System.out.println();"; //$NON-NLS-1$
		IDocument doc = new Document(contents);
		TextEdit edit = codeFormatter.format(CodeFormatter.K_UNKNOWN, doc.get(), 0, doc.get().length(), 0, null);
		
		edit.apply(doc);
		String newcontents = doc.get();
		Assert.assertNotNull(newcontents);
	}

}
