package cn.edu.xidian.ictt.msvlide.action;

import java.lang.reflect.InvocationTargetException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import cn.edu.xidian.ictt.msvlide.project.util.MProject;

public class C2MSVLAction implements IWorkbenchWindowActionDelegate{

	private IWorkbenchWindow window;
	private IProject project;
	private IResource resource;
	
	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
	
	@Override
	public void run(IAction action) {
		if(window == null){
			return;
		}
		if(project == null || resource == null){
			String[] btns = {"OK"};
			MessageDialog dialog = new MessageDialog(window.getShell(),"MSVL Project", null,"Please choose a C source file in directory \"src-c\"", MessageDialog.WARNING,btns,0); 
			dialog.open();
			return;
		}
		
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {

		if(selection == null){
			project = null;
			resource = null;
			return;
		}
		if (selection instanceof IStructuredSelection) {
			Object obj = ((IStructuredSelection) selection).getFirstElement();
			if (obj instanceof IResource)
				resource = (IResource) obj;
		}else
			resource = MProject.getActiveEditorInput();
		if(resource != null){
			project = resource.getProject();
		}
	}

	@Override
	public void dispose() {}
	
	class Convert implements IRunnableWithProgress{

		@Override
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			
		}
		
	}
}
