package com.packtpub.e4.clock.ui.views;

import java.net.URL;
import java.util.TimeZone;

import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.part.ViewPart;

import com.packtpub.e4.clock.ui.internal.TimeZoneComparator;
import com.packtpub.e4.clock.ui.internal.TimeZoneContentProvider;
import com.packtpub.e4.clock.ui.internal.TimeZoneDialog;
import com.packtpub.e4.clock.ui.internal.TimeZoneLabelProvider;
import com.packtpub.e4.clock.ui.internal.TimeZoneSelectionListener;
import com.packtpub.e4.clock.ui.internal.TimeZoneViewerComparator;

public class TimeZoneTreeView extends ViewPart {
	private TreeViewer treeViewer;
	private TimeZoneSelectionListener selectionListener;

	public TimeZoneTreeView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		ResourceManager rm = JFaceResources.getResources();
		// Resource life cycle is same as Control's life cycle.
		LocalResourceManager lrm = new LocalResourceManager(rm, parent);
		ImageRegistry ir = new ImageRegistry(lrm);
		URL sample = getClass().getResource("/icons/sample.gif");
		ir.put("sample", ImageDescriptor.createFromURL(sample));

		treeViewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		FontRegistry fr = JFaceResources.getFontRegistry();
		treeViewer.setLabelProvider(new DelegatingStyledCellLabelProvider(new TimeZoneLabelProvider(ir, fr)));

		treeViewer.setContentProvider(new TimeZoneContentProvider());

		treeViewer.setData("REVERSE", Boolean.TRUE);
		treeViewer.setComparator(new TimeZoneViewerComparator());

		// treeViewer.setFilters(new ViewerFilter[] { new
		// TimeZoneViewerFilter("GMT") });
		treeViewer.setExpandPreCheckFilters(true);

		treeViewer.setInput(new Object[] { TimeZoneComparator.getTimeZones() });

		treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				Viewer viewer = event.getViewer();
				Shell shell = viewer.getControl().getShell();
				ISelection sel = viewer.getSelection();
				Object selectedValue;
				if (!(sel instanceof IStructuredSelection) || sel.isEmpty()) {
					selectedValue = null;
				} else {
					selectedValue = ((IStructuredSelection) sel).getFirstElement();
					if (selectedValue instanceof TimeZone) {
						TimeZone timeZone = (TimeZone) selectedValue;
						new TimeZoneDialog(shell, timeZone).open();
					}
				}
			}
		});
		getSite().setSelectionProvider(treeViewer);

		/*
		 * Selection sync
		 */
		selectionListener = new TimeZoneSelectionListener(treeViewer, getSite().getPart());
		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(selectionListener);
	}

	@Override
	public void setFocus() {
		treeViewer.getControl().setFocus();
	}

	@Override
	public void dispose() {
		if (selectionListener != null) {
			getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(selectionListener);
			selectionListener = null;
		}
		super.dispose();
	}

}
