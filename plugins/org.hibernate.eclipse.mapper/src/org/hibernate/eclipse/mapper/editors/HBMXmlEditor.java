/*
 * Created on 16-Nov-2004
 *
 * 
 */
package org.hibernate.eclipse.mapper.editors;

import org.jboss.ide.eclipse.jdt.xml.ui.editors.XMLConfiguration;
import org.jboss.ide.eclipse.jdt.xml.ui.editors.XMLEditor;
import org.jboss.ide.eclipse.jdt.xml.ui.editors.XMLTextTools;

/**
 * @author max
 * 
 * 
 */
public class HBMXmlEditor extends XMLEditor {

	public HBMXmlEditor() {
		super();
	}

	/**
	 * Gets the xMLConfiguration attribute of the EjbXmlEditor object
	 * 
	 * @param xmlTextTools
	 *            Description of the Parameter
	 * @return The xMLConfiguration value
	 */
	protected XMLConfiguration getXMLConfiguration(XMLTextTools xmlTextTools) {
		return new HBMXmlSourceViewerConfiguration(xmlTextTools);
	}

}
