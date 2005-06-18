/*
 * Created on 08-Dec-2004
 *
 */
package org.hibernate.eclipse.console.views;

import java.util.Iterator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredViewer;
import org.hibernate.HibernateException;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.node.BaseNode;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.actions.ConsoleConfigurationBasedAction;
import org.hibernate.tool.hbm2ddl.SchemaExport;

/**
 * @author max
 * 
 */
public class SchemaExportAction extends ConsoleConfigurationBasedAction {

    private StructuredViewer viewer;

    /**
     * @param text
     */
    protected SchemaExportAction(String text) {
        super(text);
    }

    /**
     * @param selectionProvider
     */
    public SchemaExportAction(StructuredViewer selectionProvider) {
        super("Run SchemaExport");
        this.viewer = selectionProvider;
    }

    public void doRun() {
        for (Iterator i = getSelectedNonResources().iterator(); i.hasNext();) {
            try {
                BaseNode node = ( (BaseNode) i.next() );
                final ConsoleConfiguration config = node
                        .getConsoleConfiguration();
                config
                        .execute(new org.hibernate.console.ConsoleConfiguration.Command() {
                            public Object execute() {
                                if (config.getConfiguration() != null
                                        && MessageDialog.openConfirm(viewer
                                                .getControl().getShell(),
                                                "Run SchemaExport",
                                                "Are you sure you want to run SchemaExport on '"
                                                        + config.getName()
                                                        + "'?") ) {
                                    SchemaExport export = new SchemaExport(
                                            config.getConfiguration() );
                                    export.create(false, true);
                                    if (!export.getExceptions().isEmpty() ) {
                                        Iterator iterator = export
                                                .getExceptions().iterator();
                                        int cnt = 1;
                                        while (iterator.hasNext() ) {
                                            Throwable element = (Throwable) iterator
                                                    .next();
                                            HibernateConsolePlugin
                                                    .getDefault().logErrorMessage(
                                                            "Error "
                                                                    + cnt++
                                                                    + " while performing SchemaExport",
                                                            element);
                                        }
                                        HibernateConsolePlugin
                                                .getDefault().showError(
                                                        viewer.getControl()
                                                                .getShell(),
                                                        cnt
                                                                - 1
                                                                + " error(s) while performing SchemaExport, see Error Log for details",
                                                        null);
                                    }
                                }
                                return null;
                            }
                        });
                viewer.refresh(node); // todo: should we do it here or should
                                        // the view just react to config being
                                        // build ?
            } catch (HibernateException he) {
                HibernateConsolePlugin.getDefault().showError(
                        viewer.getControl().getShell(),
                        "Exception while running SchemaExport", he);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hibernate.eclipse.console.actions.SessionFactoryBasedAction#updateState(org.hibernate.console.ConsoleConfiguration)
     */
    protected boolean updateState(ConsoleConfiguration consoleConfiguration) {
        return consoleConfiguration.hasConfiguration();
    }
}
