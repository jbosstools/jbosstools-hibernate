package org.hibernate.eclipse.console.test;

import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ide.IDE;
import org.hibernate.eclipse.console.utils.ProjectUtils;
import org.hibernate.eclipse.mapper.editors.HBMXMLContentAssistProcessor;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class HibernateProjectTests extends HibernateConsoleTest {

	public HibernateProjectTests(String name) {
		super( name ); 
	}
	
	public void testContentAssist() throws BackingStoreException, CoreException {
		
		HBMXMLContentAssistProcessor processor = new HBMXMLContentAssistProcessor();
		
		//IEditorPart iep = IDE.openEditor(getPage(), getProject().openFile("gpd.xml"));
		ITextViewer viewer = null;
		
		ICompletionProposal[] proposals = processor.computeCompletionProposals(viewer, 0);
	}
}