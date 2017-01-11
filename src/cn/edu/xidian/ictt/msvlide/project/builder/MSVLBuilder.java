package cn.edu.xidian.ictt.msvlide.project.builder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import cn.edu.xidian.ictt.msvlide.console.MConsole;
import cn.edu.xidian.ictt.msvlide.project.util.MSetting;
import cn.edu.xidian.ictt.msvlide.project.util.PType;
import cn.edu.xidian.ictt.msvlide.project.util.Property;

public class MSVLBuilder extends IncrementalProjectBuilder {
	
	public static final String DELIMITER = " ";

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
					if(file.getParent().getName().equals(MSetting.FOLDER_SRC) 
							&& (file.getName().endsWith(MSetting.FILE_MAIN_SUFFIX) 
									|| file.getName().endsWith(MSetting.FILE_FUNC_SUFFIX))){
						checkMSVL(resource,monitor);
						compile(file,monitor);
					}
				}
				break;
			case IResourceDelta.REMOVED:
				if(resource instanceof IFile){
					IFile file = (IFile)resource;
					IContainer folder = file.getParent();
					if(folder.getName().equals(MSetting.FOLDER_SRC)){
						ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
						IFile bcFile= file.getProject().getFolder(MSetting.FOLDER_BIN).getFile(file.getName()+".bc");
						if(bcFile.exists()){
							bcFile.delete(true, null);
						}
						ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
					}
				}
				break;
			case IResourceDelta.CHANGED:
				if(resource instanceof IFile){
					IFile file = (IFile)resource;
					if(file.getParent().getName().equals(MSetting.FOLDER_SRC) 
							&& (file.getName().endsWith(MSetting.FILE_MAIN_SUFFIX) 
									|| file.getName().endsWith(MSetting.FILE_FUNC_SUFFIX))){
						checkMSVL(resource,monitor);
						compile(file,monitor);
					}
				}
				break;
			}
			return true;
		}
	}

	class MSVLResourceVisitor implements IResourceVisitor {
		private IProgressMonitor monitor;
		
		public MSVLResourceVisitor(IProgressMonitor monitor){
			this.monitor = monitor;
		}
		
		public boolean visit(IResource resource) {
			if(resource instanceof IFile){
				IFile file = (IFile)resource;
				if(file.getParent().getName().equals(MSetting.FOLDER_SRC) 
						&& (file.getName().endsWith(MSetting.FILE_MAIN_SUFFIX) 
								|| file.getName().endsWith(MSetting.FILE_FUNC_SUFFIX))){
					checkMSVL(resource,monitor);
					compile(file,monitor);
				}
			}
			return true;
		}
	}

	class MErrorHandler extends DefaultHandler {
		
		private IFile file;

		public MErrorHandler(IFile file) {
			this.file = file;
		}

		private void addMarker(SAXParseException e, int severity) {
			MSVLBuilder.this.addMarker(file, e.getMessage(), e.getLineNumber(), severity);
		}

		public void error(SAXParseException exception) throws SAXException {
			addMarker(exception, IMarker.SEVERITY_ERROR);
		}

		public void fatalError(SAXParseException exception) throws SAXException {
			addMarker(exception, IMarker.SEVERITY_ERROR);
		}

		public void warning(SAXParseException exception) throws SAXException {
			addMarker(exception, IMarker.SEVERITY_WARNING);
		}
	}

	public static final String BUILDER_ID = "cn.edu.xidian.ictt.msvlide.MSVLBuilder";

	private static final String MARKER_TYPE = "cn.edu.xidian.ictt.msvlide.msvlProblem";

	private void addMarker(IFile file, String message, int lineNumber, int severity) {
		try {
			IMarker marker = file.createMarker(MARKER_TYPE);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			if (lineNumber == -1) {
				lineNumber = 1;
			}
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
		} catch (CoreException e) {
		}
	}

	@Override
	protected IProject[] build(int kind,  Map<String,String> args, IProgressMonitor monitor)throws CoreException {

		if (kind == FULL_BUILD) {
			fullBuild(monitor);
		} else {
			IResourceDelta delta = getDelta(getProject());
			if (delta == null) {
				fullBuild(monitor);
			} else {
				incrementalBuild(delta, monitor);
			}
		}
		return null;
	}

	protected void clean(IProgressMonitor monitor) throws CoreException {
		getProject().deleteMarkers(MARKER_TYPE, true, IResource.DEPTH_INFINITE);
		ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
		File binDir = getProject().getFolder(MSetting.FOLDER_BIN).getRawLocation().toFile();
		File[] files = binDir.listFiles();
		monitor.beginTask("cleaning", files.length + 1);
		for(File file:files){
			try{
				file.delete();
			}catch(Exception e){
				System.out.println("cleaning: " + file.getName());
				e.printStackTrace();
			}
			monitor.worked(1);
		}
		ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
		monitor.done();
	}

	void checkMSVL(IResource resource,IProgressMonitor monitor) {
		monitor.setTaskName("Checking...");
		if (resource instanceof IFile && (resource.getName().endsWith(MSetting.FILE_MAIN_SUFFIX) || resource.getName().endsWith(MSetting.FILE_FUNC_SUFFIX))) {
			IFile file = (IFile) resource;
			deleteMarkers(file);
			//MErrorHandler reporter = new MErrorHandler(file);
			try {
				//getParser().parse(file.getContents(), reporter);
			} catch (Exception e1) {}
		}
		monitor.worked(2);
	}

	private void deleteMarkers(IFile file) {
		try {
			file.deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_ZERO);
		} catch (CoreException ce) {
		}
	}

	protected void fullBuild(final IProgressMonitor monitor)throws CoreException {
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

//	private SAXParser getParser() throws ParserConfigurationException,SAXException {
//		if (parserFactory == null) {
//			parserFactory = SAXParserFactory.newInstance();
//		}
//		return parserFactory.newSAXParser();
//	}

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
	
	class DisplayOutput implements Runnable{
		
		private String filename;
		private InputStream input;
		
		public DisplayOutput(InputStream input,String filename){
			this.input = input;
			this.filename = filename;
		}
		
		@Override
		public void run() {
			try {
	            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
	            String line;
	            while ((line = reader.readLine()) != null) {
	            	MConsole.print(filename + ": " + line, true);
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                input.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
		}
	}
	
	static class NameFilter implements FilenameFilter{

    	private String type;
    	public NameFilter(String type){
    		this.type = type;
    	}
		@Override
		public boolean accept(File dir, String name) {
			return name.endsWith(type);
		}
    }
	
//	public static void readOutput(InputStream input,StringBuilder output){
//		try {
//            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
//            String line;
//            while ((line = reader.readLine()) != null) {
//                output.append(line + "\n");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                input.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//	}
}
