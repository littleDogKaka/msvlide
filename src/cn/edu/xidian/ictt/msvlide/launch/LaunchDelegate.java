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
//import org.eclipse.debug.ui.CommonTab;

import cn.edu.xidian.ictt.msvlide.console.MConsole;
import cn.edu.xidian.ictt.msvlide.project.util.MSetting;
import cn.edu.xidian.ictt.msvlide.project.util.PType;
import cn.edu.xidian.ictt.msvlide.project.util.Property;

public class LaunchDelegate extends LaunchConfigurationDelegate{

	public static final String DELIMITER = " ";
	
	@Override
	public void launch(ILaunchConfiguration config, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {

		String Mode = config.getAttribute(LaunchConfig.LAUNCH_CONFIGURATION_MODE_KEY, mode);
		String projectName = config.getAttribute(LaunchConfig.LAUNCH_CONFIGURATION_PROJECT_NAME_KEY, "");
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		
		if(project == null){
			return;
		}
		
		String args = Property.get(project, PType.CMDLINEARGS);
		File wd = null;
		
		IFile exeFile = null;
		if(Mode.equals(LaunchConfig.LAUNCH_CONFIGURATION_MODE_UMC)){
			exeFile = project.getFolder(MSetting.FOLDER_OUT_UMC).getFile(MSetting.UMC_RUNFILE_NAME);
			wd = project.getFolder(MSetting.FOLDER_OUT_UMC).getRawLocation().toFile();
		}else if(Mode.equals(LaunchConfig.LAUNCH_CONFIGURATION_MODE_PMC)){
			exeFile = project.getFolder(MSetting.FOLDER_OUT_PMC).getFile(MSetting.PMC_RUNFILE_NAME);
			wd =  project.getFolder(MSetting.FOLDER_OUT_PMC).getRawLocation().toFile();
		}else {
			exeFile = project.getFolder(MSetting.FOLDER_BIN).getFile(projectName + MSetting.FILE_RUNNABLE_SUFFIX);
			wd =  new File(Property.get(project, PType.WORKINGDIR));
		}
		 
		if(!exeFile.exists()){
			MConsole.print("ERROR: " + exeFile.getName() + ": No such file.",true);
			return;
		}
		
		if(!wd.exists() || !wd.canWrite() || !wd.canRead()){
			MConsole.print("ERROR: " +  wd.getAbsolutePath() + ": don't exist or cannot read or write.",true);
			return;
		}
		
		StringBuilder cmdBuilder = new StringBuilder();
		cmdBuilder.append(exeFile.getRawLocation().toString());
		cmdBuilder.append(DELIMITER);
		
		if(args == null){
			args = "";
		}
		String[] argSet = args.split(DELIMITER);
		for(String arg: argSet){
			if(!arg.isEmpty()){
				cmdBuilder.append(arg);
				cmdBuilder.append(DELIMITER);
			}
		}
		
		String cmd = cmdBuilder.toString();
		//System.out.println(cmd);
		
		// add process type to process attributes
		Map<String, String> processAttributes = new HashMap<String, String>();
		processAttributes.put(IProcess.ATTR_PROCESS_TYPE, projectName);
		
		launch.setAttribute(IProcess.ATTR_CMDLINE , cmd);
		Process p = DebugPlugin.exec(cmd.split(DELIMITER), wd);
		DebugPlugin.newProcess(launch, p, project.getName(),processAttributes);
		
	}
	

}


//if (CommonTab.isLaunchInBackground(config)) {
//System.out.println("background");
//// refresh resources after process finishes
///*
// * if (RefreshTab.getRefreshScope(configuration) != null) {
// * BackgroundResourceRefresher refresher = new
// * BackgroundResourceRefresher(configuration, process);
// * refresher.startBackgroundRefresh(); }
// */
//} else {
//// wait for process to exit
//while (!process.isTerminated()) {
//	try {
//		if (monitor.isCanceled()) {
//			process.terminate();
//			break;
//		}
//		Thread.sleep(50);
//	} catch (InterruptedException e) {}
//}
//// refresh resources
//}



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