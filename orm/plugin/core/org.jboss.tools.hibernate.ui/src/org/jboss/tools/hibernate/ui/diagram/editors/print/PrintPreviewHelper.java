package org.jboss.tools.hibernate.ui.diagram.editors.print;

import java.util.ArrayList;
import java.util.List;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import org.eclipse.core.runtime.Assert;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.LayerConstants;
/** /
import org.eclipse.gmf.runtime.common.ui.action.actions.IPrintActionHelper;
import org.eclipse.gmf.runtime.common.ui.util.WindowUtil;
import org.eclipse.gmf.runtime.diagram.ui.internal.pagesetup.PageInfoHelper.PageMargins;
import org.eclipse.gmf.runtime.diagram.ui.printing.internal.l10n.DiagramUIPrintingPluginImages;
import org.eclipse.gmf.runtime.diagram.ui.printing.internal.util.HeaderAndFooterHelper;
import org.eclipse.gmf.runtime.diagram.ui.printing.internal.util.PrintHelperUtil;
import org.eclipse.gmf.runtime.draw2d.ui.internal.graphics.MapModeGraphics;
import org.eclipse.gmf.runtime.draw2d.ui.internal.graphics.ScaledGraphics;
import org.eclipse.gmf.runtime.draw2d.ui.internal.mapmode.DiagramMapModeUtil;
import org.eclipse.gmf.runtime.draw2d.ui.mapmode.IMapMode;
import org.eclipse.gmf.runtime.draw2d.ui.mapmode.MapModeUtil;
import org.eclipse.gmf.runtime.draw2d.ui.render.internal.graphics.RenderedMapModeGraphics;
import org.eclipse.gmf.runtime.draw2d.ui.render.internal.graphics.RenderedScaledGraphics;
/**/
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.hibernate.ui.diagram.editors.DiagramViewer;
import org.jboss.tools.hibernate.ui.diagram.editors.parts.DiagramEditPart;

/**
 * initial implementation
 * 
 * @author Vitali Yemialyanchyk
 */
public class PrintPreviewHelper{

	/**
	 * Action helper for print. This must be passed in to have something happen
	 * when print is pressed
	 */
//	protected IPrintActionHelper printActionHelper;

	/**
	 * Increment userX everytime the user moves right, decrement userX everytime
	 * the user moves left.
	 * 
	 * userX >= 0
	 */
	protected int userX = 0;

	/**
	 * Increment userY everytime the user moves down, decrement userY everytime
	 * the user moves up.
	 * 
	 * userY >= 0
	 */
	protected int userY = 0;

	/**
	 * Number of rows, initialized with initial number of rows of pages to
	 * display
	 */
	protected int numberOfRowsToDisplay = 2;

	/**
	 * Number of columns, initialized with initial number of columns of pages to
	 * display
	 */
	protected int numberOfColumnsToDisplay = 2;

	/**
	 * The diagram edit part
	 */
	protected DiagramEditPart diagramEditPart;

	/**
	 * Max bounds of a page for no page break
	 */
	protected Rectangle pageBreakBounds;

	/* SWT interface variables */

	/**
	 * Body of the shell.
	 */
	protected Composite body;

	/**
	 * Composite for the pages
	 */
	protected Composite composite;

	/**
	 * Height of the button bar, initialized right before the button bar is
	 * created.
	 */
	protected int buttonBarHeight;

	/**
	 * Shell for the new pop up
	 */
	protected Shell shell;
    
    /**
     * Temporary shell to be used when creating the diagram editpart.
     */
    private Shell tempShell;

	/* Toolbar items are in left to right order */

	/**
	 * Print item on toolbar
	 */
	protected ToolItem printTool;
	
	/**
	 * Enable or disable the print option
	 */
	protected boolean enablePrinting = true;

	/**
	 * Pages item on toolbar
	 */
	protected ToolItem pagesTool;

	/**
	 * Left item on toolbar
	 */
	protected ToolItem leftTool;

