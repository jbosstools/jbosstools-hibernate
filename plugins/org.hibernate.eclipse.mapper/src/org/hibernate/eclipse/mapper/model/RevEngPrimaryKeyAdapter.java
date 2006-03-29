package org.hibernate.eclipse.mapper.model;

import java.util.List;

import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.hibernate.eclipse.console.model.IRevEngColumn;
import org.hibernate.eclipse.console.model.IRevEngGenerator;
import org.hibernate.eclipse.console.model.IRevEngPrimaryKey;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class RevEngPrimaryKeyAdapter extends DOMAdapter implements
		IRevEngPrimaryKey {

	public RevEngPrimaryKeyAdapter(Node node, DOMReverseEngineeringDefinition revEngDef) {
		super( node, revEngDef );
	}

	public IRevEngGenerator getGenerator() {
		List adaptedElements = getAdaptedElements((Element) getNode(), "generator");
		if(adaptedElements.isEmpty()) {
			return null;
		} else {
			return (IRevEngGenerator) adaptedElements.get(0);
		}
	}

	public IRevEngColumn[] getColumns() {
		return (IRevEngColumn[]) getAdaptedElements((Element) getNode(), "key-column").toArray(new IRevEngColumn[0]);
	}

	public void notifyChanged(INodeNotifier notifier, int eventType,
			Object changedFeature, Object oldValue, Object newValue, int pos) {
		getModel().tablesChanged(notifier);
	}

	public void addGenerator() {
		RevEngGeneratorAdapter key = (RevEngGeneratorAdapter) getModel().createGenerator();
		key.setGeneratorClassName("assigned");
		getNode().insertBefore(key.getNode(), getNode().getFirstChild());
		DOMModelUtil.formatNode(getNode().getParentNode());
	}

	public void addColumn() {
		RevEngColumnAdapter key = (RevEngColumnAdapter) getModel().createKeyColumn();
		key.setName("column_" + (getColumns().length+1));
		getNode().appendChild(key.getNode());
		DOMModelUtil.formatNode(getNode().getParentNode());
	}

}
