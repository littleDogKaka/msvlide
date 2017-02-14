package cn.edu.xidian.ictt.msvlide.launch.shortcut;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;

import cn.edu.xidian.ictt.msvlide.project.builder.MSVLBuilder;
import cn.edu.xidian.ictt.msvlide.project.util.MProject;

public abstract class LaunchShortcut implements ILaunchShortcut{

	@Override
	public void launch(ISelection selection, String mode) {
		if (selection instanceof IStructuredSelection) {
			Object obj = ((IStructuredSelection) selection).getFirstElement();

			if (obj instanceof IResource){
				IProject project = ((IResource)obj).getProject();
				if(project == null){
					return;
				}
				
				if(MProject.isMSVLProject(project)){
					if(obj instanceof IFile){
						IFile file = (IFile)obj;
						runStartFromFile(project,file,mode);
					} else if(obj instanceof IFolder){
						IFolder folder = (IFolder)obj;
						runStartFromFolder(project, folder, mode);
					}else{
						runStartFromProject(project, mode);
					}
				}
			}
		}
		try {
			Thread.sleep(500);
			ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (InterruptedException | CoreException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void launch(IEditorPart part, String mode) {
		IFile file = part.getEditorInput().getAdapter(IFile.class);
		IProject project = file.getProject();
		if(project == null){
			return;
		}else if(MProject.isMSVLProject(project)){
			runStartFromFile(project,file,mode);
		}
	}
	
	protected abstract void runStartFromFile(IProject project,IFile file,String mode);
	protected abstract void runStartFromFolder(IProject project,IFolder folder,String mode);
	protected abstract void runStartFromProject(IProject project,String mode);
	
	class Build implements IRunnableWithProgress{
		private IProject project;
		private String builderName;
		private Map<String,String> map;
		
		public Build(IProject project,String builderName,Map<String,String> map){
			this.project = project;
			this.builderName = builderName;
			this.map = map;
		}
		
		@Override
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException{
			try {
				project.build(MSVLBuilder.FULL_BUILD, builderName, map, monitor);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

}