	/**
	 * Right item on toolbar
	 */
	protected ToolItem rightTool;

	/**
	 * Up item on toolbar
	 */
	protected ToolItem upTool;

	/**
	 * Down item on toolbar
	 */
	protected ToolItem downTool;

	/**
	 * Close item on toolbar
	 */
	protected ToolItem closeTool;

	/**
	 * It's easiest to keep track of the page images using an image list, but I
	 * could also have done getImage on the labels
	 */
	private List imageList = new ArrayList();

	/**
	 * Border size
	 */
	protected static final int BORDER_SIZE = 20;

	/**
	 * the background color
	 */
	private static final Color BACKGROUND_COLOR = new Color(Display
		.getDefault(), 124, 124, 124);

	/* Images */
	/**
	 * Enabled print image
	 */
	protected Image printImage;

	/**
	 * Disabled Print image
	 */
	protected Image disabledPrintImage;

	/**
	 * Page image, unlikely to ever be disabled
	 */
	protected Image pageImage;

	/**
	 * Enabled left image
	 */
	protected Image leftImage;

	/**
	 * Disabled left image
	 */
	protected Image disabledLeftImage;

	/**
	 * Enabled right image
	 */
	protected Image rightImage;

	/**
	 * Disabled right image
	 */
	protected Image disabledRightImage;

	/**
	 * Enabled up image
	 */
	protected Image upImage;

	/**
	 * Diabled up image
	 */
	protected Image disabledUpImage;

	/**
	 * Enabled down image
	 */
	protected Image downImage;

	/**
	 * Disabled down image
	 */
	protected Image disabledDownImage;

	/**
	 * Close image, unlikely to ever be disabled
	 */
	protected Image closeImage;
	
	/**
	 * The print preview helper is capable of showing zoom input.
	 * userScale is a value between 0 and 1.
	 */
	protected double userScale = 1;
	
	/**
	 *  Initial zoom levels.
	 */
	private int[] zoomLevels = { 25, 50, 75, 100, 150, 200, 250, 300, 400 };
		
	/**
	 * A minimum scale percentage.
	 */
	private static int MINIMUM_SCALE_FACTOR = 5;
	
