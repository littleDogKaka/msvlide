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
		if(NAME.isEmpty()){
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
			exeFile = project.getFolder(MSetting.FOLDER_UMC).getFile(MSetting.UMC_RUNFILE_NAME);
		}else if(MODE.equals(LaunchConfig.LAUNCH_CONFIG_MODE_PMC)){
			exeFile = project.getFolder(MSetting.FOLDER_PMC).getFile(MSetting.PMC_RUNFILE_NAME);
		}else {
			exeFile = project.getFolder(MSetting.FOLDER_BIN).getFile(NAME + MSetting.FILE_RUNNABLE_SUFFIX);
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

////org.eclipse.debug.ui.ATTR_CAPTURE_STDIN_FILE=${workspace_loc:/www/bin/main.m.bc} //input
//String inputFilePath = config.getAttribute("org.eclipse.debug.ui.ATTR_CAPTURE_STDIN_FILE", "");
//
////org.eclipse.debug.ui.ATTR_CAPTURE_IN_FILE=${workspace_loc:/www/src/main.m}
//String outputFilePath = config.getAttribute("org.eclipse.debug.ui.ATTR_CAPTURE_IN_FILE", "");
//
////org.eclipse.debug.ui.ATTR_APPEND_TO_FILE=true
//boolean append = config.getAttribute("org.eclipse.debug.ui.ATTR_APPEND_TO_FILE", false);
//
//if(CommonTab.isLaunchInBackground(config)){
//	//org.eclipse.debug.ui.ATTR_LAUNCH_IN_BACKGROUND=true
//	
//}else{
//	
//}