package cn.edu.xidian.ictt.msvlide.project.builder;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import cn.edu.xidian.ictt.msvlide.console.DisplayOutput;
import cn.edu.xidian.ictt.msvlide.console.MConsole;
import cn.edu.xidian.ictt.msvlide.project.util.MChecker;
import cn.edu.xidian.ictt.msvlide.project.util.MSetting;
import cn.edu.xidian.ictt.msvlide.util.NameFilter;

public class PMCBuilder extends IncrementalProjectBuilder{
	
	public static final String BUILDER_ID = "cn.edu.xidian.ictt.msvlide.PMCBuilder";
	public static final String BUILDER_NAME = "cn.edu.xidian.ictt.msvlide.PMCBuilder";
	
	@Override
	protected IProject[] build(int kind, Map<String, String> map, IProgressMonitor monitor) throws CoreException {

		if(kind != FULL_BUILD){
			return null;
		}
		monitor.beginTask("PMCBuilder running...", 100);
		
		IProject project = getProject();
		
		if(MChecker.checkMainFile(project) != 1){
			return null;
		}
		
		if(map == null || map.isEmpty()){
			return null;
		}
		
		String mode = map.get(MSetting.BUILD_MAP_KEY_MODE);
		
		if(!MSetting.BUILD_MODE_PMC.equals(mode)){
			return null;
		}
		
		String pFilename = map.get(MSetting.BUILD_MAP_KEY_FILE_PROPERTY);
		if(pFilename == null || pFilename.isEmpty()){
			return null;
		}
		
		monitor.worked(5);
		
		IFolder src = project.getFolder(MSetting.FOLDER_SRC);
		IFile pfile = src.getFile(pFilename);
		String[] mFilenames = src.getRawLocation().toFile().list(new NameFilter(MSetting.FILE_MAIN_SUFFIX));
		IFile mfile = src.getFile(mFilenames[0]);
		
		monitor.worked(5);
		
		File wd = project.getFolder(MSetting.FOLDER_PMC).getRawLocation().toFile();
		if(!wd.exists() || !wd.canRead() || !wd.canWrite()){
			MConsole.print("ERROR: " +  wd.getAbsolutePath() + ": don't exist or cannot read or write.",true);
			return null;
		}
		String[] args = {MSetting.PMCHECKER, pfile.getRawLocation().toOSString(), mfile.getRawLocation().toOSString()};
		monitor.worked(5);
		
		try{
			if(monitor.isCanceled()){
				return null;
			}
			int i = 15;
			
			ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
			
			Process p = Runtime.getRuntime().exec(args, null, wd);
			new Thread(new DisplayOutput(p.getInputStream(), "PMC")).start();
			new Thread(new DisplayOutput(p.getErrorStream(), "PMC")).start();
			while(p.isAlive()){
				if(monitor.isCanceled()){
					p.destroy();
				}
				if(i < 95){
					monitor.worked(1);
				}
				i++;
				Thread.sleep(500);
			}
			
			ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
			monitor.done();
		}catch(IOException | InterruptedException e){
			e.printStackTrace();
		}
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
