package org.hibernate.eclipse.mapper.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.sse.core.internal.provisional.IModelStateListener;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.hibernate.eclipse.console.model.IRevEngTable;
import org.hibernate.eclipse.console.model.IReverseEngineeringDefinition;
import org.hibernate.eclipse.console.model.ITableFilter;
import org.hibernate.eclipse.console.model.ITypeMapping;
import org.hibernate.eclipse.mapper.factory.ObserverAdapterFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DOMReverseEngineeringDefinition implements	IReverseEngineeringDefinition {

	private IModelStateListener listener = new IModelStateListener() {
	
		public void modelReinitialized(IStructuredModel structuredModel) {
			//System.out.println("reinit" + structuredModel);	
		}
	
		public void modelAboutToBeReinitialized(IStructuredModel structuredModel) {
			//System.out.println("about to be reinit" + structuredModel);	
		}
	
		public void modelResourceMoved(IStructuredModel oldModel,
				IStructuredModel newModel) {
			//System.out.println("res moved" + oldModel);	
		}
	
		public void modelResourceDeleted(IStructuredModel model) {
			//System.out.println("deleted" + model);	
		}
	
		public void modelDirtyStateChanged(IStructuredModel model, boolean isDirty) {
			//System.out.println("dirty changed " + model + " to " + isDirty);
	
		}
	
		int cnt = 0;
		public void modelChanged(IStructuredModel model) {
			//System.out.println("model changed" + cnt++);
			//pcs.firePropertyChange(null, null, null);
		}
	
		public void modelAboutToBeChanged(IStructuredModel model) {
			//System.out.println("about to be changed" + cnt++);
	
		}
	
	};
	
	private ObserverAdapterFactory factory;

	private IDOMDocument document;

	public DOMReverseEngineeringDefinition(IDOMDocument document) {
		this.document = document;
		factory = new ObserverAdapterFactory(this); 
		
		document.getModel().addModelStateListener(listener);
		factory.adapt(document);
	}

	public ITableFilter createTableFilter() {		
		return (ITableFilter) factory.adapt((INodeNotifier) getDocument().createElement("table-filter"));
	}

	public void addTableFilter(ITableFilter filter) {
		if ( filter instanceof TableFilterAdapter ) {
			TableFilterAdapter tf = (TableFilterAdapter) filter;
			factory.adapt((INodeNotifier) tf.getNode());
			
			NodeList lastChild = getDocument().getDocumentElement().getElementsByTagName("table-filter");
			if(lastChild==null || lastChild.getLength()==0) {
				NodeList typeMapping = getDocument().getDocumentElement().getElementsByTagName("type-mapping");
				if(typeMapping==null || typeMapping.getLength()==0) {
					getDocument().getDocumentElement().appendChild(tf.getNode());
				} else {
					Element e = (Element) typeMapping.item(typeMapping.getLength()-1);
					getDocument().getDocumentElement().insertBefore(tf.getNode(), e.getNextSibling());
				}
			}  else {
				Element e = (Element) lastChild.item(lastChild.getLength()-1);
				getDocument().getDocumentElement().insertBefore(tf.getNode(), e.getNextSibling());			
			}
			
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
			pcs.firePropertyChange(TABLEFILTER_STRUCTURE,null, null);
		}
	}

	public void moveTableFilterUp(ITableFilter item) {
		if ( item instanceof TableFilterAdapter ) {
			TableFilterAdapter tfe = (TableFilterAdapter) item;
			
			Node sibling = DOMModelUtil.getPreviousNamedSibling( tfe.getNode(), "table-filter");
			if(sibling!=null) {
				DOMModelUtil.addElementBefore(tfe.getNode().getParentNode(), tfe.getNode(), sibling);			
			}
			pcs.firePropertyChange(TABLEFILTER_STRUCTURE,null, null);
		}
	}
	
	private List getTableFiltersList() {
		return DOMModelUtil.getAdaptedElements(getDocument().getDocumentElement(), "table-filter", factory);
	}

	public void unknownNotifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
		System.out.println("Unknown change: " + notifier + " " + INodeNotifier.EVENT_TYPE_STRINGS[eventType]);
	}

	public void hibernateMappingChanged() {
		pcs.firePropertyChange(TABLEFILTER_STRUCTURE,null,null);		
		pcs.firePropertyChange(TYPEMAPPING_STRUCTURE,null,null);
		pcs.firePropertyChange(TABLES_STRUCTURE,null,null);
	}

	public void tableFilterChanged(INodeNotifier notifier) {
		pcs.firePropertyChange(TABLEFILTER_STRUCTURE,null,null); // could also be more specific but then need to map between node and TableFilter
	}

	public ITypeMapping[] getTypeMappings() {
		return (ITypeMapping[]) getTypeMappingsList().toArray(new ITypeMapping[0]);
	}

	private List getTypeMappingsList() {
		List result = new ArrayList();
		NodeList list = getDocument().getDocumentElement().getElementsByTagName("type-mapping");
		for (int i = 0; i < list.getLength(); i++) {
			Element item = (Element) list.item(i);
			NodeList sqllist = item.getElementsByTagName("sql-type");
			for (int j = 0; j < sqllist.getLength(); j++) {
				Element item2 = (Element) sqllist.item(j);
				result.add(factory.adapt((INodeNotifier) item2));
			}
		}
		return result;
	}

	public ITypeMapping createTypeMapping() {
		return (ITypeMapping) factory.adapt((INodeNotifier)getDocument().createElement("sql-type"));
	}

	public void addTypeMapping(ITypeMapping typeMapping) {
		if ( typeMapping instanceof TypeMappingAdapter ) {
			TypeMappingAdapter tf = (TypeMappingAdapter) typeMapping;
			
			NodeList parentList = getDocument().getDocumentElement().getElementsByTagName("type-mapping");
			Element parent;
			if(parentList.getLength()==0) {
				parent = getDocument().createElement("type-mapping");
				Node firstChild = getDocument().getDocumentElement().getFirstChild();
				if(firstChild==null) {
					parent = (Element) getDocument().getDocumentElement().appendChild(parent);
				} else {
					parent = (Element) getDocument().getDocumentElement().insertBefore(parent, firstChild);
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

	public void moveTypeMappingDown(ITypeMapping item) {
		if ( item instanceof TypeMappingAdapter ) {
			TypeMappingAdapter tfe = (TypeMappingAdapter) item;
			Node nextSibling = DOMModelUtil.getNextNamedSibling( tfe.getNode(), "sql-type" );
			if(nextSibling!=null) {
				DOMModelUtil.addElementBefore(tfe.getNode().getParentNode(), nextSibling, tfe.getNode());			
			}			
		}		
	}

	public void moveTypeMappingUp(ITypeMapping item) {
		if ( item instanceof TypeMappingAdapter ) {
			TypeMappingAdapter tfe = (TypeMappingAdapter) item;
			
			Node sibling = DOMModelUtil.getPreviousNamedSibling( tfe.getNode(), "sql-type");
			if(sibling!=null) {
				DOMModelUtil.addElementBefore(tfe.getNode().getParentNode(), tfe.getNode(), sibling);			
			}
		}
	}

	public void removeTypeMapping(ITypeMapping item) {
		if ( item instanceof TypeMappingAdapter) {
			TypeMappingAdapter tf = (TypeMappingAdapter) item;
			Node parentNode = tf.getNode().getParentNode();
			Node previousSibling = tf.getNode().getPreviousSibling();
			if(DOMModelUtil.isWhiteSpace(previousSibling)) {
				parentNode.removeChild(previousSibling);
			}
			parentNode.removeChild(tf.getNode());
			DOMModelUtil.formatNode(parentNode);
		}
	}

	public IRevEngTable[] getTables() {
		return (IRevEngTable[]) getTablesList().toArray(new IRevEngTable[0]);
	}

	private List getTablesList() {
		return DOMModelUtil.getAdaptedElements(getDocument().getDocumentElement(), "table",factory);		
	}

	public void tablesChanged(INodeNotifier notifier) {
		pcs.firePropertyChange(TABLES_STRUCTURE, null,null);		
	}

	public INodeAdapterFactory getNodeFactory() {
		return factory;
	}

	private Document getDocument() {
		return document;
	}

	public IRevEngTable createTable() {
		return (IRevEngTable) factory.adapt((INodeNotifier) getDocument().createElement("table"));
	}

	public void addTable(IRevEngTable retable) {
		if ( retable instanceof RevEngTableAdapter ) {
			RevEngTableAdapter tf = (RevEngTableAdapter) retable;
			
			getDocument().getDocumentElement().appendChild(tf.getNode());
			
			DOMModelUtil.formatNode(tf.getNode().getParentNode());
		}	
	}

	
}
