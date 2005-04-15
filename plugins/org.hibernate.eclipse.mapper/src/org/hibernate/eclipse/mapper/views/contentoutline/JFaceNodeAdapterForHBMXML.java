package org.hibernate.eclipse.mapper.views.contentoutline;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.eclipse.wst.sse.core.INodeAdapterFactory;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.sse.ui.internal.contentoutline.IJFaceNodeAdapter;
import org.eclipse.wst.xml.ui.internal.XMLUIMessages;
import org.eclipse.wst.xml.ui.internal.XMLUIPlugin;
import org.eclipse.wst.xml.ui.internal.contentoutline.BufferedOutlineUpdater;
import org.eclipse.wst.xml.ui.internal.contentoutline.JFaceNodeAdapter;
import org.eclipse.wst.xml.ui.internal.contentoutline.JFaceNodeAdapterFactory;
import org.eclipse.wst.xml.ui.internal.contentoutline.RefreshPropertySheetJob;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;


/**
 * For hbm.xml files.
 * 
 * Adapts a DOM node to a JFace viewer.
 */
public class JFaceNodeAdapterForHBMXML extends JFaceNodeAdapter {
	final static Class ADAPTER_KEY = IJFaceNodeAdapter.class;
	private BufferedOutlineUpdater fUpdater;

	public JFaceNodeAdapterForHBMXML(INodeAdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	

	/**
	 * Fetches the label text specific to this object instance.
	 */
	public String getLabelText(Object object) {
		String result = getNodeName(object);
		Node node = (Node) object;		
		NamedNodeMap attributes = node.getAttributes();
		if(attributes!=null) {
			Node firstAttribute = attributes.item(0);
			if(firstAttribute!=null) {
				return result + " " + firstAttribute.getNodeName() + "=\"" + firstAttribute.getNodeValue() + "\"";
			} 
		} 
		
		return result;
	}

	private String getNodeName(Object object) {
		Node node = (Node) object;
		String nodeName = node.getNodeName();
		if ("cheatsheet".equals(nodeName)) {
			Node titleNode = node.getAttributes().getNamedItem("title");
			String title = "";
			if (titleNode != null) {
				title = titleNode.getNodeValue();
			}
			return "XXX Title:" + title;
		}
		if ("item".equals(nodeName)) {
			Node titleNode = node.getAttributes().getNamedItem("title");
			String title = "";
			if (titleNode != null) {
				title = titleNode.getNodeValue();
			}
			return "Funky Title:" + title;
		}
		if ("action".equals(nodeName)) {
			Node classNode = node.getAttributes().getNamedItem("class");
			String className = "";
			if (classNode != null) {
				className = classNode.getNodeValue();
				int index = className.lastIndexOf(".");
				
				if (index != -1) {
					className = className.substring(index + 1);
				}
			}
			return "Action:" + className;
		}
		return nodeName;
	}

	public Object getParent(Object object) {

		Node node = (Node) object;
		return node.getParentNode();
	}

	public boolean hasChildren(Object object) {
		Node node = (Node) object;
		if ("description".equals(node.getNodeName())) {
			return false;
		}
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child.getNodeType() != Node.TEXT_NODE)
				return true;
		}
		return false;
	}

	/**
	 * Allowing the INodeAdapter to compare itself against the type allows it
	 * to return true in more than one case.
	 */
	public boolean isAdapterForType(Object type) {
		return type.equals(ADAPTER_KEY);
	}

	/** only needed to provide better notifychanged operation */
	private BufferedOutlineUpdater getOutlineUpdater() {
		if (fUpdater == null)
			fUpdater = new BufferedOutlineUpdater();
		return fUpdater;
	}
	
	/** only needed to provide better notifychanged operation */
	Display getDisplay() {

		// Note: the workbench should always have a display
		// (unless running headless), whereas Display.getCurrent()
		// only returns the display if the currently executing thread
		// has one.
		if (PlatformUI.isWorkbenchRunning())
			return PlatformUI.getWorkbench().getDisplay();
		else
			return null;
	}

	/**
	 * Called by the object being adapter (the notifier) when something has
	 * changed.
	 */
	public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {

		// future_TODO: the 'uijobs' used in this method were added to solve
		// threading problems when the dom
		// is updated in the background while the editor is open. They may be
		// a bit overkill and not that useful.
		// (That is, may be be worthy of job manager management). If they are
		// found to be important enough to leave in,
		// there's probably some optimization that can be done.
		Collection listeners = ((JFaceNodeAdapterFactory) adapterFactory).getListeners();
		Iterator iterator = listeners.iterator();

		while (iterator.hasNext()) {
			Object listener = iterator.next();			
			if (notifier instanceof Node && (listener instanceof StructuredViewer) && (eventType == INodeNotifier.STRUCTURE_CHANGED || (eventType == INodeNotifier.CHANGE /*&& changedFeature == null*/))) {

					System.out.println("JFaceNodeAdapter notified on event type > " + eventType + " at " + changedFeature);

				// refresh on structural and "unknown" changes
				StructuredViewer structuredViewer = (StructuredViewer) listener;
				// https://w3.opensource.ibm.com/bugzilla/show_bug.cgi?id=5230
				if (structuredViewer.getControl() != null /*
														   * &&
														   * structuredViewer.getControl().isVisible()
														   */)
					getOutlineUpdater().processNode(structuredViewer, (Node) notifier);
			} else if ((listener instanceof PropertySheetPage) && ((eventType == INodeNotifier.CHANGE) || (eventType == INodeNotifier.STRUCTURE_CHANGED))) {
				PropertySheetPage propertySheetPage = (PropertySheetPage) listener;
				if (propertySheetPage.getControl() != null /*
														    * &&
														    * !propertySheetPage.getControl().isDisposed()
														    */) {
					RefreshPropertySheetJob refreshPropertySheetJob = new RefreshPropertySheetJob(getDisplay(), XMLUIMessages.JFaceNodeAdapter_1, propertySheetPage); //$NON-NLS-1$
					refreshPropertySheetJob.schedule();
				}
			}
		}
	}
}
