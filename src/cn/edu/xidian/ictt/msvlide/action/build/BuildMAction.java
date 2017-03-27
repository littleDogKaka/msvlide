package cn.edu.xidian.ictt.msvlide.action.build;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;

import cn.edu.xidian.ictt.msvlide.launch.config.LaunchConfig;
import cn.edu.xidian.ictt.msvlide.project.util.MSetting;

public class BuildMAction extends BuildAction{
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
			dialog.run(true, true, new Build(project,MSetting.BUILD_MODE_RUN_M));
			
			ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
			Thread.sleep(100);
			
			IFile runFile = project.getFolder(MSetting.FOLDER_BIN).getFile(project.getName() + MSetting.FILE_RUNNABLE_SUFFIX);
			if(runFile.isAccessible()){
				ILaunchConfiguration config = LaunchConfig.find(LaunchConfig.LAUNCH_CONFIG_MODE_RUN);
				ILaunchConfigurationWorkingCopy wc = config.getWorkingCopy();
				wc.setAttribute(LaunchConfig.LAUNCH_CONFIG_KEY_MODE, LaunchConfig.LAUNCH_CONFIG_MODE_RUN);
				wc.setAttribute(LaunchConfig.LAUNCH_CONFIG_KEY_PROJECT_NAME, project.getName());
				wc.setAttribute(LaunchConfig.LAUNCH_CONFIG_KEY_RUN_FILENAME, runFile.getRawLocation().toOSString());
				config = wc.doSave();
				try {
					config.launch(LaunchConfig.LAUNCH_CONFIG_MODE_RUN, null, true);
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
			Thread.sleep(100);
			ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch ( InvocationTargetException | InterruptedException | CoreException e) {
			e.printStackTrace();
		}
	}
}