	/**
	 * Initialize all toolbar images
	 */
	/** /
	protected void initializeToolbarImages() {
		printImage = DiagramUIPrintingPluginImages.DESC_PRINT.createImage();

		disabledPrintImage = DiagramUIPrintingPluginImages.DESC_PRINT_DISABLED
			.createImage();

		pageImage = DiagramUIPrintingPluginImages.DESC_PAGE.createImage();

		leftImage = DiagramUIPrintingPluginImages.DESC_LEFT.createImage();

		disabledLeftImage = DiagramUIPrintingPluginImages.DESC_LEFT_DISABLED
			.createImage();

		rightImage = DiagramUIPrintingPluginImages.DESC_RIGHT.createImage();

		disabledRightImage = DiagramUIPrintingPluginImages.DESC_RIGHT_DISABLED
			.createImage();

		upImage = DiagramUIPrintingPluginImages.DESC_UP.createImage();
		disabledUpImage = DiagramUIPrintingPluginImages.DESC_UP_DISABLED
			.createImage();

		downImage = DiagramUIPrintingPluginImages.DESC_DOWN.createImage();

		disabledDownImage = DiagramUIPrintingPluginImages.DESC_DOWN_DISABLED
			.createImage();

		closeImage = DiagramUIPrintingPluginImages.DESC_CLOSE.createImage();
	}

	public void enablePrinting(boolean enablePrinting){
		this.enablePrinting = enablePrinting;
	}
	
	public void doPrintPreview(IPrintActionHelper prActionHelper) {
		this.printActionHelper = prActionHelper;

		setUserScale(PrintHelperUtil.getScale());
		
		if (getDiagramEditorPart() == null) {
			MessageDialog
				.openInformation(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(),
					"PrintPreview_Title", 
					"PrintPreview_NotEnabled"); 
			return;
		}

		if (!isPrinterInstalled()) {
			WindowUtil
				.doMessageBox("PrintPreview_NoPrinterInstalled", 
					"PrintPreview_Title", 
					SWT.ICON_ERROR, PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getShell());
			return;
		}

		initializeToolbarImages();

		initializeMapMode();

		diagramEditPart = null;
		pageBreakBounds = null;
		
		userX = 0;
		userY = 0;
		
		if (getTotalNumberOfRows() == 1 && getTotalNumberOfColumns() == 1) {
			numberOfRowsToDisplay = 1;
			numberOfColumnsToDisplay = 1;
		}
		else if (getTotalNumberOfRows() == 1) {
			numberOfRowsToDisplay = 1;
			numberOfColumnsToDisplay = 2;
		}
		else {
			numberOfRowsToDisplay = 2;
			numberOfColumnsToDisplay = 2;
		}

		Display display = PlatformUI.getWorkbench().getDisplay();
		
        //check for rtl Torientation...
        int style = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getStyle();
        if ((style & SWT.MIRRORED) != 0) {
            shell = new Shell(display, SWT.APPLICATION_MODAL | SWT.TITLE
                | SWT.CLOSE | SWT.BORDER | SWT.RIGHT_TO_LEFT);
        }
        else
            shell = new Shell(display, SWT.APPLICATION_MODAL | SWT.TITLE
                | SWT.CLOSE | SWT.BORDER);
        
		

		shell.setSize(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
			.getShell().getSize());
		shell.setText("PrintPreview_Title");
		shell.setLocation(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
			.getShell().getLocation());
		shell.setLayout(new GridLayout(1, true));

		ToolBar bar = new ToolBar(shell, SWT.FLAT | SWT.HORIZONTAL);

		printTool = new ToolItem(bar, SWT.NULL);
		printTool.setToolTipText("PrintPreview_PrintToolItem");
		printTool.setImage(printImage);
		printTool.setDisabledImage(disabledPrintImage);
		printTool.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				//should not be enabled
				Assert.isNotNull(printActionHelper);
				
				printActionHelper
					.doPrint(PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage()
						.getActivePart());
				shell.setActive();
				
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		if (printActionHelper == null || !enablePrinting) {
			printTool.setEnabled(false);
		}

		new ToolItem(bar, SWT.SEPARATOR);

		pagesTool = new ToolItem(bar, SWT.DROP_DOWN);
		pagesTool.setToolTipText("PrintPreview_PagesToolItem");
		pagesTool.setImage(pageImage);

		pagesTool.addSelectionListener(new SelectionAdapter() {

			private Menu menu = null;

			//also update userX, userY, numberOfRowsToDisplay,
			// numberOfColumnsToDisplay
			private void updatePreview(int newNumberOfColumnsToDisplay,
					int newNumberOfRowsToDisplay) {
				numberOfRowsToDisplay = newNumberOfRowsToDisplay;
				numberOfColumnsToDisplay = newNumberOfColumnsToDisplay;

				//When switching the number of pages to display to a bigger
				//number, you can get an extra blank page on the right or on
				//the bottom. This check prevents the extra blank page.

				if (userX + numberOfColumnsToDisplay > getTotalNumberOfColumns()) {
					//move it left
					userX = getTotalNumberOfColumns()
						- numberOfColumnsToDisplay;
					//be safe, check for 0
					if (userX < 0)
						userX = 0;
				}

				if (userY + numberOfRowsToDisplay > getTotalNumberOfRows()) {
					//move it up
					userY = getTotalNumberOfRows() - numberOfRowsToDisplay;
					//be safe, check for 0
					if (userY < 0)
						userY = 0;
				}
				
				refreshComposite();

			}

			public void widgetSelected(SelectionEvent event) {
				// Create the menu if it has not already been created

				if (menu == null) {
					// Lazy create the menu.
					menu = new Menu(shell);
					MenuItem menuItem = new MenuItem(menu, SWT.NONE);
					menuItem.setText("PrintPreview_1Up");
					menuItem.addSelectionListener(new SelectionAdapter() {

						public void widgetSelected(SelectionEvent e) {
							updatePreview(1, 1);
						}
					});

					menuItem = new MenuItem(menu, SWT.NONE);
					menuItem.setText("PrintPreview_2Up");
					menuItem.addSelectionListener(new SelectionAdapter() {

						public void widgetSelected(SelectionEvent e) {
							updatePreview(2, 1);
						}
					});

					menuItem = new MenuItem(menu, SWT.NONE);
					menuItem.setText("PrintPreview_4Up");
					menuItem.addSelectionListener(new SelectionAdapter() {

						public void widgetSelected(SelectionEvent e) {
							updatePreview(2, 2);
						}
					});
				}

				final ToolItem toolItem = (ToolItem) event.widget;
				final ToolBar toolBar = toolItem.getParent();
				org.eclipse.swt.graphics.Rectangle toolItemBounds = toolItem
					.getBounds();
				//left aligned under the pages button
				Point point = toolBar.toDisplay(new Point(toolItemBounds.x,
					toolItemBounds.y));
				menu.setLocation(point.x, point.y + toolItemBounds.height);
				setMenuVisible(true);

			}

			private void setMenuVisible(boolean visible) {
				menu.setVisible(visible);
			}

		});

		new ToolItem(bar, SWT.SEPARATOR);

		leftTool = new ToolItem(bar, SWT.NULL);
        if ((style & SWT.MIRRORED) != 0) {
            //switch left and right for RTL...
            leftTool.setToolTipText("PrintPreview_RightToolItem");
            leftTool.setImage(rightImage);
            leftTool.setDisabledImage(disabledRightImage);
        }
        else { 
            leftTool.setToolTipText("PrintPreview_LeftToolItem");
            leftTool.setImage(leftImage);
            leftTool.setDisabledImage(disabledLeftImage);
        }
		
		leftTool.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				if (userX > 0) {
					userX--;
					refreshComposite();
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

        rightTool = new ToolItem(bar, SWT.NULL);
        if ((style & SWT.MIRRORED) != 0) {
            //switch left and right for RTL
            rightTool.setToolTipText("PrintPreview_LeftToolItem");
            rightTool.setImage(leftImage);
            rightTool.setDisabledImage(disabledLeftImage);
        }
        else { 
            rightTool.setToolTipText("PrintPreview_RightToolItem");
            rightTool.setImage(rightImage);
            rightTool.setDisabledImage(disabledRightImage);    
        }
		
		rightTool.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				//check for max pages to be safe
				if (!(userX + numberOfColumnsToDisplay + 1 > getTotalNumberOfColumns())) {
					userX++;
					refreshComposite();
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		upTool = new ToolItem(bar, SWT.NULL);
		upTool.setToolTipText("PrintPreview_UpToolItem");
		upTool.setImage(upImage);
		upTool.setDisabledImage(disabledUpImage);
		upTool.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				if (userY > 0) {
					userY--;
					refreshComposite();
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		downTool = new ToolItem(bar, SWT.NULL);
		downTool.setToolTipText("PrintPreview_DownToolItem");
		downTool.setImage(downImage);
		downTool.setDisabledImage(disabledDownImage);
		downTool.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				if (!(userY + numberOfRowsToDisplay + 1 > getTotalNumberOfRows())) {
					userY++;
					refreshComposite();
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
				
		new ToolItem(bar, SWT.SEPARATOR);
							
		ToolItem separator = new ToolItem(bar, SWT.SEPARATOR);
		final Combo zoomCombo = new Combo(bar, SWT.DROP_DOWN);
			
		for (int i = 0; i < zoomLevels.length; i++) {
			zoomCombo.add(getDisplayScale(zoomLevels[i]));
		}
		
		zoomCombo.setText(getDisplayScale(PrintHelperUtil.getScale()));
						
		zoomCombo.addSelectionListener(new SelectionAdapter() {

			private void doZoom(Combo combo) {
				String scaleFactor = combo.getText();
				int percentageIndex = scaleFactor.indexOf("%"); //$NON-NLS-1$
				if (percentageIndex > 0) {
					scaleFactor = scaleFactor.substring(0, percentageIndex);
				}
				
				int scalePercentage = PrintHelperUtil.getScale();
				try {
					scalePercentage = Integer.parseInt(scaleFactor);
				} catch (NumberFormatException e) {
					// Ignore invalid entry; default is last known acceptable value
				}
							
				if(scalePercentage < MINIMUM_SCALE_FACTOR){
					scalePercentage = MINIMUM_SCALE_FACTOR;
				}
				setPercentScaling(scalePercentage);
				refreshComposite();
				combo.setText(getDisplayScale(scalePercentage));
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				doZoom((Combo) e.getSource());
			}

			public void widgetSelected(SelectionEvent e) {
				//String selectedString = ((Combo) e.getSource()).getText();
				doZoom((Combo) e.getSource());
			}
		});
						
		zoomCombo.pack();
		separator.setWidth(zoomCombo.getSize().x);
		separator.setControl(zoomCombo);
										
		new ToolItem(bar, SWT.SEPARATOR);
		closeTool = new ToolItem(bar, SWT.NULL);
		closeTool.setToolTipText("PrintPreview_CloseToolItem");
		closeTool.setImage(closeImage);
		closeTool.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				dispose();
				shell.close();
				shell.dispose();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		buttonBarHeight = bar.getBounds().height - bar.getBounds().y;

		bar.setBounds(0, 0, shell.getSize().x, buttonBarHeight);

		//do the body in the middle
		body = new Composite(shell, SWT.NULL);
		body.setLayout(new GridLayout(1, true));
		body.setLayoutData(new GridData(GridData.FILL_BOTH));
		body.setBackground(BACKGROUND_COLOR);

		composite = new Composite(body, SWT.NULL);
		composite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
		
		refreshComposite();

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		
		dispose();
		shell.dispose();

	}

	private IMapMode mm;
	
	public PrintPreviewHelper() {
		//do nothing
	}
	
	protected IMapMode getMapMode() {
		return mm;
	}

	private void initializeMapMode() {
		DiagramViewer diagramEditor = getDiagramEditorPart();
		
		assert diagramEditor != null;
		this.mm = MapModeUtil.getMapMode();
	}
	
	private int getTotalNumberOfRows() {
		return 1;
	}

	private DiagramViewer getDiagramEditorPart() {
		//more explicit than using window
		IEditorPart editorPart = PlatformUI.getWorkbench()
			.getActiveWorkbenchWindow().getActivePage().getActiveEditor();

		if (!(editorPart instanceof DiagramViewer)) {
			return null;
		}

		DiagramViewer diagramEditorPart = (DiagramViewer) editorPart;

		return diagramEditorPart;
	}

	protected Rectangle getDiagramBounds() {
		return new Rectangle(0, 0, 100, 100);
	}

	protected Rectangle getPageBreakBounds() {
		if (pageBreakBounds == null) {
			pageBreakBounds = new Rectangle(0, 0, 100, 100);
			//vit
			//pageBreakBounds = PrintHelperUtil.getPageBreakBounds(getDiagramEditPart(), true).getCopy();
		}
		
		return pageBreakBounds;
	}

	protected Rectangle getBounds() {
		//don't worry about storing it, it's cached
		return (getPageBreakBounds() == null) ? getDiagramBounds()
			: getPageBreakBounds();
	}

	protected DiagramEditPart getDiagramEditPart() {
		if (diagramEditPart == null) {
			diagramEditPart = getDiagramEditorPart().getDiagramEditPart();
		}
		if (diagramEditPart == null) {
			//Diagram diagram = getDiagramEditorPart().getDiagram(); //do not getDiagramEditPart
			//PreferencesHint preferencesHint = getPreferencesHint(getDiagramEditorPart());
			//diagramEditPart = PrintHelperUtil.createDiagramEditPart(diagram, preferencesHint, getTempShell());
			//PrintHelperUtil.initializePreferences(diagramEditPart, preferencesHint);
		}
		return diagramEditPart;
	}

	private int getTotalNumberOfColumns() {
		return 1;
	}

	protected boolean isPrinterInstalled() {
		try {
			PrintService[] printServices = PrintServiceLookup.lookupPrintServices(
					null, null);
			return printServices.length > 0;
		} catch (SWTError e) {
			if (e.code == SWT.ERROR_NO_HANDLES) {
				//it might have really been ERROR_NO_HANDLES, but there's
				//no way for me to really know
				return false;
			}
			// "Failed to make instance of Printer object"
			throw e;
		}	
	}

	private void disposeImages() {
		while (imageList.size() > 0) {
			Assert.isTrue(imageList.get(0) instanceof Image);
			if (!((Image) imageList.get(0)).isDisposed())
				((Image) imageList.get(0)).dispose();
			imageList.remove(0);
		}
	}

	private void refreshComposite(){
		
		updateCompositeForNumberOfColumns(numberOfRowsToDisplay,
				numberOfColumnsToDisplay);
		
		updateLeftRightUpDownButtonsForToolbar();
	}	
	
	private void updateCompositeForNumberOfColumns(int numberOfRows,
			int numberOfColumns) {
		Assert.isNotNull(shell);
		Assert.isNotNull(composite);

		WindowUtil.disposeChildren(composite);
		disposeImages();

		//the next two lines of code are intentional
		composite.setLayout(null);
		composite.pack();

		composite.setLayout(new GridLayout(numberOfColumns, true));

		//(shell height - toolbar height - top border - bottom border - title -
		// ((# of rows - 1) x vertical border between images)) / # of rows
		int imageHeight = (shell.getSize().y - buttonBarHeight - BORDER_SIZE
			- BORDER_SIZE - BORDER_SIZE - ((numberOfRows - 1) * BORDER_SIZE))
			/ numberOfRows;

		//(shell width - left border - right border - ((# of columns - 1) x
		// horizontal border between images)) / # of columns
		int imageWidth = (shell.getSize().x - BORDER_SIZE - BORDER_SIZE - ((numberOfColumns - 1) * BORDER_SIZE))
			/ numberOfColumns;

		//now adjust to the limiting one based on aspect ratio

		//to make this conform to the page breaks, RATLC00247228
		//get printer ratio from the page, not the real printer

		//vit
		//org.eclipse.draw2d.geometry.Point pageSize = PageInfoHelper
		//	.getPageSize(getPreferenceStore(), false, getMapMode());
		org.eclipse.draw2d.geometry.Point pageSize = 
			new org.eclipse.draw2d.geometry.Point(100, 100);
		Assert.isNotNull(pageSize);
		
		//width / height
		float printerRatio = ((float) pageSize.x) / ((float) pageSize.y);

		if (imageHeight * printerRatio < imageWidth) {
			//round down
			imageWidth = (int) (imageHeight * printerRatio);
		} else if (imageWidth * (1 / printerRatio) < imageHeight) {
			//round down
			imageHeight = (int) (imageWidth * (1.0f / printerRatio));
		}
		
		//vit
		//PageMargins margins = PageInfoHelper.getPageMargins(getPreferenceStore(), getMapMode());
		PageMargins margins = new PageMargins();
		margins.left = 1270;
		margins.right = 1270;
		margins.top = 1270;
		margins.bottom = 1270;
		

		//make sure height and width are not 0, if too small <4, don't bother
		if (!(imageHeight <= 4 || imageWidth <= 4)) {

			//or imageWidth / pageSize.x
			float scale = ( imageHeight / (float) pageSize.y)
				/ (float) DiagramMapModeUtil.getScale(getMapMode());
		
			scale *= userScale;
			
			margins.left /= userScale;   
			margins.right /= userScale;
			margins.bottom /= userScale;
			margins.top /= userScale;
									
			for (int i = 0; i < numberOfRows; i++) {
				for (int j = 0; j < numberOfColumns; j++) {
					Label label = new Label(composite, SWT.NULL);
					Image pageImg = makeImage(imageWidth, imageHeight, i, j,
						scale, margins);
					label.setImage(pageImg);
					imageList.add(pageImg);
				}
			}
		}

		composite.pack();

		//GridData.VERTICAL_ALIGN_CENTER | GridData.HORIZONTAL_ALIGN_CENTER
		// won't do it for you
		org.eclipse.swt.graphics.Rectangle compositeBounds = composite
			.getBounds();

		//this approximation is OK
		compositeBounds.x = (shell.getSize().x - BORDER_SIZE - compositeBounds.width) / 2;
		compositeBounds.y = (shell.getSize().y - buttonBarHeight - BORDER_SIZE
			- BORDER_SIZE - BORDER_SIZE - compositeBounds.height) / 2;
		composite.setBounds(compositeBounds);
	}

	protected void updateLeftRightUpDownButtonsForToolbar() {
		if (userX == 0) {
			leftTool.setEnabled(false);
		} else {
			leftTool.setEnabled(true);
		}

		//should be (user + 1) + (display - 1), the +1 and -1 can be taken out
		if (userX + numberOfColumnsToDisplay + 1 > getTotalNumberOfColumns()) {
			rightTool.setEnabled(false);
		} else {
			rightTool.setEnabled(true);
		}

		if (userY == 0) {
			upTool.setEnabled(false);
		} else {
			upTool.setEnabled(true);
		}

		if (userY + numberOfRowsToDisplay + 1 > getTotalNumberOfRows()) {
			downTool.setEnabled(false);
		} else {
			downTool.setEnabled(true);
		}
	}

	protected Image makeImage(int imageWidth, int imageHeight, int row,
			int col, float scale, PageMargins margins) {

		Image image = new Image(shell.getDisplay(), imageWidth, imageHeight);

        GC gc = null;
        
        //check for rtl orientation...
        if ((shell.getStyle() & SWT.MIRRORED) != 0) {
            gc = new GC(image, SWT.RIGHT_TO_LEFT);
        }
        else
            gc = new GC(image);

		SWTGraphics sg = new SWTGraphics(gc);
		//for scaling
		ScaledGraphics g1 = new RenderedScaledGraphics(sg);
	
		//for himetrics and svg
		MapModeGraphics mmg = createMapModeGraphics(g1);
		
		//if mmg's font is null, gc.setFont will use a default font
		gc.setFont(mmg.getFont());
	
		drawPage(mmg, gc, scale, row, col, margins);
		
		gc.dispose();

		return image;
	}
	
	protected void drawPage(Graphics g, GC gc, float scale, int row, int col, PageMargins margins) {
		
		//vit
		scale = 4.888f;
		
		org.eclipse.draw2d.geometry.Point pageSize =
			new org.eclipse.draw2d.geometry.Point(0, 0);
		//vit
		pageSize.x = 21590;
		pageSize.y = 27940;
		
		g.pushState();
		
		Rectangle bounds = getBounds();
		//vit
		bounds.x = bounds.y = 0;
		bounds.height = 25400;
		bounds.width = 19050;

		if (PrintHelperUtil.getScaleToWidth() == 1	&& PrintHelperUtil.getScaleToHeight() == 1) {
			//vit
			//bounds = getDiagramEditPart().getChildrenBounds();
		}		
							
		int scaledPageSizeWidth = (int)(pageSize.x/userScale) ;
		int scaledPageSizeHeight = (int)(pageSize.y/userScale) ;
						
		//offset by page break figure bounds, then offset by the row or column we're at, and then take margins into account
		int translateX = - bounds.x - (scaledPageSizeWidth * (col + userX)) + (margins.left * (col + userX + 1)) + (margins.right * (col + userX));
		int translateY = - bounds.y - (scaledPageSizeHeight * (row + userY)) + (margins.top * (row + userY + 1)) + (margins.bottom * (row + userY));
		
		//To set a specific font, we could do this
		//For a completely accurate print preview, the font is printer specific
		//and may not be supported by the screen, so it is pointless
		//FontData fontData = JFaceResources.getDefaultFont().getFontData()[0];
		//Font font = new Font(device, fontData);
		//g.setFont(font);
		
		if (doesPageExist(1 + userX + col, 1 + userY + row)) {
			g.pushState(); //draw text, don't make it too small or big
			g.scale(scale);

			//vit
			//String headerOrFooter =
			//	HeaderAndFooterHelper.makeHeaderOrFooterString(
			//		WorkspaceViewerProperties.HEADER_PREFIX,
			//		1 + userY + row,
			//		1 + userX + col,
			//		getDiagramEditPart());
			String headerOrFooter = "headerOrFooter";
			
			g.drawText(
				headerOrFooter,				
				(pageSize.x - getMapMode().DPtoLP(gc.textExtent(headerOrFooter).x)) / 2,
				getMapMode().DPtoLP(HeaderAndFooterHelper.TOP_MARGIN_DP));

			//vit
			//headerOrFooter =
			//	HeaderAndFooterHelper.makeHeaderOrFooterString(
			//		WorkspaceViewerProperties.FOOTER_PREFIX,
			//		1 + userY + row,
			//		1 + userX + col,
			//		getDiagramEditPart());
			headerOrFooter = "headerOrFooter";

			g.drawText(
				headerOrFooter,
				(pageSize.x - getMapMode().DPtoLP(gc.textExtent(headerOrFooter).x)) / 2,
				pageSize.y - getMapMode().DPtoLP(HeaderAndFooterHelper.BOTTOM_MARGIN_DP));

			g.popState(); //for drawing the text				
		}

		g.scale(scale);

		g.translate(translateX, translateY);
		
        Rectangle clip = new Rectangle(
        		(scaledPageSizeWidth - margins.left - margins.right) * (col + userX) + bounds.x, 
    			(scaledPageSizeHeight - margins.top - margins.bottom)* (row + userY) + bounds.y, 
    			scaledPageSizeWidth - margins.left - margins.right,
    			scaledPageSizeHeight - margins.top - margins.bottom);
		g.clipRect(clip);		
		
		//should be from printer and screen ratio and image size
		getDiagramEditPart().getLayer(LayerConstants.PRINTABLE_LAYERS).paint(g);
		
		g.popState();
	}

	private boolean doesPageExist(int x, int y) {
		return x > 0 && y > 0 && x <= getTotalNumberOfColumns() && y <= getTotalNumberOfRows();
	}
	

	private void safeDisposeImage(Image image) {
		if (image != null && !image.isDisposed())
			image.dispose();
	}

	protected void dispose() {
		disposeImages();
		safeDisposeImage(printImage);
		safeDisposeImage(disabledPrintImage);
		safeDisposeImage(pageImage);
		safeDisposeImage(leftImage);
		safeDisposeImage(disabledLeftImage);
		safeDisposeImage(rightImage);
		safeDisposeImage(disabledRightImage);
		safeDisposeImage(upImage);
		safeDisposeImage(disabledUpImage);
		safeDisposeImage(downImage);
		safeDisposeImage(disabledDownImage);
		safeDisposeImage(closeImage);
		if (tempShell != null) {
	        tempShell.dispose();
	        tempShell = null;
		}
	}
	
	protected MapModeGraphics createMapModeGraphics(
			ScaledGraphics scaledGraphics) {
		return new RenderedMapModeGraphics(scaledGraphics, getMapMode());
	}
				
	private void setUserScale(int scale){
		userScale = scale/100f;
		PrintHelperUtil.setScale(scale);
	}
				
	private String getDisplayScale(int scale) {
		return String.valueOf(scale) + "%"; //$NON-NLS-1$
	}

	public void setPercentScaling(int userScale){
		setUserScale(userScale);
	}
	/**/
}
