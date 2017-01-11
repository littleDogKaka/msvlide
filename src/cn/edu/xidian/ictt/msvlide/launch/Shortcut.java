package cn.edu.xidian.ictt.msvlide.launch;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;

import cn.edu.xidian.ictt.msvlide.project.util.MProject;


public class Shortcut implements ILaunchShortcut{
	 
	@Override
	public void launch(ISelection selection, String mode) {

		IResource resource = null;
		if (selection instanceof IStructuredSelection) {
			Object obj = ((IStructuredSelection) selection).getFirstElement();
			if (obj instanceof IResource)
				resource = (IResource) obj;
		}else
			resource = MProject.getActiveEditorInput();
		
		if(resource != null){
			ILaunchConfiguration config = LaunchConfig.find(mode, resource.getProject().getName());
			try {
				config.launch(mode, null, true);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		
		
	}

	@Override
	public void launch(IEditorPart editor, String mode) {
//		editor.
//		ILaunchConfiguration config = LaunchConfig.find(mode);
//		try {
//			config.launch(mode, null, true);
//		} catch (CoreException e) {
//			e.printStackTrace();
//		}
	}
}


//IFile runFile = null;
//if (selection instanceof IStructuredSelection) {
//	Object obj = ((IStructuredSelection) selection).getFirstElement();
//	if (obj instanceof IFile)
//		runFile = (IFile) obj;
//}
//
//if(runFile != null){
//	String path = runFile.getRawLocation().toString().replace("/", "\\");
//	String name = runFile.getName();
//}
