/*
 * Created on 19-Nov-2004
 *
 */
package org.hibernate.eclipse.mapper.editors;

import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.internal.ui.text.java.ResultCollector;

/**
 * @author max
 *
 */
public class HBMXmlResultCollector extends ResultCollector {

	private boolean acceptTypes = false;
	private boolean acceptPackages = false;
	private boolean acceptClasses = false;
	private boolean acceptInterfaces = false;
	private boolean acceptFields = false;
	private boolean acceptMethods = false;
	 	
	public void setAccepts(boolean types, boolean packages, boolean classes, boolean interfaces, boolean fields, boolean methods) {
		acceptTypes = types;
		acceptPackages = packages;
		acceptClasses = classes;
		acceptInterfaces = interfaces;
		acceptFields = fields;
		acceptMethods = methods;
		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.ICompletionRequestor#acceptPackage(char[], char[], int, int, int)
	 */
	public void acceptPackage(char[] packageName, char[] completionName,
			int start, int end, int relevance) {
		// TODO Auto-generated method stub
		if(acceptPackages) { 
			super.acceptPackage(packageName, completionName, start, end, relevance);
		}
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.ICompletionRequestor#acceptClass(char[], char[], char[], int, int, int, int)
	 */
	public void acceptClass(char[] packageName, char[] typeName,
			char[] completionName, int modifiers, int start, int end,
			int relevance) {
		if(acceptClasses) {
			super.acceptClass(packageName, typeName, completionName, modifiers,
					start, end, relevance);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.ICompletionRequestor#acceptInterface(char[], char[], char[], int, int, int, int)
	 */
	public void acceptInterface(char[] packageName, char[] typeName,
			char[] completionName, int modifiers, int start, int end,
			int relevance) {
		if(acceptInterfaces) {
			super.acceptInterface(packageName, typeName, completionName, modifiers,
					start, end, relevance);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.ICompletionRequestor#acceptType(char[], char[], char[], int, int, int)
	 */
	public void acceptType(char[] packageName, char[] typeName,
			char[] completionName, int start, int end, int relevance) {
		if(acceptTypes) {
			super.acceptType(packageName, typeName, completionName, start, end,
					relevance);
		}
	}	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.ICompletionRequestor#acceptField(char[], char[], char[], char[], char[], char[], int, int, int, int)
	 */
	public void acceptField(char[] declaringTypePackageName,
			char[] declaringTypeName, char[] name, char[] typePackageName,
			char[] typeName, char[] completionName, int modifiers, int start,
			int end, int relevance) {
		if(acceptFields) {
			super.acceptField(declaringTypePackageName, declaringTypeName, name,
					typePackageName, typeName, completionName, modifiers, start,					
					end, relevance);
		}
	}
	
	
	//
	// ALL METHODS BELOW THIS COMMENT IS NO-OP's
	//
	//
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.ICompletionRequestor#acceptAnonymousType(char[], char[], char[][], char[][], char[][], char[], int, int, int, int)
	 */
	public void acceptAnonymousType(char[] superTypePackageName,
			char[] superTypeName, char[][] parameterPackageNames,
			char[][] parameterTypeNames, char[][] parameterNames,
			char[] completionName, int modifiers, int completionStart,
			int completionEnd, int relevance) {
//		super.acceptAnonymousType(superTypePackageName, superTypeName,
//				parameterPackageNames, parameterTypeNames, parameterNames,
//				completionName, modifiers, completionStart, completionEnd,
//				relevance);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.ICompletionRequestor#acceptError(org.eclipse.jdt.core.compiler.IProblem)
	 */
	public void acceptError(IProblem error) {
		// TODO Auto-generated method stub
//		super.acceptError(error);
	}
	
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.ICompletionRequestor#acceptKeyword(char[], int, int, int)
	 */
	public void acceptKeyword(char[] keyword, int start, int end, int relevance) {
		// TODO Auto-generated method stub
//		super.acceptKeyword(keyword, start, end, relevance);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.ICompletionRequestor#acceptLabel(char[], int, int, int)
	 */
	public void acceptLabel(char[] labelName, int start, int end, int relevance) {
		// TODO Auto-generated method stub
//		super.acceptLabel(labelName, start, end, relevance);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.ICompletionRequestor#acceptLocalVariable(char[], char[], char[], int, int, int, int)
	 */
	public void acceptLocalVariable(char[] name, char[] typePackageName,
			char[] typeName, int modifiers, int start, int end, int relevance) {
		// TODO Auto-generated method stub
//		super.acceptLocalVariable(name, typePackageName, typeName, modifiers,
//				start, end, relevance);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.ICompletionRequestor#acceptMethod(char[], char[], char[], char[][], char[][], char[][], char[], char[], char[], int, int, int, int)
	 */
	public void acceptMethod(char[] declaringTypePackageName,
			char[] declaringTypeName, char[] name,
			char[][] parameterPackageNames, char[][] parameterTypeNames,
			char[][] parameterNames, char[] returnTypePackageName,
			char[] returnTypeName, char[] completionName, int modifiers,
			int start, int end, int relevance) {

		if(acceptMethods ) {
		super.acceptMethod(declaringTypePackageName, declaringTypeName, name,
				parameterPackageNames, parameterTypeNames, parameterNames,
				returnTypePackageName, returnTypeName, completionName,
				modifiers, start, end, relevance);
		}
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.ICompletionRequestor#acceptMethodDeclaration(char[], char[], char[], char[][], char[][], char[][], char[], char[], char[], int, int, int, int)
	 */
	public void acceptMethodDeclaration(char[] declaringTypePackageName,
			char[] declaringTypeName, char[] name,
			char[][] parameterPackageNames, char[][] parameterTypeNames,
			char[][] parameterNames, char[] returnTypePackageName,
			char[] returnTypeName, char[] completionName, int modifiers,
			int start, int end, int relevance) {
		// TODO Auto-generated method stub
//		super.acceptMethodDeclaration(declaringTypePackageName,
//				declaringTypeName, name, parameterPackageNames,
//				parameterTypeNames, parameterNames, returnTypePackageName,
//				returnTypeName, completionName, modifiers, start, end,
//				relevance);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.ICompletionRequestor#acceptModifier(char[], int, int, int)
	 */
	public void acceptModifier(char[] modifier, int start, int end,
			int relevance) {
//		// TODO Auto-generated method stub
//		super.acceptModifier(modifier, start, end, relevance);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.codeassist.IExtendedCompletionRequestor#acceptPotentialMethodDeclaration(char[], char[], char[], int, int, int)
	 */
	public void acceptPotentialMethodDeclaration(
			char[] declaringTypePackageName, char[] declaringTypeName,
			char[] selector, int completionStart, int completionEnd,
			int relevance) {
		// TODO Auto-generated method stub
//		super.acceptPotentialMethodDeclaration(declaringTypePackageName,
//				declaringTypeName, selector, completionStart, completionEnd,
//				relevance);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.ICompletionRequestor#acceptVariableName(char[], char[], char[], char[], int, int, int)
	 */
	public void acceptVariableName(char[] typePackageName, char[] typeName,
			char[] name, char[] completionName, int start, int end,
			int relevance) {
		// TODO Auto-generated method stub
//		super.acceptVariableName(typePackageName, typeName, name,
//				completionName, start, end, relevance);
	}
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
