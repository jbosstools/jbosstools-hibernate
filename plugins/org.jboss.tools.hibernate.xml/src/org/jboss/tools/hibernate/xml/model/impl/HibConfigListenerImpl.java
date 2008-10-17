package org.jboss.tools.hibernate.xml.model.impl;

public class HibConfigListenerImpl extends RegularObject2Impl {

	private static final long serialVersionUID = 1L;

	public HibConfigListenerImpl() {}

	public String getPathPart() {
		String pp = super.getPathPart();
		String type = getAttributeValue("type");
		if(type != null && type.length() > 0) {
			pp += ":" + type;
		}
		return pp;
	}

}
