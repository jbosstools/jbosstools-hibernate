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
package org.hibernate.eclipse.launch;

import java.util.Iterator;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.hibernate.eclipse.console.model.impl.ExporterInstance;
import org.hibernate.eclipse.console.model.impl.ExporterProperty;

public class ExporterPropertyDialog extends Dialog
{
   private ExporterInstance exporter;
   private Combo propertyNameCombo;
   private Text propertyValueText;
   
   private String propertyName, propertyValue;
   
   public ExporterPropertyDialog (Shell shell, ExporterInstance instance)
   {
      super(shell);
      
      this.exporter = instance;
   }
   
   protected Control createDialogArea (Composite parent)
   {
      Composite main = new Composite(parent, SWT.NONE);
      main.setLayout(new GridLayout(2,false));
      
      new Label(main, SWT.NONE).setText("Property name:");
      propertyNameCombo = new Combo(main, SWT.DROP_DOWN);
      
      new Label(main, SWT.NONE).setText("Value:");
      propertyValueText = new Text(main, SWT.BORDER);
      
      addDefinedProperties();
      addListeners();
      
      return main;
   }
   
   private void addDefinedProperties ()
   {
      for (Iterator iter = exporter.getDefinition().getProperties().keySet().iterator(); iter.hasNext(); )
      {
         ExporterProperty property = (ExporterProperty) iter.next();
         
         if (! exporter.getProperties().containsKey(property))
         {
           propertyNameCombo.add(property.getName());
         }
      }
   }
   
   private void addListeners ()
   {
      propertyNameCombo.addModifyListener(new ModifyListener () {
         public void modifyText(ModifyEvent e)
         {
            propertyName = propertyNameCombo.getText();
         }
      });
      
      propertyValueText.addModifyListener(new ModifyListener () {
         public void modifyText(ModifyEvent e)
         {
            propertyValue = propertyValueText.getText();
         }
      });
   }
   
   public ExporterProperty getProperty ()
   {
      return exporter.findOrCreateProperty(propertyName);
   }
   
   public String getPropertyValue ()
   {
      return propertyValue;
   }
}
