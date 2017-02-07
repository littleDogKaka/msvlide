package cn.edu.xidian.ictt.msvlide.launch;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;

import cn.edu.xidian.ictt.msvlide.console.MConsole;
import cn.edu.xidian.ictt.msvlide.project.util.MSetting;


public class LaunchDelegate extends LaunchConfigurationDelegate{

	public static final String DELIMITER = " ";
	
	@Override
	public void launch(ILaunchConfiguration config, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		
		String MODE = config.getAttribute(LaunchConfig.LAUNCH_CONFIG_KEY_MODE, mode);
		String NAME = config.getAttribute(LaunchConfig.LAUNCH_CONFIG_KEY_PROJECT_NAME, "");
		String RUNFILENAME = config.getAttribute(LaunchConfig.LAUNCH_CONFIG_KEY_RUN_FILENAME, NAME);
		if(RUNFILENAME.isEmpty()){
			return;
		}
		
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(NAME);
		if(project == null){
			return;
		}
		
		File wd = null;
		String WDIR = config.getAttribute(LaunchConfig.LAUNCH_CONFIG_KEY_WD, "");
		if(WDIR.isEmpty()){
			wd = project.getRawLocation().toFile();
		}else{
			wd = new File(WDIR);
		}
		
		if(!wd.exists() || !wd.canWrite() || !wd.canRead()){
			MConsole.print("ERROR: " +  wd.getAbsolutePath() + ": don't exist or cannot read or write.",true);
			return;
		}

		IFile exeFile = null;
		if(MODE.equals(LaunchConfig.LAUNCH_CONFIG_MODE_UMC)){
			exeFile = project.getFolder(MSetting.FOLDER_UMC).getFile(RUNFILENAME + MSetting.FILE_RUNNABLE_SUFFIX);
		}else if(MODE.equals(LaunchConfig.LAUNCH_CONFIG_MODE_PMC)){
			exeFile = project.getFolder(MSetting.FOLDER_PMC).getFile(RUNFILENAME + MSetting.FILE_RUNNABLE_SUFFIX);
		}else {
			exeFile = project.getFolder(MSetting.FOLDER_BIN).getFile(RUNFILENAME + MSetting.FILE_RUNNABLE_SUFFIX);
		}
		 
		if(!exeFile.exists()){
			MConsole.print("ERROR: " + exeFile.getName() + ": No such file.",true);
			return;
		}
		
		String ARGS = config.getAttribute(LaunchConfig.LAUNCH_CONFIG_KEY_ARGS, "").trim();
		
		StringBuilder cmdBuilder = new StringBuilder();
		cmdBuilder.append(exeFile.getRawLocation().toString());
		cmdBuilder.append(DELIMITER);
		cmdBuilder.append(ARGS);
		String cmd = cmdBuilder.toString();
		//System.out.println(cmd);
		
		// add process type to process attributes
		Map<String, String> processAttributes = new HashMap<String, String>();
		processAttributes.put(IProcess.ATTR_PROCESS_TYPE, NAME);
		launch.setAttribute(IProcess.ATTR_CMDLINE , cmd);
		Process p = DebugPlugin.exec(cmd.split(DELIMITER), wd);
		DebugPlugin.newProcess(launch, p, NAME, processAttributes);
	}
}
