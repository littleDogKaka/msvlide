package cn.edu.xidian.ictt.msvlide.action.convert;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import cn.edu.xidian.ictt.msvlide.console.DisplayOutput;
import cn.edu.xidian.ictt.msvlide.project.util.MProject;
import cn.edu.xidian.ictt.msvlide.project.util.MSetting;

public class C2MSVLAction implements IWorkbenchWindowActionDelegate{

	private IWorkbenchWindow window;
	private IProject project;
	private IResource resource;
	
	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
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
	public void run(IAction action) {
		if(window == null){
			return;
		}
		if(resource == null || project == null){
			showDialog();
			return;
		}
		
		if(resource instanceof IFolder){
			showDialog();
			return;
		}else if(resource instanceof IFile){
			IFile file = (IFile)resource;
			String filename = file.getName();
			if(!filename.endsWith(MSetting.FILE_C_SUFFIX)){
				showDialog();
				return;
			}
			
			String input = file.getRawLocation().toString().replace("/", "\\");
			//System.out.println(input);
			String[] args = {MSetting.CONVERT_C_TO_MSVL, input, file.getName() + MSetting.FILE_MAIN_SUFFIX};
			//System.out.println(args[1]);
			
			File wd = project.getFolder(MSetting.FOLDER_SRC).getRawLocation().toFile();
			
			try {
				Process p = Runtime.getRuntime().exec(args, null, wd);
				new Thread(new DisplayOutput(p.getInputStream(), "C2MSVL")).start();
				new Thread(new DisplayOutput(p.getErrorStream(), "C2MSVL")).start();
				p.waitFor();
				ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			showDialog();
		}
	}

	@Override
	public void dispose() {}
	
	private void showDialog(){
		String[] btns = {"OK"};
		MessageDialog dialog = new MessageDialog(window.getShell(),"MSVL Project", null,"Please choose a C source file in directory " + MSetting.FOLDER_SRC_C, MessageDialog.WARNING,btns,0); 
		dialog.open();
	}
}
