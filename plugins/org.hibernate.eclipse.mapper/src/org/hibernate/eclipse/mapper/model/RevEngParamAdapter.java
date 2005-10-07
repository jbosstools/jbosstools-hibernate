package org.hibernate.eclipse.mapper.model;

import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.hibernate.eclipse.console.model.IRevEngParameter;
import org.w3c.dom.Node;

public class RevEngParamAdapter extends DOMAdapter implements IRevEngParameter {

	public RevEngParamAdapter(Node node, DOMReverseEngineeringDefinition revEngDef) {
		super( node, revEngDef );
	}

	public String getName() {
		return getNodeValue("name", "");
	}

	public String getValue() {
		return getNodeValue("value", "");		
	}

	public void notifyChanged(INodeNotifier notifier, int eventType,
			Object changedFeature, Object oldValue, Object newValue, int pos) {
		getModel().tablesChanged(notifier);
	}

	public void setName(String value) {
		setAttribute("name", value, "");		
	}

	public void setValue(String value) {
		setAttribute("value", value, "");		
	}

}
