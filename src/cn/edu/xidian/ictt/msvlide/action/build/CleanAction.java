package cn.edu.xidian.ictt.msvlide.action.build;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import cn.edu.xidian.ictt.msvlide.console.MConsole;
import cn.edu.xidian.ictt.msvlide.project.util.MProject;
import cn.edu.xidian.ictt.msvlide.project.util.MSetting;

public class CleanAction implements IWorkbenchWindowActionDelegate{

	private IWorkbenchWindow window;
	private IProject project = null;
	
	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
	
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		project = MProject.get(selection);
	}
	
	@Override
	public void run(IAction action) {
		if(window == null){
			return;
		}
		
		if(project == null){
			String[] btns = {"OK"};
			MessageDialog dialog = new MessageDialog(window.getShell(),"MSVL Project", null,"Please choose a MSVL project!", MessageDialog.WARNING,btns,0); 
			dialog.open();
			return;
		}
		try {
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(window.getShell());
			dialog.run(true, true, new Clean(project));
		} catch ( InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void dispose() {}
	
	class Clean implements IRunnableWithProgress{

		private IProject project;
		
		public Clean(IProject project){
			this.project = project;
		}
		
		@Override
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			if(!MProject.isMSVLProject(project)){
				return;
			}
			try{
				project.deleteMarkers(null, true, IResource.DEPTH_INFINITE);
				ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
				File[] folders = new File[3];
				folders[0] = project.getFolder(MSetting.FOLDER_BIN).getRawLocation().toFile();
				folders[1] = project.getFolder(MSetting.FOLDER_PMC).getRawLocation().toFile();
				folders[2] = project.getFolder(MSetting.FOLDER_UMC).getRawLocation().toFile();

				monitor.beginTask("cleaning...", 4);
				
				for(int i = 0; i < 3; ++i){
					File[] files = folders[i].listFiles();
					for(File file:files){
						try{
							file.delete();
						}catch(Exception e){
							e.printStackTrace();
						}
					}
					monitor.worked(1);
				}
				ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
				MConsole.clear();
				monitor.done();
			}catch(CoreException e){
				e.printStackTrace();
			}
		}
	}
}
