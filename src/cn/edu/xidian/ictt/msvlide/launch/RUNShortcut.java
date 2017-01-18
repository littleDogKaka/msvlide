package cn.edu.xidian.ictt.msvlide.launch;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;

import cn.edu.xidian.ictt.msvlide.Activator;
import cn.edu.xidian.ictt.msvlide.project.builder.MSVLBuilder;
import cn.edu.xidian.ictt.msvlide.project.util.MSetting;


public class RUNShortcut extends LaunchShortcut {
	
	protected void runStartFromFile(IProject project, IFile file,String mode){
		if(!file.getName().endsWith(MSetting.FILE_MAIN_SUFFIX)){
			return;
		}

		Map<String,String> map = new HashMap<String,String>();
		map.put(MSetting.BUILD_MAP_KEY_MODE, MSetting.BUILD_MODE_RUN);
		map.put(MSetting.BUILD_MAP_KEY_FILE_MAIN, file.getFullPath().toOSString());
		
		try {
			// compile
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell());
			dialog.run(true, true, new Build(project,MSVLBuilder.BUILDER_ID,map));
			// run
			ILaunchConfiguration config = LaunchConfig.find(mode, project);
			config.launch(mode, null, false);
		} catch (CoreException | InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
