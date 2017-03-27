package cn.edu.xidian.ictt.msvlide.action.build;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;

import cn.edu.xidian.ictt.msvlide.launch.config.LaunchConfig;
import cn.edu.xidian.ictt.msvlide.project.util.MSetting;

public class UMCMAction extends MCAction{
	
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
			dialog.run(true, true, new MCBuild(file.getProject(), MSetting.BUILD_MODE_UMC_M, filename));
			
			ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
			
			IProject project = file.getProject();
			IFolder out = project.getFolder(MSetting.FOLDER_UMC);
			IFile runFile = out.getFile(filename.substring(0, filename.length() -2));
			if(runFile.isAccessible()){
				ILaunchConfiguration config = LaunchConfig.find(LaunchConfig.LAUNCH_CONFIG_MODE_UMC);
				ILaunchConfigurationWorkingCopy wc = config.getWorkingCopy();
				wc.setAttribute(LaunchConfig.LAUNCH_CONFIG_KEY_MODE, LaunchConfig.LAUNCH_CONFIG_MODE_UMC);
				wc.setAttribute(LaunchConfig.LAUNCH_CONFIG_KEY_PROJECT_NAME, project.getName());
				wc.setAttribute(LaunchConfig.LAUNCH_CONFIG_KEY_RUN_FILENAME, runFile.getRawLocation().toOSString());
				config = wc.doSave();
				try {
					config.launch(LaunchConfig.LAUNCH_CONFIG_MODE_UMC, null, true);
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
			
			//ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (InterruptedException | InvocationTargetException | CoreException e) {
			e.printStackTrace();
		}
	}

}
