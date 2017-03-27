package cn.edu.xidian.ictt.msvlide.launch.shortcut;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;

import cn.edu.xidian.ictt.msvlide.Activator;
import cn.edu.xidian.ictt.msvlide.console.MConsole;
import cn.edu.xidian.ictt.msvlide.launch.config.LaunchConfig;
import cn.edu.xidian.ictt.msvlide.project.builder.PMCBuilder;
import cn.edu.xidian.ictt.msvlide.project.util.MSetting;

public class PMCSShortcut extends LaunchShortcut{

	@Override
	protected void runStartFromFile(IProject project, IFile file, String mode) {
		if(!file.getName().endsWith(MSetting.FILE_PROPERTY_SUFFIX)){
			return;
		}
		
		if(!MSetting.FOLDER_SRC.equals(file.getParent().getName())){
			MConsole.print("ERROR: " + MSetting.FILE_PROPERTY_SUFFIX + " file must be placed in " + MSetting.FOLDER_SRC, true);
			return;
		}
		
		HashMap<String,String> map = new HashMap<String,String>();
		map.put(MSetting.BUILD_MAP_KEY_MODE, MSetting.BUILD_MODE_PMC_S);
		map.put(MSetting.BUILD_MAP_KEY_FILE_PROPERTY, file.getName());
		
		try {
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell());
			dialog.run(true, true, new Build(project,PMCBuilder.BUILDER_ID,map));
			
			ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
			
			String name = project.getFolder(MSetting.FOLDER_PMC).getFile(file.getName().substring(0, file.getName().length() - 2) + MSetting.FILE_RUNNABLE_SUFFIX).getRawLocation().toOSString();
			ILaunchConfiguration config = LaunchConfig.find(LaunchConfig.LAUNCH_CONFIG_MODE_PMC);
			ILaunchConfigurationWorkingCopy wc = config.getWorkingCopy();
			wc.setAttribute(LaunchConfig.LAUNCH_CONFIG_KEY_MODE, LaunchConfig.LAUNCH_CONFIG_MODE_PMC);
			wc.setAttribute(LaunchConfig.LAUNCH_CONFIG_KEY_PROJECT_NAME, project.getName());
			wc.setAttribute(LaunchConfig.LAUNCH_CONFIG_KEY_RUN_FILENAME, name);
			config = wc.doSave();
			config.launch(LaunchConfig.LAUNCH_CONFIG_MODE_PMC, null, false);
		} catch (CoreException | InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void runStartFromFolder(IProject project, IFolder folder, String mode) {}

	@Override
	protected void runStartFromProject(IProject project, String mode) {}
}
