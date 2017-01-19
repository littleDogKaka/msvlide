package cn.edu.xidian.ictt.msvlide.launch;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;

import cn.edu.xidian.ictt.msvlide.Activator;
import cn.edu.xidian.ictt.msvlide.console.MConsole;
import cn.edu.xidian.ictt.msvlide.project.builder.UMCBuilder;
import cn.edu.xidian.ictt.msvlide.project.util.MSetting;

public class UMCShortcut extends LaunchShortcut {

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
		map.put(MSetting.BUILD_MAP_KEY_MODE, MSetting.BUILD_MODE_UMC);
		map.put(MSetting.BUILD_MAP_KEY_FILE_PROPERTY, file.getName());
		
		try {
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell());
			dialog.run(true, true, new Build(project,UMCBuilder.BUILDER_ID,map));
			
			ILaunchConfiguration config = LaunchConfig.find(LaunchConfig.LAUNCH_CONFIG_MODE_UMC, project);
			config.launch(LaunchConfig.LAUNCH_CONFIG_MODE_UMC, null, false);
		} catch (CoreException | InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
	}

}
