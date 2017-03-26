package cn.edu.xidian.ictt.msvlide.launch.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;

import cn.edu.xidian.ictt.msvlide.console.MConsole;
import cn.edu.xidian.ictt.msvlide.project.util.PType;
import cn.edu.xidian.ictt.msvlide.project.util.Property;


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
			String path = project.getLocation().toOSString();
			wd = new File(path);
		}else{
			wd = new File(WDIR);
		}
		
		if(!wd.exists() || !wd.canWrite() || !wd.canRead()){
			MConsole.print("ERROR: " +  wd.getAbsolutePath() + ": don't exist or cannot read or write.",true);
			return;
		}
		
		String ARGS = "";
		if(LaunchConfig.LAUNCH_CONFIG_MODE_CONVERT.equals(MODE)){
			ARGS = config.getAttribute(LaunchConfig.LAUNCH_CONFIG_KEY_ARGS, "").trim();
		}else{
			ARGS = Property.get(project, PType.CMDLINEARGS);
		}
		
		StringBuilder cmdBuilder = new StringBuilder();
		cmdBuilder.append(RUNFILENAME);
		cmdBuilder.append(DELIMITER);
		cmdBuilder.append(ARGS);
		String cmd = cmdBuilder.toString();
		
		// add process type to process attributes
		Map<String, String> processAttributes = new HashMap<String, String>();
		processAttributes.put(IProcess.ATTR_PROCESS_TYPE, NAME);
		launch.setAttribute(IProcess.ATTR_CMDLINE , cmd);
		Process p = DebugPlugin.exec(cmd.split(DELIMITER), wd);
		DebugPlugin.newProcess(launch, p, NAME, processAttributes);
		//
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					p.waitFor();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				try {
					ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
