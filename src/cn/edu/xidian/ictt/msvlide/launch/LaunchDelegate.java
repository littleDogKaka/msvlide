package cn.edu.xidian.ictt.msvlide.launch;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.debug.ui.CommonTab;

import cn.edu.xidian.ictt.msvlide.project.util.MProject;
import cn.edu.xidian.ictt.msvlide.project.util.PType;
import cn.edu.xidian.ictt.msvlide.project.util.Property;

public class LaunchDelegate extends LaunchConfigurationDelegate{

	public static final String DELIMITER = " ";
	
	@Override
	public void launch(ILaunchConfiguration config, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {

		IProject project = MProject.get();
		
		if(project == null){
			return;
		}
		String projectName = project.getName();
		String args = Property.get(project, PType.CMDLINEARGS);
		String wd = Property.get(project, PType.WORKINGDIR);
		
		IFile exeFile = project.getFolder("bin").getFile(projectName + ".exe");
		if(!exeFile.exists()){
			System.out.println("The executable program does not exist.");
			return;
		}
		
		File wdir = new File(wd);
		if(!wdir.exists() || !wdir.canWrite() || !wdir.canRead()){
			System.out.println("Working directory does not exist or is unreadable or unwritable.");
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
		
		Process p = DebugPlugin.exec(cmd.split(DELIMITER), wdir);
		
		IProcess process = null;
		
		// add process type to process attributes
		Map<String, String> processAttributes = new HashMap<String, String>();
		processAttributes.put(IProcess.ATTR_PROCESS_TYPE, projectName);
		
		launch.setAttribute(IProcess.ATTR_CMDLINE , cmd);
		
		process= DebugPlugin.newProcess(launch, p, project.getName(),processAttributes);
		
		if (CommonTab.isLaunchInBackground(config)) {
			// refresh resources after process finishes
			/*
			 * if (RefreshTab.getRefreshScope(configuration) != null) {
			 * BackgroundResourceRefresher refresher = new
			 * BackgroundResourceRefresher(configuration, process);
			 * refresher.startBackgroundRefresh(); }
			 */
		} else {
			// wait for process to exit
			while (!process.isTerminated()) {
				try {
					if (monitor.isCanceled()) {
						process.terminate();
						break;
					}
					Thread.sleep(50);
				} catch (InterruptedException e) {}
			}
			// refresh resources
			
		}
		
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