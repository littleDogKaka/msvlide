package cn.edu.xidian.ictt.msvlide.project.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
//import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
//import org.eclipse.core.resources.ResourcesPlugin;
//import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;

import cn.edu.xidian.ictt.msvlide.Activator;
import cn.edu.xidian.ictt.msvlide.project.nature.MSVLNature;


public class MProject {
	
	public static IProject get(ISelection selection){
		IProject project = null;
		
		try{
			if(selection == null){
				ISelectionService selectionService = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getSelectionService();
				if(selectionService != null){
					selection = selectionService.getSelection();
				}
			}
		}catch(Exception e){
			//e.printStackTrace();
			selection = null;
		}
		
		
		if (selection != null && selection instanceof IStructuredSelection) {
			Object obj = ((IStructuredSelection) selection).getFirstElement();
			if (obj instanceof IResource)
				project = ((IResource) obj).getProject();
		}
		
		if(project != null){
        	return project;
        }
		
		IFile currentFile = getActiveEditorInput();
		
		if(currentFile != null){
			project = currentFile.getProject();
		}
		
		if(project != null){
			return project;
		}
		
//		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
//
//		for(IProject obj: projects){
//			try{
//				IProjectNature nature = obj.getNature(MSVLNature.NATURE_ID);
//				if(nature == null){
//					continue;
//				}else{
//					project = obj;
//					break;
//				}
//			}catch(CoreException e){
//				//e.printStackTrace();
//			}
//		}
		
		return project;
	}
	
	public static IFile getActiveEditorInput(){
		IFile file = null;
		IWorkbench workbench = null;
		IWorkbenchWindow window = null;
		IWorkbenchPage page = null;
		IEditorPart	part = null;
		try{
			workbench =  Activator.getDefault().getWorkbench();
			window = workbench.getActiveWorkbenchWindow();
			page = window.getActivePage();
			part = page.getActiveEditor();
		}catch(Exception e){
			//e.printStackTrace();
			part = null;
		}
		

	    if(part != null){  
	        Object object = part.getEditorInput().getAdapter(IFile.class);  
	        if(object != null){  
	            file = (IFile)object;  
	        }  
	     }
		return file;
	}
	
	public static boolean isMSVLProject(IProject project){
		boolean rtn = false;
		try {
			rtn = project.hasNature(MSVLNature.NATURE_ID);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return rtn;
	}
}
