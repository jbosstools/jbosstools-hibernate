package org.hibernate.eclipse.console.views.navigator;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.navigator.CommonNavigator;
import org.hibernate.console.KnownConfigurations;

public class KnownConfigurationsNaviagtor extends CommonNavigator {

	protected IAdaptable getInitialInput() {
		return new AdaptableWrapper(new String());
	}
	
	public void createPartControl(Composite aParent) {
		super.createPartControl( aParent );
		getCommonViewer().setInput(KnownConfigurations.getInstance());
	}
}
