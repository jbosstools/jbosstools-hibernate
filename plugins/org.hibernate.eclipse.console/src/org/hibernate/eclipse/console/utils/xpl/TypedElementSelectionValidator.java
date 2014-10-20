package org.hibernate.eclipse.console.utils.xpl;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.hibernate.eclipse.console.HibernateConsolePlugin;

public class TypedElementSelectionValidator implements ISelectionStatusValidator {

	private IStatus errorStatus= new Status(IStatus.ERROR, HibernateConsolePlugin.ID, ""); //$NON-NLS-1$
	private IStatus okStatus= new Status(IStatus.OK, HibernateConsolePlugin.ID, ""); //$NON-NLS-1$

	private Class<?>[] fAcceptedTypes;

	public TypedElementSelectionValidator(Class<?>[] acceptedTypes, boolean allowMultipleSelection) {
		Assert.isNotNull(acceptedTypes);
		fAcceptedTypes= acceptedTypes;
	}

	public IStatus validate(Object[] elements) {
		if (isValid(elements)) {
			return okStatus;
		}
		return errorStatus;
	}

	private boolean isOfAcceptedType(Object o) {
		for (int i= 0; i < fAcceptedTypes.length; i++) {
			if (fAcceptedTypes[i].isInstance(o)) {
				return true;
			}
		}
		return false;
	}

	protected boolean isSelectedValid(Object elem) {
		return true;
	}

	private boolean isValid(Object[] selection) {
		if (selection.length == 0) {
			return false;
		}
		for (int i= 0; i < selection.length; i++) {
			Object o= selection[i];
			if (!isOfAcceptedType(o) || !isSelectedValid(o)) {
				return false;
			}
		}
		return true;
	}
}
