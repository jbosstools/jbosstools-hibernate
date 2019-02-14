package org.jboss.tools.hibernate.xml.model.impl;

public class HibClassImpl extends OrderedObject2Impl {
	private static final long serialVersionUID = 1L;

	public String getPresentationString() {
		String en = getAttributeValue("entity-name"); //$NON-NLS-1$
		if(en == null || en.length() == 0) {
			return super.getPresentationString();
		}
		return en;
	}

	public String getPathPart() {
		String en = getAttributeValue("entity-name"); //$NON-NLS-1$
		if(en == null || en.length() == 0) {
			return super.getPathPart();
		}
		
		return super.getPathPart() + ':' + en.replace('/', '#');
	}
}
