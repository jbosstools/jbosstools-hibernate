package org.hibernate.eclipse.mapper.model;

import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.hibernate.eclipse.console.model.IRevEngGenerator;
import org.hibernate.eclipse.console.model.IRevEngParameter;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class RevEngGeneratorAdapter extends DOMAdapter implements
		IRevEngGenerator {

	public RevEngGeneratorAdapter(Node node, DOMReverseEngineeringDefinition revEngDef) {
		super( node, revEngDef );
	}

	public String getGeneratorClassName() {
		return getNodeValue("class", "");
	}

	public IRevEngParameter[] getParameters() {
		return (IRevEngParameter[]) getAdaptedElements((Element) getNode(), "param").toArray(new IRevEngParameter[0]);
	}

	public void notifyChanged(INodeNotifier notifier, int eventType,
			Object changedFeature, Object oldValue, Object newValue, int pos) {
		getModel().tablesChanged(notifier);
	}

	public void setGeneratorClassName(String value) {
		setAttribute("class", value, null);		
	}

}
