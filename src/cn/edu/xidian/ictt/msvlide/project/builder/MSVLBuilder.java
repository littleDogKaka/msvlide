package cn.edu.xidian.ictt.msvlide.project.builder;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import cn.edu.xidian.ictt.msvlide.console.DisplayOutput;
import cn.edu.xidian.ictt.msvlide.console.MConsole;
import cn.edu.xidian.ictt.msvlide.project.util.MChecker;
import cn.edu.xidian.ictt.msvlide.project.util.MSetting;
import cn.edu.xidian.ictt.msvlide.project.util.PType;
import cn.edu.xidian.ictt.msvlide.project.util.Property;
import cn.edu.xidian.ictt.msvlide.util.NameFilter;

public class MSVLBuilder extends IncrementalProjectBuilder {

	public static final String BUILDER_ID = "cn.edu.xidian.ictt.msvlide.MSVLBuilder";
	public static final String BUILDER_NAME = "cn.edu.xidian.ictt.msvlide.MSVLBuilder";
	public static final String MARKER_TYPE = "cn.edu.xidian.ictt.msvlide.marker.project";
	
	public static final String DELIMITER = " ";

	@Override
	protected IProject[] build(int kind,  Map<String,String> map, IProgressMonitor monitor)throws CoreException {
		if(MChecker.checkMainFile(getProject()) != 1){
			return null;
		}
		
		switch(kind){
		case AUTO_BUILD: 
		case INCREMENTAL_BUILD:
			IResourceDelta delta = getDelta(getProject());
			if (delta == null) {
				fullBuild(monitor,map);
			} else {
				incrementalBuild(delta, monitor);
			}
			break;
		case FULL_BUILD:
			fullBuild(monitor,map);
			break;
		case CLEAN_BUILD:
			System.out.println("clean?");
			break;
		}
		return null;
	}
	
