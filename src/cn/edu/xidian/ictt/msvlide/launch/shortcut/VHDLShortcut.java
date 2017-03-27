package cn.edu.xidian.ictt.msvlide.launch.shortcut;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

import cn.edu.xidian.ictt.msvlide.console.MConsole;
import cn.edu.xidian.ictt.msvlide.launch.config.LaunchConfig;
import cn.edu.xidian.ictt.msvlide.project.util.MSetting;

public class VHDLShortcut extends LaunchShortcut{

	@Override
	protected void runStartFromFile(IProject project, IFile file, String mode) {
		if(!file.getName().endsWith(MSetting.FILE_VHDL_SUFFIX)){
			return;
		}
		
		if(!MSetting.FOLDER_SRC_VHDL.equals(file.getParent().getName())){
			MConsole.print("ERROR: " + MSetting.FILE_VHDL_SUFFIX + " file must be placed in " + MSetting.FOLDER_SRC_VHDL, true);
			return;
		}
		
		try {
			String args = file.getRawLocation().toOSString() +" "+ file.getProject().getFolder(MSetting.FOLDER_SRC).getRawLocation().toOSString() + "\\" + file.getName() + MSetting.FILE_MAIN_SUFFIX;
			ILaunchConfiguration config = LaunchConfig.find(LaunchConfig.LAUNCH_CONFIG_MODE_CONVERT);
			ILaunchConfigurationWorkingCopy wc = config.getWorkingCopy();
			wc.setAttribute(LaunchConfig.LAUNCH_CONFIG_KEY_MODE, LaunchConfig.LAUNCH_CONFIG_MODE_CONVERT);
			wc.setAttribute(LaunchConfig.LAUNCH_CONFIG_KEY_PROJECT_NAME, file.getProject().getName());
			wc.setAttribute(LaunchConfig.LAUNCH_CONFIG_KEY_RUN_FILENAME, MSetting.CONVERT_VHDL_TO_MSVL);
			wc.setAttribute(LaunchConfig.LAUNCH_CONFIG_KEY_WD, file.getProject().getFolder(MSetting.FOLDER_SRC).getRawLocation().toOSString());
			wc.setAttribute(LaunchConfig.LAUNCH_CONFIG_KEY_ARGS, args);
			config = wc.doSave();
			config.launch(LaunchConfig.LAUNCH_CONFIG_MODE_CONVERT, null, true);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	protected void runStartFromFolder(IProject project, IFolder folder, String mode) {}

	@Override
	protected void runStartFromProject(IProject project, String mode) {}

}
