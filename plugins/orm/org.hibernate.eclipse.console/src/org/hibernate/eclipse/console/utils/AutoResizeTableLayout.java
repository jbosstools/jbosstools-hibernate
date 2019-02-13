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
package org.hibernate.eclipse.console.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.hibernate.eclipse.console.HibernateConsoleMessages;

public class AutoResizeTableLayout extends TableLayout implements
        ControlListener {

    private final Table table;
    private List<ColumnLayoutData> columns = new ArrayList<ColumnLayoutData>();
    private boolean autosizing = false;

    public AutoResizeTableLayout(Table table) {
        this.table = table;
        table.addControlListener(this);
    }

    public void addColumnData(ColumnLayoutData data) {
        columns.add(data);
        super.addColumnData(data);
    }

    public void controlMoved(ControlEvent e) {
    }

    public void controlResized(ControlEvent e) {
        if (autosizing) {
            return;
        }
        autosizing = true;
        try {
            autoSizeColumns();
        } finally {
            autosizing = false;
        }
    }

    private void autoSizeColumns() {
        int width = table.getClientArea().width;
        if (width <= 1) {
            return;
        }

        TableColumn[] tableColumns = table.getColumns();
        int size =
            Math.min(columns.size(), tableColumns.length);
        int[] widths = new int[size];
        int fixedWidth = 0;
        int numberOfWeightColumns = 0;
        int totalWeight = 0;

        // First calc space occupied by fixed columns.
        for (int i = 0; i < size; i++) {
            ColumnLayoutData col = columns.get(i);
            if (col instanceof ColumnPixelData) {
                int pixels = ((ColumnPixelData)col).width;
                widths[i] = pixels;
                fixedWidth += pixels;
            } else if (col instanceof ColumnWeightData) {
                ColumnWeightData cw = (ColumnWeightData)col;
                numberOfWeightColumns++;
                int weight = cw.weight;
                totalWeight += weight;
            } else {
                throw new IllegalStateException(HibernateConsoleMessages.AutoResizeTableLayout_unknown_column_layout_data);
            }
        }

        // Do we have columns that have a weight?
        if (numberOfWeightColumns > 0) {
            // Now, distribute the rest
            // to the columns with weight.
            int rest = width - fixedWidth;
            int totalDistributed = 0;
            for (int i = 0; i < size; i++) {
                ColumnLayoutData col = columns.get(i);
                if (col instanceof ColumnWeightData) {
                    ColumnWeightData cw = (ColumnWeightData)col;
                    int weight = cw.weight;
                    int pixels = totalWeight == 0 ? 0 : weight * rest / totalWeight;
                    totalDistributed += pixels;
                    widths[i] = pixels;
                }
            }

            // Distribute any remaining pixels
            // to columns with weight.
            int diff = rest - totalDistributed;
            for (int i = 0; diff > 0; i++) {
                if (i == size) {
                    i = 0;
                }
                ColumnLayoutData col = columns.get(i);
                if (col instanceof ColumnWeightData) {
                    ++widths[i];
                    --diff;
                }
            }
        }

        for (int i = 0; i < size; i++) {
            if (tableColumns[i].getWidth() != widths[i]) {
                tableColumns[i].setWidth(widths[i]);
            }

        }

    }

}
