package com.packtpub.e4.application.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.osgi.service.log.LogService;
import org.osgi.service.prefs.BackingStoreException;

public class Hello {
	private Label label;
	private Button button;

	@Inject
	@Optional
	private LogService log;

	@Inject
	private MWindow window;

	@Inject
	@Named("math.random")
	private Object random; // Cache?

	@Inject
	@Preference(nodePath = "com.packtpub.e4.application")
	private IEclipsePreferences prefs;

	@Inject
	@Preference(nodePath = "com.packtpub.e4.application", value = "greeting")
	private String greeting;

	@Inject
	private UISynchronize ui;

	@PostConstruct
	public void create(Composite parent, EMenuService menu) {
		label = new Label(parent, SWT.NONE);
		label.setText(greeting + " " + window.getLabel() + " " + random);

		if (log != null) { // @Optional should null check.
			log.log(LogService.LOG_ERROR, "Hello, " + window.getLabel());
		}

		button = new Button(parent, SWT.PUSH);
		button.setText("Do not push");
		button.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				button.setEnabled(false);
				new Job("Button Pusher") {
					@Override
					protected IStatus run(IProgressMonitor monitor) {
						ui.asyncExec(new Runnable() {
							@Override
							public void run() {
								button.setEnabled(true);
							}
						});
						return Status.OK_STATUS;
					}
				}.schedule(1000);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		menu.registerContextMenu(parent, "com.packtpub.e4.application.popupmenu.hello");
	}

	@Focus
	public void focus() {
		label.setFocus();
	}

	@Inject
	@Optional
	public void setSelection(@Named(IServiceConstants.ACTIVE_SELECTION) Object selection) {
		if (selection != null) {
			label.setText(selection.toString());
		}
	}

	@Inject
	@Optional
	public void receiveEvent(@UIEventTopic("rainbow/colour") String data) throws BackingStoreException {
		// label.setText(data);
		prefs.put("greeting", "I like " + data);
		prefs.flush();
	}

	@Inject
	@Optional
	void setText(@Preference(nodePath = "com.packtpub.e4.application", value = "greeting") String text) {
		if (text != null && label != null && !label.isDisposed()) {
			label.setText(text);
		}
	}

}
