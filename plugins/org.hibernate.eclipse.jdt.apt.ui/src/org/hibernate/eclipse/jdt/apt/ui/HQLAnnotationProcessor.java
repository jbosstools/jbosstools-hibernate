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
package org.hibernate.eclipse.jdt.apt.ui;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.apt.core.env.EclipseAnnotationProcessorEnvironment;
import org.eclipse.jdt.apt.core.env.Phase;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.eclipse.nature.HibernateNature;
import org.hibernate.engine.query.HQLQueryPlan;
import org.hibernate.impl.SessionFactoryImpl;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.Messager;
import com.sun.mirror.declaration.AnnotationMirror;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import com.sun.mirror.declaration.AnnotationTypeElementDeclaration;
import com.sun.mirror.declaration.AnnotationValue;
import com.sun.mirror.declaration.Declaration;
import com.sun.mirror.type.AnnotationType;

public class HQLAnnotationProcessor implements AnnotationProcessor
{
	final EclipseAnnotationProcessorEnvironment	env;

	public HQLAnnotationProcessor(AnnotationProcessorEnvironment env)
	{
		if(env instanceof EclipseAnnotationProcessorEnvironment) {
			this.env = (EclipseAnnotationProcessorEnvironment) env;
		} else {
			this.env = null;
		}
	}
	
	static ConsoleConfiguration getConsoleConfiguration(HibernateNature hibernateNature) {
		
		if(hibernateNature!=null) {
			return hibernateNature.getDefaultConsoleConfiguration();
		} else {
			return null;
		}
	}

	private ConsoleConfiguration getConsoleConfiguration() {
		return getConsoleConfiguration(HibernateNature.getHibernateNature( env.getJavaProject() ));
	}
	

	public void process()
	{
		try {
			if(env==null) {
				return; // quick stop if not running inside eclipse.
			}

			if(env.getPhase()==Phase.RECONCILE) {
				return; // quick stop if user "editing"
			}
			
			ConsoleConfiguration cc = getConsoleConfiguration();
			
			Messager messager = env.getMessager();

			Set types = new HashSet();
			AnnotationTypeDeclaration jpa_queries = (AnnotationTypeDeclaration)this.env.getTypeDeclaration("javax.persistence.NamedQueries");
			types.addAll(env.getDeclarationsAnnotatedWith(jpa_queries));

			AnnotationTypeDeclaration hibernate_queries = (AnnotationTypeDeclaration)this.env.getTypeDeclaration("org.hibernate.annotations.NamedQueries");
			types.addAll( env.getDeclarationsAnnotatedWith(hibernate_queries) );

			AnnotationTypeDeclaration jpa_namedQuery = (AnnotationTypeDeclaration)this.env.getTypeDeclaration("javax.persistence.NamedQuery");
			types.addAll( env.getDeclarationsAnnotatedWith(jpa_namedQuery) );

			AnnotationTypeDeclaration hibernate_namedQuery = (AnnotationTypeDeclaration)this.env.getTypeDeclaration("org.hibernate.annotations.NamedQuery");
			types.addAll( env.getDeclarationsAnnotatedWith(hibernate_namedQuery) );				

			handleNamedQueryTypes( cc, messager, types );
		} catch (NullPointerException npe) {
			env.getMessager().printError( npe.toString() );
			npe.printStackTrace();
		}

	}

	private void handleNamedQueryTypes(ConsoleConfiguration cc, Messager messager, Collection annotatedTypes) {
		for (Iterator iter = annotatedTypes.iterator(); iter.hasNext();) {
			Declaration decl = (Declaration) iter.next();

			Collection mirrors = decl.getAnnotationMirrors();

			for (Iterator iterator = mirrors.iterator(); iterator.hasNext();) {
				AnnotationMirror mirror = (AnnotationMirror) iterator.next();
				AnnotationType annotationType = mirror.getAnnotationType();
				String simpleName = annotationType.getDeclaration().getSimpleName();
				if(simpleName.equals( "NamedQueries" )) {
					Map valueMap = mirror.getElementValues();
					Set valueSet = valueMap.entrySet();

					for (Iterator it3 = valueSet.iterator(); it3.hasNext();) {
						Map.Entry annoKeyValue = (Map.Entry) it3.next();

						AnnotationValue annoValue = (AnnotationValue) annoKeyValue.getValue();	

						AnnotationTypeElementDeclaration atedec = (AnnotationTypeElementDeclaration) annoKeyValue.getKey();
						if (atedec.getSimpleName().equals("value")) {
							Object queryValueList = annoValue.getValue();			
							if (queryValueList instanceof Collection) {
								Collection values = (Collection) queryValueList;
								for (Iterator val = values.iterator(); val.hasNext();) {
									Object element = val.next();
									if(element instanceof AnnotationValue) {
										AnnotationValue el = (AnnotationValue) element;
										if(el.getValue() instanceof AnnotationMirror) {
											AnnotationMirror annMirror = (AnnotationMirror) el.getValue();	
											
											handleNamedQuery( cc, messager, annMirror );
											
										}																				
									}
								}
								
							}
						}						
					}				
				} else if (simpleName.equals("NamedQuery")) {
					handleNamedQuery( cc, messager, mirror );	
				}
				
			}
		}
	}

	private void handleNamedQuery(ConsoleConfiguration cc, Messager messager, AnnotationMirror mirror) {
		Map valueMap = mirror.getElementValues();
		Set valueSet = valueMap.entrySet();

		for (Iterator it3 = valueSet.iterator(); it3.hasNext();) {
			Map.Entry annoKeyValue = (Map.Entry) it3.next();

			AnnotationValue annoValue = (AnnotationValue) annoKeyValue.getValue();	

			AnnotationTypeElementDeclaration atedec = (AnnotationTypeElementDeclaration) annoKeyValue.getKey();
			handleSingleQuery( cc, messager, annoValue, atedec );						
		}
	}

	private void handleSingleQuery(ConsoleConfiguration cc, Messager messager, AnnotationValue annoValue, AnnotationTypeElementDeclaration atedec) {
		if (atedec.getSimpleName().equals("query")) {
			Object query = annoValue.getValue();			
			if (query instanceof String) {
				checkQuery( cc, messager, annoValue, query );
			}
		}
	}

	private void checkQuery(ConsoleConfiguration cc, Messager messager, AnnotationValue annoValue, Object query) {
		try {
			if(cc!=null && cc.isSessionFactoryCreated()) {
				new HQLQueryPlan((String)query, false, Collections.EMPTY_MAP, (SessionFactoryImpl)cc.getSessionFactory());
			} else {											
				messager.printWarning( annoValue.getPosition(), "Could not verify syntax. SessionFactory not created." );
			}
		} catch(RuntimeException re) {
			messager.printError(annoValue.getPosition(), re.getMessage());	
		}
	}


	

}
