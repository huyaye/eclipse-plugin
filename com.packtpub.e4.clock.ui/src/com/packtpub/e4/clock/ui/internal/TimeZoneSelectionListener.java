package com.packtpub.e4.clock.ui.internal;

import java.util.TimeZone;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;

public class TimeZoneSelectionListener implements ISelectionListener {
	private Viewer viewer;
	private IWorkbenchPart part;

	public TimeZoneSelectionListener(Viewer viewer, IWorkbenchPart part) {
		this.viewer = viewer;
		this.part = part;
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (part != this.part && selection instanceof IStructuredSelection) {
			Object selected = ((IStructuredSelection) selection).getFirstElement();
			Object current = ((IStructuredSelection) viewer.getSelection()).getFirstElement();
			if (selected != current && selected instanceof TimeZone) {
				viewer.setSelection(selection);
				if (viewer instanceof StructuredViewer) {
					// TreeView cannot expand it when it is collapsed.
					((StructuredViewer) viewer).reveal(selected);
				}
			}
		}
	}

}
