package org.jboss.tools.hibernate.xml.model.handlers;

import java.util.Properties;

import org.jboss.tools.common.meta.XAttribute;
import org.jboss.tools.common.meta.action.impl.DefaultWizardDataValidator;
import org.jboss.tools.common.meta.action.impl.SpecialWizardSupport;
import org.jboss.tools.common.meta.action.impl.WizardDataValidator;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateHandler;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.project.ext.store.XMLStoreConstants;
import org.jboss.tools.hibernate.xml.model.impl.ComplexAttrUtil;

public class AddPropertySupport extends SpecialWizardSupport {

	public AddPropertySupport() {}

	public void action(String name) throws XModelException {
		if(FINISH.equals(name)) {
			execute();
			setFinished(true);
		} else if(CANCEL.equals(name)) {
			setFinished(true);
		}
	}
	
	public String[] getActionNames(int stepId) {
		return new String[]{FINISH, CANCEL};
	}

	void execute() throws XModelException {
		Properties p = extractStepData(0);
		String entity = getEntityData()[0].getModelEntity().getName();
		XModelObject o = getTarget().getModel().createModelObject(entity, p);
		DefaultCreateHandler.addCreatedObject(getTarget(), o, getProperties());
	}

    public WizardDataValidator getValidator(int step) {
    	validator.setSupport(this, step);
    	return validator;
    }

    Validator validator = new Validator();

	class Validator extends DefaultWizardDataValidator {
		public void validate(Properties data) {
			super.validate(data);
			String name = data.getProperty(XMLStoreConstants.ATTR_NAME);
			String value = data.getProperty(XMLStoreConstants.ATTR_VALUE);
			XAttribute attr = ComplexAttrUtil.findComplexAttr(getTarget(), name);
			if(attr != null) {
				String error = DefaultCreateHandler.getConstraintMessage(name, value, attr.getConstraint());
				if(error != null) {
					message = error;
				}
			}
		}
	}

}
