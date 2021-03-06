package com.packtpub.e4.clock.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.progress.IProgressConstants2;
import org.eclipse.ui.statushandlers.StatusManager;

import com.packtpub.e4.clock.ui.Activator;

public class HelloHandler extends AbstractHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Job job = new Job("About to say hello") {
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.
			 * IProgressMonitor)
			 */
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					SubMonitor subMonitor = SubMonitor.convert(monitor, "Preparing", 5000);
					subMonitor = null;	// To test error handle
					for (int i = 0; i < 50 && subMonitor.isCanceled() == false; i++) {
						if (i == 10) {
							subMonitor.subTask("Doing something");
						} else if (i == 12) {
							checkDozen(subMonitor.newChild(100));
							continue;
						} else if (i == 25) {
							subMonitor.subTask("Doing something else");
						} else if (i == 40) {
							subMonitor.subTask("Nearly there");
						}

						Thread.sleep(100);
						subMonitor.worked(100);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (NullPointerException e) {
					return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Programming bug?", e);

//					StatusManager statusManager = StatusManager.getManager();
//					Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Programming bug?", e);
//					statusManager.handle(status, StatusManager.LOG | StatusManager.SHOW);
				} finally {
					if (monitor != null) {
						monitor.done();
					}
				}

				if (monitor.isCanceled() == false) {
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							MessageDialog.openInformation(null, "Hello", "World");
						}
					});
				}
				return Status.OK_STATUS;
			}

			private void checkDozen(IProgressMonitor monitor) {
				if (monitor == null) {
					monitor = new NullProgressMonitor();
				}

				try {
					monitor.beginTask("Checking a dozen", 12);
					for (int i = 0; i < 12; i++) {
						Thread.sleep(10);
						monitor.worked(1);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					monitor.done();
				}
			}
		};

		ICommandService service = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
		Command command = service == null ? null : service.getCommand("com.packtpub.e4.clock.ui.command.hello");
		if (command != null) {
			job.setProperty(IProgressConstants2.COMMAND_PROPERTY, ParameterizedCommand.generateCommand(command, null)); // Not
																														// working..
			job.setProperty(IProgressConstants2.ICON_PROPERTY,
					ImageDescriptor.createFromURL(HelloHandler.class.getResource("/icons/sample.gif")));
			job.setProperty(IProgressConstants2.SHOW_IN_TASKBAR_ICON_PROPERTY, Boolean.TRUE);
		}
		job.schedule();
		return null;
	}

}
