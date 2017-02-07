package cn.edu.xidian.ictt.msvlide.action;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

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

import cn.edu.xidian.ictt.msvlide.launch.LaunchConfig;
import cn.edu.xidian.ictt.msvlide.project.builder.UMCBuilder;
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
		if(!filename.endsWith(MSetting.FILE_PROPERTY_SUFFIX)){
			showDialog();
			return;
		}
		
		
		try {
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(window.getShell());
			dialog.run(true, true, new Build(filename));
			
			ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
			Thread.sleep(100);
			
			IProject project = file.getProject();
			IFolder out = project.getFolder(MSetting.FOLDER_UMC);
			IFile runFile = out.getFile(filename.substring(0, filename.length() -2));
			if(runFile.isAccessible()){
				ILaunchConfiguration config = LaunchConfig.find(LaunchConfig.LAUNCH_CONFIG_MODE_UMC, project,filename.substring(0, filename.length() -2));
				try {
					config.launch(LaunchConfig.LAUNCH_CONFIG_MODE_UMC, null, true);
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
			
			ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (InterruptedException | InvocationTargetException | CoreException e) {
			e.printStackTrace();
		}
	}

	private void showDialog(){
		String[] btns = {"OK"};
		MessageDialog dialog = new MessageDialog(window.getShell(),"Unified Model Checker", null,"Please choose a " + MSetting.FILE_PROPERTY_SUFFIX + " file in " + MSetting.FOLDER_SRC, MessageDialog.WARNING,btns,0); 
		dialog.open();
	}

	@Override
	public void dispose() {}
	
	class Build implements IRunnableWithProgress{
		String pFilename;
		
		public Build(String pFilename){
			this.pFilename = pFilename;
		}
		
		@Override
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException{
			try{
				HashMap<String,String> map = new HashMap<String,String>();
				map.put(MSetting.BUILD_MAP_KEY_MODE, MSetting.BUILD_MODE_UMC);
				map.put(MSetting.BUILD_MAP_KEY_FILE_PROPERTY, pFilename);
				file.getProject().build(UMCBuilder.FULL_BUILD, UMCBuilder.BUILDER_ID, map, monitor);
			}catch(CoreException e){
				e.printStackTrace();
			}
		}
	}
}
