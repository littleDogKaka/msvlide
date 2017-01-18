package cn.edu.xidian.ictt.msvlide.project.builder;

import java.io.File;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import cn.edu.xidian.ictt.msvlide.console.MConsole;
import cn.edu.xidian.ictt.msvlide.project.util.MSetting;

public class PMCBuilder extends IncrementalProjectBuilder{
	
	public static final String BUILDER_ID = "cn.edu.xidian.ictt.msvlide.PMCBuilder";
	public static final String BUILDER_NAME = "cn.edu.xidian.ictt.msvlide.PMCBuilder";
	
	@Override
	protected IProject[] build(int kind, Map<String, String> map, IProgressMonitor monitor) throws CoreException {
		System.out.println("PMCBuilder");
		
		
		return null;
	}

	@Override
	protected void clean(IProgressMonitor monitor) throws CoreException {
		getProject().deleteMarkers(null, true, IResource.DEPTH_INFINITE);
		ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
		File folders = getProject().getFolder(MSetting.FOLDER_PMC).getRawLocation().toFile();
		File[] files = folders.listFiles();
		monitor.beginTask("cleaning...", files.length + 2);
		monitor.worked(1);
		for(File file:files){
			try{
				file.delete();
			}catch(Exception e){
				e.printStackTrace();
			}
			monitor.worked(1);
		}
		ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
		MConsole.clear();
		monitor.done();
	}
	
}
