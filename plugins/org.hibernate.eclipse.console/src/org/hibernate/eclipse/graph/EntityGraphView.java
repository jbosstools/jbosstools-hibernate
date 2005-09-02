package org.hibernate.eclipse.graph;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.part.ViewPart;
import org.hibernate.cfg.Configuration;
import org.hibernate.eclipse.console.ConsolePreferencesConstants;
import org.hibernate.eclipse.console.HibernateConsolePerspectiveFactory;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.views.KnownConfigurationsView;
import org.hibernate.eclipse.graph.model.ConfigurationViewAdapter;
import org.hibernate.eclipse.graph.parts.ConfigurationEditPart;

public class EntityGraphView extends ViewPart {

	private ISelectionListener listener = new ISelectionListener() {
	
		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			if (!(selection instanceof IStructuredSelection))
	               return;
	            IStructuredSelection ss = (IStructuredSelection) selection;
	            Object o = ss.getFirstElement();
	            if (o instanceof Configuration) {
	            	viewer.setContents(new ConfigurationViewAdapter((Configuration) o));
	            	boolean b = HibernateConsolePlugin.getDefault().getPluginPreferences().getBoolean(ConsolePreferencesConstants.ENTITY_MODEL_LAYOUT);
					((ConfigurationEditPart)viewer.getContents()).setManualLayoutActive(b);
	            }	               
		}
	
	};
	private ScrollingGraphicalViewer viewer;
	private EditDomain editDomain;
	private ActionGroup actionGroup;

	public EntityGraphView() {
		super();		
	}

	public void init(IViewSite site) throws PartInitException {
		super.init( site );
		
		initEditDomain();		
	}
	
	private void initEditDomain() {
		editDomain = new EditDomain();		
	}

	public void createPartControl(Composite parent) {
		  viewer = new ScrollingGraphicalViewer();
		  viewer.createControl(parent);
		  viewer.setRootEditPart(new ScalableFreeformRootEditPart());
		  viewer.getControl().setBackground(ColorConstants.white);
		  viewer.setEditPartFactory(new HibernateConfigurationPartFactory());
		  
		  editDomain.addViewer(viewer);
		  
		  getSite().getPage().addSelectionListener(KnownConfigurationsView.ID, listener);
		  
		  initActions();
	}

	private void initActions() {
		this.actionGroup = new EntityGraphViewActionGroup(this);
        
        IActionBars actionBars = getViewSite().getActionBars();
        this.actionGroup.fillActionBars(actionBars);
		
	}

	public void setFocus() {

	}

	public void dispose() {
		  getSite().getPage().removeSelectionListener(KnownConfigurationsView.ID, listener);
	}

	public ConfigurationViewAdapter getConfigurationViewAdapter() {
		ConfigurationEditPart cp = (ConfigurationEditPart) viewer.getContents();
		
		if(cp!=null) {
			return cp.getConfigurationViewAdapter();
		} else {
			return null;
		}
	}

	public void setManualLayout(boolean value) {
		ConfigurationEditPart cp = (ConfigurationEditPart) viewer.getContents();
		if(cp!=null) {
			cp.setManualLayoutActive(value);
		}
	}

}
	
	
