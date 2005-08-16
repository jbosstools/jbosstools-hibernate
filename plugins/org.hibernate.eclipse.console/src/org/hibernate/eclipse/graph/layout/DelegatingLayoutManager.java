package org.hibernate.eclipse.graph.layout;

import java.util.Map;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayoutManager;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.hibernate.eclipse.graph.model.ConfigurationViewAdapter;
import org.hibernate.eclipse.graph.parts.ConfigurationEditPart;
import org.hibernate.eclipse.graph.policy.ConfigurationLayoutEditPolicy;

/**
 * Used to delegate between the GraphyLayoutManager and the GraphXYLayout
 * classes
 * 
 * @author Phil Zoio
 */
public class DelegatingLayoutManager implements LayoutManager {

	private ConfigurationEditPart diagram;

	private LayoutManager activeLayoutManager;

	private GraphLayoutManager graphLayoutManager;

	private GraphXYLayout xyLayoutManager;

	public DelegatingLayoutManager(ConfigurationEditPart diagram) {
		this.diagram = diagram;
		this.graphLayoutManager = new GraphLayoutManager( diagram );
		this.xyLayoutManager = new GraphXYLayout( diagram );

		// use the graph layout manager as the initial delegate
		this.activeLayoutManager = this.graphLayoutManager;
	}

	// ********************* layout manager methods methods
	// ****************************/

	public void layout(IFigure container) {

		ConfigurationViewAdapter schema = diagram.getConfigurationViewAdapter();

		if ( schema.isManualLayoutDesired() ) {

			if ( activeLayoutManager != xyLayoutManager ) {

				// yes we are okay to start populating the table bounds
				setLayoutManager( container, xyLayoutManager );
				activeLayoutManager.layout( container );
			}
			else {
				setLayoutManager( container, xyLayoutManager );
				activeLayoutManager.layout( container );
			}
		}
		else {
			setLayoutManager( container, graphLayoutManager );
			activeLayoutManager.layout( container );
		}

	}

	public Object getConstraint(IFigure child) {
		return activeLayoutManager.getConstraint( child );
	}

	public Dimension getMinimumSize(IFigure container, int wHint, int hHint) {
		return activeLayoutManager.getMinimumSize( container, wHint, hHint );
	}

	public Dimension getPreferredSize(IFigure container, int wHint, int hHint) {
		return activeLayoutManager.getPreferredSize( container, wHint, hHint );
	}

	public void invalidate() {
		activeLayoutManager.invalidate();
	}

	public void remove(IFigure child) {
		activeLayoutManager.remove( child );
	}

	public void setConstraint(IFigure child, Object constraint) {
		activeLayoutManager.setConstraint( child, constraint );
	}

	public void setXYLayoutConstraint(IFigure child, Rectangle constraint) {
		xyLayoutManager.setConstraint( child, constraint );
	}

	// ********************* protected and private methods
	// ****************************/

	/**
	 * Sets the current active layout manager
	 */
	private void setLayoutManager(IFigure container, LayoutManager layoutManager) {
		container.setLayoutManager( layoutManager );
		this.activeLayoutManager = layoutManager;
		if ( layoutManager == xyLayoutManager ) {
			diagram.installEditPolicy( EditPolicy.LAYOUT_ROLE,
					new ConfigurationLayoutEditPolicy() );
		}
		else {
			diagram.installEditPolicy( EditPolicy.LAYOUT_ROLE, null );
		}
	}

	public LayoutManager getActiveLayoutManager() {
		return activeLayoutManager;
	}

}