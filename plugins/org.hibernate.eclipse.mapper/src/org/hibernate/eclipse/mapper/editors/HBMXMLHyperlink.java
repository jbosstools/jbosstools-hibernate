package org.hibernate.eclipse.mapper.editors;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;

public class HBMXMLHyperlink implements IHyperlink {

	private final IRegion region;
	private final IJavaProject project;
	private IEditorPart fEditor; // used for opening
	private IJavaElement element;

	public HBMXMLHyperlink(IRegion region, IJavaElement element, IJavaProject project) {
		assert (element!=null);
		this.region = region;
		this.element = element;
		this.project = project;
	}

	public IRegion getHyperlinkRegion() {
		return region;
	}

	public String getTypeLabel() {
		return null;
	}

	public String getHyperlinkText() {
		return null;
	}

	public void open() {
		try {
			IEditorPart part = EditorUtility.openInEditor(element, true);
			if(part!=null) {
				EditorUtility.revealInEditor(part, element);
			}
		} catch (JavaModelException e) {
			// ignore...TODO?	
			e.printStackTrace();
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String toString() {
		return "HBMXML hyperlink: " + element; 
	}

	
}
