package cn.edu.xidian.ictt.msvlide.action;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import cn.edu.xidian.ictt.msvlide.console.DisplayOutput;
import cn.edu.xidian.ictt.msvlide.launch.LaunchConfig;
import cn.edu.xidian.ictt.msvlide.project.util.MProject;
import cn.edu.xidian.ictt.msvlide.project.util.MSetting;

public class UMCheckerAction implements IWorkbenchWindowActionDelegate{

	private IWorkbenchWindow window;
	private IFile file;
	
	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
	
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		if(selection == null){
			file = null;
			return;
		}
		if (selection instanceof IStructuredSelection) {
			Object obj = ((IStructuredSelection) selection).getFirstElement();
			if (obj instanceof IFile){
				file = (IFile) obj;
			}
		}else {
			file = MProject.getActiveEditorInput();
		}
	}
	
	@Override
	public void run(IAction action) {
		if(window == null){
			return;
		}
		if(file == null){
			showDialog();
			return;
		}
		String filename = file.getName();
		if(!filename.endsWith(MSetting.FILE_MAIN_SUFFIX)){
			showDialog();
			return;
		}
		
		String input = file.getRawLocation().toString().replace("/", "\\");
		File wd = file.getProject().getFolder(MSetting.FOLDER_UMC).getRawLocation().toFile();
		String[] args = {MSetting.UMCHECKER, input};
		
		try {
			Process p = Runtime.getRuntime().exec(args, null, wd);
			new Thread(new DisplayOutput(p.getInputStream(), "UMC")).start();
			new Thread(new DisplayOutput(p.getErrorStream(), "UMC")).start();
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(window.getShell());
			dialog.run(true, true, new Wait(p));
			
			ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
			Thread.sleep(100); // wait for 
			
			IProject project = file.getProject();
			IFolder out = project.getFolder(MSetting.FOLDER_UMC);
			IFile runFile = out.getFile(MSetting.UMC_RUNFILE_NAME);
			if(runFile.isAccessible()){
				ILaunchConfiguration config = LaunchConfig.find(LaunchConfig.LAUNCH_CONFIG_MODE_UMC, project);
				try {
					config.launch(LaunchConfig.LAUNCH_CONFIG_MODE_UMC, null, true);
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
			
			ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (IOException | InterruptedException | InvocationTargetException | CoreException e) {
			e.printStackTrace();
		}
	}

	private void showDialog(){
		String[] btns = {"OK"};
		MessageDialog dialog = new MessageDialog(window.getShell(),"Unified Model Checker", null,"Please choose a " + MSetting.FILE_MAIN_SUFFIX + " file in directory " + MSetting.FOLDER_SRC, MessageDialog.WARNING,btns,0); 
		dialog.open();
	}

	@Override
	public void dispose() {}

	class Wait implements IRunnableWithProgress{
		private Process p;
		public Wait(Process p){
			this.p = p;
		}
		
		@Override
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException{
			monitor.beginTask("Handling...",100);
			while(p.isAlive()){
				Thread.sleep(500);
				if(monitor.isCanceled()){
					p.destroy();
				}else{
					monitor.worked(1);
				}
			}
			monitor.done();
		}
	}
}
