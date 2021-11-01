package org.cj5x.main.node.ui;

import java.io.IOException;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.cj5x.main.App;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class Main {

	private App application;

	/**
	 * Open the window.
	 * 
	 * @wbp.parser.entryPoint
	 */
	public void open() {
		Display display = Display.getDefault();
		Shell shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("CJ5X");
		shell.setLayout(new GridLayout(2, false));

		TabFolder mainTab = new TabFolder(shell, SWT.NONE);
		GridData gd_mainTab = new GridData(SWT.FILL, SWT.FILL, false, true, 2, 1);
		gd_mainTab.widthHint = 160;
		mainTab.setLayoutData(gd_mainTab);

		TabItem tbtmAdmin = new TabItem(mainTab, SWT.NONE);
		tbtmAdmin.setText("Admin");

		Composite compositeAdmin = new Composite(mainTab, SWT.NONE);
		tbtmAdmin.setControl(compositeAdmin);
		compositeAdmin.setLayout(new FillLayout(SWT.HORIZONTAL));

		TabItem tbtmWallet = new TabItem(mainTab, SWT.NONE);
		tbtmWallet.setText("Wallet");

		Composite composite_2 = new Composite(mainTab, SWT.NONE);
		tbtmWallet.setControl(composite_2);
		composite_2.setLayout(new GridLayout(1, false));

		Group grpImportWallet = new Group(composite_2, SWT.NONE);
		grpImportWallet.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		grpImportWallet.setToolTipText("Drag your PEM or any private key file supplied by CJ5X wallet generation.");
		grpImportWallet.setText("Import Wallet");
		grpImportWallet.setLayout(new GridLayout(1, false));

		Label lblDropFileHere = new Label(grpImportWallet, SWT.NONE);
		lblDropFileHere.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
		lblDropFileHere.setBounds(0, 0, 55, 15);
		lblDropFileHere.setText("drop file here...");

		DropTarget dropTarget = new DropTarget(composite_2, DND.DROP_MOVE);
		final FileTransfer fileTransfer = FileTransfer.getInstance();
		final TextTransfer textTransfer = TextTransfer.getInstance();
		Transfer[] transfers = new Transfer[] { textTransfer, fileTransfer };
		dropTarget.setTransfer(transfers);
		dropTarget.addDropListener(new DropTargetAdapter() {
			@Override
			public void drop(DropTargetEvent event) {
				Security.addProvider(new BouncyCastleProvider());

				if (fileTransfer.isSupportedType(event.currentDataType)) {
					String[] files = (String[]) event.data;
					
				}
			}
		});

		Button btnStart = new Button(shell, SWT.NONE);
		Button btnStop = new Button(shell, SWT.NONE);
		btnStart.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		btnStart.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				try {
					Main.this.getApplication().initialize();

					btnStop.setEnabled(true);
					btnStart.setEnabled(false);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		btnStart.setText("Start");

		btnStop.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, 1, 1));
		btnStop.setEnabled(false);

		btnStop.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				Main.this.getApplication().stop();
				btnStop.setEnabled(false);
				btnStart.setEnabled(true);
			}
		});
		btnStop.setText("Stop");

		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	public void setApplication(App app) {
		this.application = app;
	}

	public App getApplication() {
		return this.application;
	}
}
