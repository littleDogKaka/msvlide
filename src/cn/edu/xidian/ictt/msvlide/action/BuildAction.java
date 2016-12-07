package cn.edu.xidian.ictt.msvlide.action;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import cn.edu.xidian.ictt.msvlide.project.util.MProject;

public class BuildAction implements IWorkbenchWindowActionDelegate{

	private IWorkbenchWindow window;
	private IProject project = null;
	
	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
	
	@Override
	public void run(IAction action) {
		if(window == null || project == null){
			return;
		}
		IProject project = MProject.get();
		if(project == null){
			String[] btns = {"OK"};
			MessageDialog dialog = new MessageDialog(window.getShell(),"MSVL Project", null,"Please choose a MSVL project!", MessageDialog.WARNING,btns,0); 
			dialog.open();
			return;
		}
		try {
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(window.getShell());
			dialog.run(true, true, new Build(project));
		} catch ( InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		
	}

	@Override
	public void dispose() {
		
	}

	class Build implements IRunnableWithProgress{
		private IProject project;
		public Build(IProject project){
			this.project = project;
		}
		
		@Override
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException{
			try {
				project.build(6, monitor);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}
}
