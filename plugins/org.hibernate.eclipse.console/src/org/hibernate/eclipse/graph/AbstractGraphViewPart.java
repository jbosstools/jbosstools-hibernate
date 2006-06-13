package org.hibernate.eclipse.graph;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
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
import org.hibernate.eclipse.console.ConsolePreferencesConstants;
import org.hibernate.eclipse.graph.model.ConfigurationViewAdapter;
import org.hibernate.eclipse.graph.parts.ConfigurationEditPart;

public abstract class AbstractGraphViewPart extends ViewPart {

	protected ISelectionListener listener = new ISelectionListener() {
		
			public void selectionChanged(IWorkbenchPart part, ISelection selection) {				
				if (!(selection instanceof IStructuredSelection))
		               return;
				
		            AbstractGraphViewPart.this.selectionChanged( (IStructuredSelection)selection );	               
			}
		
		};
		
	protected ScrollingGraphicalViewer viewer;
	protected EditDomain editDomain;
	private ActionGroup actionGroup;

	public void createPartControl(Composite parent) {
		  viewer = new ScrollingGraphicalViewer();
		  viewer.createControl(parent);
		  viewer.setRootEditPart(new ScalableFreeformRootEditPart());
		  viewer.getControl().setBackground(ColorConstants.white);
		  viewer.setEditPartFactory(createEditPartFactory());
		  
		  editDomain.addViewer(viewer);
		  getSite().setSelectionProvider(viewer);
		  
		  setupListener();
		  initActions();
	}

	protected HibernateConfigurationPartFactory createEditPartFactory() {
		return new HibernateConfigurationPartFactory();
	}

	abstract protected void setupListener();

	private void initActions() {
		ScalableFreeformRootEditPart root = ((ScalableFreeformRootEditPart)viewer.getRootEditPart());
	    
		this.actionGroup = new GraphViewActionGroup(this, ConsolePreferencesConstants.ENTITY_MODEL_LAYOUT, root);
	    
	    IActionBars actionBars = getViewSite().getActionBars();
	    this.actionGroup.fillActionBars(actionBars);
	    
	    List zoomLevels = new ArrayList(3);
	    zoomLevels.add(ZoomManager.FIT_ALL);
	    zoomLevels.add(ZoomManager.FIT_WIDTH);
	    zoomLevels.add(ZoomManager.FIT_HEIGHT);
	    root.getZoomManager().setZoomLevelContributions(zoomLevels);
	    root.getZoomManager().setZoomLevels( new double[] {.067, .125, .25, .5, .75, 1.0, 1.5, 2.0, 2.5, 3, 4} );
	    
	}

	public void init(IViewSite site) throws PartInitException {
		super.init( site );
		
		initEditDomain();		
	}

	private void initEditDomain() {
		editDomain = new EditDomain();		
	}

	public void setFocus() {
	
	}

	public void dispose() {
		  disposeListeners();
	}

	abstract protected void disposeListeners();

	public void setManualLayout(boolean value) {
		ConfigurationEditPart cp = (ConfigurationEditPart) viewer.getContents();
		if(cp!=null) {
			cp.setManualLayoutActive(value);
		}
	}

	public Object getAdapter(Class adapter) {
		if(adapter.equals(GraphicalViewer.class)) {
			return (GraphicalViewer)viewer;
		}
		return super.getAdapter( adapter );
	}

	abstract protected void selectionChanged(IStructuredSelection selection);
	
	public ConfigurationViewAdapter getConfigurationViewAdapter() {
		ConfigurationEditPart cp = (ConfigurationEditPart) viewer.getContents();
		
		if(cp!=null) {
			return cp.getConfigurationViewAdapter();
		} else {
			return null;
		}
	}

}