	protected void fullBuild(final IProgressMonitor monitor,Map<String,String> map)throws CoreException {
		try {
			
			IProject project = getProject();
			int count = project.getFolder(MSetting.FOLDER_SRC).getRawLocation().toFile().list().length;
			monitor.beginTask("Compiling ", count * 5 + 5);
			MConsole.clear();
			project.accept(new MSVLResourceVisitor(monitor));
			genExe(monitor);

		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	protected void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor) throws CoreException {
		delta.accept(new MSVLDeltaVisitor(monitor));
		genExe(monitor);
	}
	
	private int compile(IFile file,IProgressMonitor monitor){
		monitor.setTaskName(file.getName());
		int rtn = 0;
		try {
			ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
			
			String[] cmd = new String[8];
			cmd[0] = MSetting.MSVL_COMPILER;
			try {
				cmd[1] = "output=" + Property.get(getProject(), PType.ISOUTPUT);
				cmd[2] = "maxstate=" + Property.get(getProject(), PType.MAXSTATUS);
				cmd[3] = "gap=" + Property.get(getProject(), PType.INTERVAL);
			} catch (CoreException e) {
				cmd[1] = "output=false";
				cmd[2] = "maxstate=1000000";
				cmd[3] = "gap=1";
			}
			cmd[4] = "-c";
			cmd[5] = file.getRawLocation().toString();
			cmd[6] = "-o";
			cmd[7] = file.getName() + ".bc";
			
			monitor.worked(1);
			// delete xxx.*.bc before compile
			try{
				File oldBc = getProject().getFolder(MSetting.FOLDER_BIN).getFile(cmd[7]).getRawLocation().toFile();
				if(oldBc.exists()){
					oldBc.delete();
				}
				ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
			}catch(Exception e){
				e.printStackTrace();
			}
			
			monitor.worked(1);
			try{
				File dir = getProject().getFolder(MSetting.FOLDER_BIN).getRawLocation().toFile();
				Process p = Runtime.getRuntime().exec(cmd,null,dir);
				new Thread(new DisplayOutput(p.getInputStream(),file.getName())).start();
				new Thread(new DisplayOutput(p.getErrorStream(),file.getName())).start();
				
				p.waitFor();
				rtn = p.exitValue();
			}catch(Exception e){
				rtn = -1;
				e.printStackTrace();
			}
			ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
			monitor.worked(1);
		} catch (CoreException e1) {
			rtn = -1;
			e1.printStackTrace();
		}
		return rtn;
	}
	
	private int genExe(IProgressMonitor monitor){
		monitor.setTaskName("Generating executable program");
		int rtn = 0;
		try{
			ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
			IProject project = getProject();
			String exeName = project.getName() + ".exe";
			IFolder binFolder = project.getFolder(MSetting.FOLDER_BIN);
			
			monitor.worked(1);
			try{
				File oldexe = binFolder.getFile(exeName).getRawLocation().toFile();
				if(oldexe.exists()){
					oldexe.delete();
					ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			monitor.worked(2);
			
			File binDir = binFolder.getRawLocation().toFile();
			String[] files = binDir.list(new NameFilter(".bc"));
			if(files.length > 0){
				StringBuilder cmdBuilder = new StringBuilder();
				cmdBuilder.append(MSetting.MSVL_COMPILER + DELIMITER);
				for(String filename:files){
					cmdBuilder.append(filename + DELIMITER);
				}
				cmdBuilder.append("-o" + DELIMITER);
				cmdBuilder.append(exeName);

				Process p = Runtime.getRuntime().exec(cmdBuilder.toString().split(DELIMITER),null,binDir);
				new Thread(new DisplayOutput(p.getInputStream(),exeName)).start();
				new Thread(new DisplayOutput(p.getErrorStream(),exeName)).start();
				p.waitFor();
				rtn = p.exitValue();
			}else{
				rtn = -1;
			}
			ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
			monitor.worked(2);
		}catch(CoreException | IOException | InterruptedException e){
			rtn = -1;
			e.printStackTrace();
		}
		if(rtn == 0){
			monitor.done();
		}else{
			monitor.setCanceled(true);
		}
		return rtn;
	}
	
	protected void clean(IProgressMonitor monitor) throws CoreException {
		getProject().deleteMarkers(null, true, IResource.DEPTH_INFINITE);
		ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
		File[] folders = new File[3];
		folders[0] = getProject().getFolder(MSetting.FOLDER_BIN).getRawLocation().toFile();
		folders[1] = getProject().getFolder(MSetting.FOLDER_PMC).getRawLocation().toFile();
		folders[2] = getProject().getFolder(MSetting.FOLDER_UMC).getRawLocation().toFile();

		monitor.beginTask("cleaning...", 4);
		
		for(int i = 0; i < 3; ++i){
			File[] files = folders[i].listFiles();
			for(File file:files){
				try{
					file.delete();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			monitor.worked(1);
		}
		ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
		MConsole.clear();
		monitor.done();
	}
	
	
	class MSVLDeltaVisitor implements IResourceDeltaVisitor {
		
		private IProgressMonitor monitor;
		
		public MSVLDeltaVisitor(IProgressMonitor monitor){
			this.monitor = monitor;
		}
		
		@Override
		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource resource = delta.getResource();
			
			switch (delta.getKind()) {
			case IResourceDelta.ADDED:
				
				if(resource instanceof IFile){
					IFile file = (IFile)resource;
					
					String filename = file.getName();
					String parentname = file.getParent().getName();
					
					if(parentname.equals(MSetting.FOLDER_SRC)){
						if(filename.endsWith(MSetting.FILE_MAIN_SUFFIX)){
							compile(file,monitor);
						}else if(filename.endsWith(MSetting.FILE_FUNC_SUFFIX)) {
							compile(file,monitor);
						}else if(filename.endsWith(MSetting.FILE_HEADER_SUFFIX)){
							
						}
					}
				}
				break;
			case IResourceDelta.REMOVED:
				
				if(resource instanceof IFile){
					IFile file = (IFile)resource;
					IContainer folder = file.getParent();
					if(folder.getName().equals(MSetting.FOLDER_SRC)){
						ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
						IFile bcFile= file.getProject().getFolder(MSetting.FOLDER_BIN).getFile(file.getName() + MSetting.FILE_IR_SUFFIX);
						if(bcFile.exists()){
							bcFile.delete(true, null);
						}
						ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
					}
				}else if(resource instanceof IFolder){
					
				}
				break;
			case IResourceDelta.CHANGED:
				
				if(resource instanceof IFile){
					IFile file = (IFile)resource;
					if(file.getParent().getName().equals(MSetting.FOLDER_SRC) && (file.getName().endsWith(MSetting.FILE_MAIN_SUFFIX) || file.getName().endsWith(MSetting.FILE_FUNC_SUFFIX))){
						compile(file,monitor);
					}
				}
				break;
			}
			return true;
		}
	}

	// full build will execute visit on every file in project(even .project file)
	class MSVLResourceVisitor implements IResourceVisitor {
		private IProgressMonitor monitor;
		
		public MSVLResourceVisitor(IProgressMonitor monitor){
			this.monitor = monitor;
		}
		
		public boolean visit(IResource resource) {
			if(resource instanceof IFile){
				IFile file = (IFile)resource;
				if(file.getParent().getName().equals(MSetting.FOLDER_SRC) ){
					String filename = file.getName();
					if(filename.endsWith(MSetting.FILE_MAIN_SUFFIX) || filename.endsWith(MSetting.FILE_FUNC_SUFFIX)){
						compile(file,monitor);
					}
				}
			}
			return true;
		}
	}
}





//private void addMarker(IFile file, String message, int lineNumber, int severity) {
//try {
//	IMarker marker = file.createMarker(MARKER_TYPE);
//	marker.setAttribute(IMarker.MESSAGE, message);
//	marker.setAttribute(IMarker.SEVERITY, severity);
//	if (lineNumber == -1) {
//		lineNumber = 1;
//	}
//	marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
//} catch (CoreException e) {
//}
//}
//private void checkMSVL(IResource resource,IProgressMonitor monitor) {
//	monitor.setTaskName("Checking...");
//	if (resource instanceof IFile) {
//		IFile file = (IFile) resource;
//		String filename = file.getName();
//		if(filename.endsWith(MSetting.FILE_MAIN_SUFFIX) || filename.endsWith(MSetting.FILE_FUNC_SUFFIX)){
//			try {
//				file.deleteMarkers(null, true, IResource.DEPTH_INFINITE);
//				//getParser().parse(file.getContents(), reporter);
//			} catch (Exception e) {}
//		}
//	}
//	monitor.worked(2);
//}