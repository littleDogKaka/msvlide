package cn.edu.xidian.ictt.msvlide.project.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;

import cn.edu.xidian.ictt.msvlide.Activator;
import cn.edu.xidian.ictt.msvlide.project.builder.MSVLNature;


public class MProject {
	public static IProject get(){
		
		IProject project = null;
		try{
			ISelectionService selectionService = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getSelectionService();
			ISelection selection = selectionService.getSelection();
			if (selection instanceof IStructuredSelection) {
				Object element = ((IStructuredSelection) selection).getFirstElement();

				if (element instanceof IResource) {
					project = ((IResource) element).getProject();
				} 
			}
		}catch(NullPointerException e){
			//e.printStackTrace();
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
		
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();

		for(IProject obj: projects){
			try{
				IProjectNature nature = obj.getNature(MSVLNature.NATURE_ID);
				if(nature == null){
					continue;
				}else{
					project = obj;
					break;
				}
			}catch(CoreException e){
				//e.printStackTrace();
			}
		}
		
		return project;
	}
	
	public static IFile getActiveEditorInput(){
		IFile file = null;
		try{
			IWorkbench workbench =  Activator.getDefault().getWorkbench();
			IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
			IWorkbenchPage page = window.getActivePage();
			IEditorPart	part = page.getActiveEditor();

	        if(part != null){  
	            Object object = part.getEditorInput().getAdapter(IFile.class);  
	            if(object != null){  
	                file = (IFile)object;  
	            }  
	        }
		}catch(NullPointerException e){
			//e.printStackTrace();
		}
		return file;
	}
}
