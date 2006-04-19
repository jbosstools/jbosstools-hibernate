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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.AbstractElementListSelectionDialog;
import org.hibernate.eclipse.console.ExtensionManager;
import org.hibernate.eclipse.console.model.impl.ExporterDefinition;

public class ExporterSelectionDialog extends AbstractElementListSelectionDialog
{
   private ExporterDefinition[] exporters;
   private Label message;
   
   public ExporterSelectionDialog (Shell shell)
   {
      super(shell, new ExporterLabelProvider());
      
      exporters = ExtensionManager.findExporterDefinitions();
   }
   
   protected Control createDialogArea(Composite parent)
   {
      Composite contents = (Composite) super.createDialogArea(parent);
      message = createMessageArea(contents);
      createFilterText(contents);
      createFilteredList(contents);
      
      setListElements(exporters);
      message.setText("Please select an Exporter");
      setMultipleSelection(false);
      
      return contents;
   }
   
   protected void computeResult()
   {
      setResult(Arrays.asList(this.fFilteredList.getSelection()));
   }
   
   private static class ExporterLabelProvider implements ILabelProvider {
        Map exp2img = new HashMap(); // not the most optimized but better than having a finalize method.

        public Image getImage (Object element) {
            ExporterDefinition definition = (ExporterDefinition) element;
            Image image = (Image) exp2img.get( definition.getId() );
            if ( image == null ) {
                image = definition.getIconDescriptor().createImage();
                exp2img.put( definition.getId(), image );
            }
            return image;
        }

        public String getText(Object element) {
            ExporterDefinition definition = (ExporterDefinition) element;
            return definition.getDescription();
        }

        public void addListener(ILabelProviderListener listener) {
        }

        public void dispose() {

            Iterator iterator = exp2img.values().iterator();
            while ( iterator.hasNext() ) {
                Image img = (Image) iterator.next();
                if ( img != null ) {
                    img.dispose();
                }
            }
        }

        public boolean isLabelProperty(Object element, String property) {
            return true;
        }

        public void removeListener(ILabelProviderListener listener) {
        }
    }
}
