package org.jboss.tools.hibernate.ui.view;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ImageBundle {
	private static final String BUNDLE_NAME = "org.jboss.tools.hibernate.ui.view.image"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private ImageBundle() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
