/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.hibernate.eclipse.hqleditor;

import org.eclipse.jdt.core.CompletionProposal;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.ui.text.java.CompletionProposalCollector;

/**
 * @author max
 *
 */
public class HibernateResultCollector extends CompletionProposalCollector {

    public HibernateResultCollector(ICompilationUnit cu) {
		super(cu);			
	}
	
	public HibernateResultCollector(IJavaProject project) {
		super(project);		
	}

	public static class Settings {
        private boolean acceptTypes = false;
        private boolean acceptPackages = false;
        private boolean acceptClasses = false;
        private boolean acceptInterfaces = false;
        private boolean acceptFields = false;
        private boolean acceptMethods = false;        
        
        
        /**
         * @return Returns the acceptClasses.
         */
        public boolean isAcceptClasses() {
            return acceptClasses;
        }
        /**
         * @param acceptClasses The acceptClasses to set.
         */
        public void setAcceptClasses(boolean acceptClasses) {
            this.acceptClasses = acceptClasses;
        }
        /**
         * @return Returns the acceptFields.
         */
        public boolean isAcceptFields() {
            return acceptFields;
        }
        /**
         * @param acceptFields The acceptFields to set.
         */
        public void setAcceptFields(boolean acceptFields) {
            this.acceptFields = acceptFields;
        }
        /**
         * @return Returns the acceptInterfaces.
         */
        public boolean isAcceptInterfaces() {
            return acceptInterfaces;
        }
        /**
         * @param acceptInterfaces The acceptInterfaces to set.
         */
        public void setAcceptInterfaces(boolean acceptInterfaces) {
            this.acceptInterfaces = acceptInterfaces;
        }
        /**
         * @return Returns the acceptMethods.
         */
        public boolean isAcceptMethods() {
            return acceptMethods;
        }
        /**
         * @param acceptMethods The acceptMethods to set.
         */
        public void setAcceptMethods(boolean acceptMethods) {
            this.acceptMethods = acceptMethods;
        }
        /**
         * @return Returns the acceptPackages.
         */
        public boolean isAcceptPackages() {
            return acceptPackages;
        }
        /**
         * @param acceptPackages The acceptPackages to set.
         */
        public void setAcceptPackages(boolean acceptPackages) {
            this.acceptPackages = acceptPackages;
        }
        /**
         * @return Returns the acceptTypes.
         */
        public boolean isAcceptTypes() {
            return acceptTypes;
        }
        /**
         * @param acceptTypes The acceptTypes to set.
         */
        public void setAcceptTypes(boolean acceptTypes) {
            this.acceptTypes = acceptTypes;
        }
    }
    
    Settings settings;
	public void setAccepts(boolean types, boolean packages, boolean classes, boolean interfaces, boolean fields, boolean methods) {
        settings = new Settings();
        settings.acceptTypes = types;
        settings.acceptPackages = packages;
        settings.acceptClasses = classes;
        settings.acceptInterfaces = interfaces;
        settings.acceptFields = fields;
        settings.acceptMethods = methods;
		
		setIgnored(CompletionProposal.ANONYMOUS_CLASS_DECLARATION,true);
		setIgnored(CompletionProposal.FIELD_REF,!isAcceptFields() );
		setIgnored(CompletionProposal.KEYWORD, true);
		setIgnored(CompletionProposal.LABEL_REF, true);
		setIgnored(CompletionProposal.LOCAL_VARIABLE_REF,true);
		setIgnored(CompletionProposal.METHOD_DECLARATION,true);
		setIgnored(CompletionProposal.METHOD_NAME_REFERENCE,true); // maybe true to handle properties ?
		setIgnored(CompletionProposal.METHOD_REF,true); // maybe true to handle properties ?
		setIgnored(CompletionProposal.PACKAGE_REF,!isAcceptPackages() );
		setIgnored(CompletionProposal.POTENTIAL_METHOD_DECLARATION,true);
		setIgnored(CompletionProposal.TYPE_REF,!(isAcceptClasses() || isAcceptInterfaces() ) ); // decide during completion if accepted truly
		setIgnored(CompletionProposal.VARIABLE_DECLARATION, true);
	}
	
    public void setAccepts(Settings settings) {
        setAccepts(settings.acceptTypes,settings.acceptPackages,settings.acceptClasses,settings.acceptInterfaces, settings.acceptFields, settings.acceptMethods);
    }
    	
	/**
	 * @return Returns the acceptClasses.
	 */
	public boolean isAcceptClasses() {
		return settings.acceptClasses;
	}
	/**
	 * @param acceptClasses The acceptClasses to set.
	 */
	public void setAcceptClasses(boolean acceptClasses) {
		settings.acceptClasses = acceptClasses;
	}
	/**
	 * @return settings.Returns the acceptFields.
	 */
	public boolean isAcceptFields() {
		return settings.acceptFields;
	}
	/**
	 * @param acceptFields The acceptFields to set.
	 */
	public void setAcceptFields(boolean acceptFields) {
		settings.acceptFields = acceptFields;
	}
	/**
	 * @return settings.Returns the acceptInterfaces.
	 */
	public boolean isAcceptInterfaces() {
		return settings.acceptInterfaces;
	}
	/**
	 * @param acceptInterfaces The acceptInterfaces to set.
	 */
	public void setAcceptInterfaces(boolean acceptInterfaces) {
		settings.acceptInterfaces = acceptInterfaces;
	}
	/**
	 * @return settings.Returns the acceptPackages.
	 */
	public boolean isAcceptPackages() {
		return settings.acceptPackages;
	}
	/**
	 * @param acceptPackages The acceptPackages to set.
	 */
	public void setAcceptPackages(boolean acceptPackages) {
		settings.acceptPackages = acceptPackages;
	}
	/**
	 * @return settings.Returns the acceptTypes.
	 */
	public boolean isAcceptTypes() {
		return settings.acceptTypes;
	}
	/**
	 * @param acceptTypes The acceptTypes to set.
	 */
	public void setAcceptTypes(boolean acceptTypes) {
		settings.acceptTypes = acceptTypes;
	}
	
	public void accept(CompletionProposal proposal) {
		super.accept(proposal);
	}
}
