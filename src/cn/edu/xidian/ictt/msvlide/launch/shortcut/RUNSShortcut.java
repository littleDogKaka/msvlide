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
import org.eclipse.jface.dialogs.ProgressMonitorDialog;

import cn.edu.xidian.ictt.msvlide.Activator;
import cn.edu.xidian.ictt.msvlide.console.MConsole;
import cn.edu.xidian.ictt.msvlide.launch.config.LaunchConfig;
import cn.edu.xidian.ictt.msvlide.project.builder.MSVLBuilder;
import cn.edu.xidian.ictt.msvlide.project.util.MSetting;


public class RUNSShortcut extends LaunchShortcut {
	
	protected void runStartFromFile(IProject project, IFile file,String mode){
		if(!file.getName().endsWith(MSetting.FILE_MAIN_SUFFIX)){
			return;
		}
		
		if(!MSetting.FOLDER_SRC.equals(file.getParent().getName())){
			MConsole.print("ERROR: " + MSetting.FILE_MAIN_SUFFIX + " file must be placed in " + MSetting.FOLDER_SRC, true);
			return;
		}
		compileAndRun(project,mode);
	}

	@Override
	protected void runStartFromFolder(IProject project, IFolder folder, String mode) {}

	@Override
	protected void runStartFromProject(IProject project, String mode) {
		compileAndRun(project,mode);
	}
	
	private void compileAndRun(IProject project, String mode){
		try {
			// compile
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell());
			HashMap<String,String> map = new HashMap<String,String>();
			map.put(MSetting.BUILD_MAP_KEY_MODE, MSetting.BUILD_MODE_RUN_S);
			dialog.run(true, true, new Build(project,MSVLBuilder.BUILDER_ID,map));
			// run
			ILaunchConfiguration config = LaunchConfig.find(mode, project, project.getName());
			config.launch(mode, null, false);
			
			Thread.sleep(500);
			ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException | InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}
