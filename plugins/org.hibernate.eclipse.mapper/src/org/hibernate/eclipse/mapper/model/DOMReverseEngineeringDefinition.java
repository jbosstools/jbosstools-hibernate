package org.hibernate.eclipse.mapper.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.sse.core.internal.provisional.IModelStateListener;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.hibernate.eclipse.console.model.IReverseEngineeringDefinition;
import org.hibernate.eclipse.console.model.ITableFilter;
import org.hibernate.eclipse.console.model.ITypeMapping;
import org.hibernate.eclipse.mapper.factory.ObserverAdapterFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DOMReverseEngineeringDefinition implements	IReverseEngineeringDefinition {

	private IModelStateListener listener = new IModelStateListener() {
	
		public void modelReinitialized(IStructuredModel structuredModel) {
			System.out.println("reinit" + structuredModel);	
		}
	
		public void modelAboutToBeReinitialized(IStructuredModel structuredModel) {
			System.out.println("about to be reinit" + structuredModel);	
		}
	
		public void modelResourceMoved(IStructuredModel oldModel,
				IStructuredModel newModel) {
			System.out.println("res moved" + oldModel);	
		}
	
		public void modelResourceDeleted(IStructuredModel model) {
			System.out.println("deleted" + model);	
		}
	
		public void modelDirtyStateChanged(IStructuredModel model, boolean isDirty) {
			System.out.println("dirty changed " + model + " to " + isDirty);
	
		}
	
		int cnt = 0;
		public void modelChanged(IStructuredModel model) {
			System.out.println("model changed" + cnt++);
			//pcs.firePropertyChange(null, null, null);
		}
	
		public void modelAboutToBeChanged(IStructuredModel model) {
			System.out.println("about to be changed" + cnt++);
	
		}
	
	};
	private final IDOMDocument document;
	private ObserverAdapterFactory factory;

	public DOMReverseEngineeringDefinition(IDOMDocument document) {
		this.document = document;
		factory = new ObserverAdapterFactory(this); 
		document.getModel().addModelStateListener(listener);
		factory.adapt(document);
	}

	public ITableFilter createTableFilter() {		
		return new TableFilterAdapter(document.createElement("table-filter"));
	}

	public void addTableFilter(ITableFilter filter) {
		if ( filter instanceof TableFilterAdapter ) {
			TableFilterAdapter tf = (TableFilterAdapter) filter;
			factory.adapt((INodeNotifier) tf.getNode());
			document.getDocumentElement().appendChild(tf.getNode());
			DOMModelUtil.formatNode(tf.getNode().getParentNode());
		}
		
	}

	PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	
	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		pcs.addPropertyChangeListener(pcl);
	}

	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		pcs.removePropertyChangeListener(pcl);
	}
	
	public void addPropertyChangeListener(String property, PropertyChangeListener pcl) {
		pcs.addPropertyChangeListener(property, pcl);		
	}

	public void removePropertyChangeListener(String property, PropertyChangeListener pcl) {
		pcs.removePropertyChangeListener(property, pcl);
	}


	public ITableFilter[] getTableFilters() {
		return (ITableFilter[]) getTableFiltersList().toArray(new ITableFilter[0]);
	}

	public void removeTableFilter(ITableFilter filter) {
		if ( filter instanceof TableFilterAdapter ) {
			TableFilterAdapter tf = (TableFilterAdapter) filter;
			Node parentNode = tf.getNode().getParentNode();
			Node previousSibling = tf.getNode().getPreviousSibling();
			if(DOMModelUtil.isWhiteSpace(previousSibling)) {
				parentNode.removeChild(previousSibling);
			}
			parentNode.removeChild(tf.getNode());
			DOMModelUtil.formatNode(parentNode);
		}
	}

	public void moveTableFilterDown(ITableFilter item) {
		if ( item instanceof TableFilterAdapter ) {
			TableFilterAdapter tfe = (TableFilterAdapter) item;
			Node nextSibling = DOMModelUtil.getNextNamedSibling( tfe.getNode(), "table-filter" );
			if(nextSibling!=null) {
				DOMModelUtil.addElementBefore(tfe.getNode().getParentNode(), nextSibling, tfe.getNode());			
			}
			pcs.firePropertyChange("tableFilters",null, null);
		}
	}

	public void moveTableFilterUp(ITableFilter item) {
		if ( item instanceof TableFilterAdapter ) {
			TableFilterAdapter tfe = (TableFilterAdapter) item;
			
			Node sibling = DOMModelUtil.getPreviousNamedSibling( tfe.getNode(), "table-filter");
			if(sibling!=null) {
				DOMModelUtil.addElementBefore(tfe.getNode().getParentNode(), tfe.getNode(), sibling);			
			}
			pcs.firePropertyChange("tableFilters",null, null);
		}
	}
	
	private List getTableFiltersList() {
		List result = new ArrayList();
		NodeList list = document.getDocumentElement().getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node item = list.item(i);
			if (item.getNodeName().equals("table-filter")) {
				factory.adapt((INodeNotifier) item);
				result.add(new TableFilterAdapter(item));
			}
		}
		return result;
	}

	public void unknownNotifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
		System.out.println("Unknown change: " + notifier + " " + INodeNotifier.EVENT_TYPE_STRINGS[eventType]);
		//pcs.firePropertyChange(null,null,null);		
	}

	public void hibernateMappingChanged() {
		pcs.firePropertyChange(TABLEFILTER_STRUCTURE,null,null);		
	}

	public void tableFilterChanged(INodeNotifier notifier) {
		pcs.firePropertyChange(TABLEFILTER_STRUCTURE,null,null); // could also be more specific but then need to map between node and TableFilter
	}

	public ITypeMapping[] getTypeMappings() {
		return (ITypeMapping[]) getTypeMappingsList().toArray(new ITypeMapping[0]);
	}

	private List getTypeMappingsList() {
		List result = new ArrayList();
		NodeList list = document.getDocumentElement().getElementsByTagName("type-mapping");
		for (int i = 0; i < list.getLength(); i++) {
			Element item = (Element) list.item(i);
			NodeList sqllist = item.getElementsByTagName("sql-type");
			for (int j = 0; j < sqllist.getLength(); j++) {
				Element item2 = (Element) sqllist.item(j);
				factory.adapt((INodeNotifier) item2);
				result.add(new TypeMappingAdapter(item2));
			}
		}
		return result;
	}

	public ITypeMapping createTypeMapping() {
		return new TypeMappingAdapter(document.createElement("sql-type"));
	}

	public void addTypeMapping(ITypeMapping typeMapping) {
		if ( typeMapping instanceof TypeMappingAdapter ) {
			TypeMappingAdapter tf = (TypeMappingAdapter) typeMapping;
			factory.adapt((INodeNotifier) tf.getNode());
			NodeList parentList = document.getDocumentElement().getElementsByTagName("type-mapping");
			Element parent;
			if(parentList.getLength()==0) {
				parent = document.createElement("type-mapping");
				Node firstChild = document.getDocumentElement().getFirstChild();
				if(firstChild==null) {
					parent = (Element) document.getDocumentElement().appendChild(parent);
				} else {
					parent = (Element) document.getDocumentElement().insertBefore(parent, firstChild);
				}
			} else {
				parent = (Element) parentList.item(0);
			}
			parent.appendChild(tf.getNode());
			DOMModelUtil.formatNode(tf.getNode().getParentNode());
		}		
	}

	public void typeMappingChanged(INodeNotifier notifier) {
		pcs.firePropertyChange(TYPEMAPPING_STRUCTURE, null,null);		
	}

	public void sqlTypeChanged(INodeNotifier notifier) {
		pcs.firePropertyChange(TYPEMAPPING_STRUCTURE, null,null);		
	}

	}
