package cn.edu.xidian.ictt.msvlide.project.builder;

import java.io.File;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

import cn.edu.xidian.ictt.msvlide.console.MConsole;
import cn.edu.xidian.ictt.msvlide.project.util.MSetting;

public class UMCBuilder extends IncrementalProjectBuilder{
	
	public static final String BUILDER_ID = "cn.edu.xidian.ictt.msvlide.UMCBuilder";
	public static final String BUILDER_NAME = "cn.edu.xidian.ictt.msvlide.UMCBuilder";
	
	@Override
	protected IProject[] build(int kind, Map<String, String> map, IProgressMonitor monitor) throws CoreException {
		System.out.println("UMCBuilder");
		if(map == null || map.isEmpty()){
			return null;
		}
		String mode = map.get(MSetting.BUILD_MAP_KEY_MODE);
		
		if(!MSetting.BUILD_MODE_UMC.equals(mode)){
			return null;
		}
		
		String pFilePathStr = map.get(MSetting.BUILD_MAP_KEY_FILE_PROPERTY);
		if(pFilePathStr == null || pFilePathStr.isEmpty()){
			return null;
		}
		
		IPath pPath = Path.fromOSString(pFilePathStr);
		
		return null;
	}

	@Override
	protected void clean(IProgressMonitor monitor) throws CoreException {
		getProject().deleteMarkers(null, true, IResource.DEPTH_INFINITE);
		ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
		File folders = getProject().getFolder(MSetting.FOLDER_UMC).getRawLocation().toFile();
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
