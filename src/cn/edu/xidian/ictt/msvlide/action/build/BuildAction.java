package cn.edu.xidian.ictt.msvlide.action.build;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import cn.edu.xidian.ictt.msvlide.project.builder.MSVLBuilder;
import cn.edu.xidian.ictt.msvlide.project.util.MProject;
import cn.edu.xidian.ictt.msvlide.project.util.MSetting;

public abstract class BuildAction implements IWorkbenchWindowActionDelegate{

	protected IWorkbenchWindow window;
	protected IProject project = null;
	
	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
	
	

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		project = MProject.get(selection);
	}

	@Override
	public void dispose() {
		
	}

	class Build implements IRunnableWithProgress{
		private IProject project;
		private String mode;
		
		public Build(IProject project, String mode){
			this.project = project;
			this.mode = mode;
		}
		
		@Override
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException{
			try {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put(MSetting.BUILD_MAP_KEY_MODE, mode);
				project.build(MSVLBuilder.FULL_BUILD, MSVLBuilder.BUILDER_ID, map, monitor);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}
}
