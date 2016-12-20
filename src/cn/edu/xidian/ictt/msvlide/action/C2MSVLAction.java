package cn.edu.xidian.ictt.msvlide.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

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

import cn.edu.xidian.ictt.msvlide.console.MessageConsoleFactory;
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
		if(window == null || project == null){
			return;
		}
		if(resource == null){
			showDialog();
			return;
		}
		
		if(resource instanceof IFolder){
			showDialog();
			return;
		}else if(resource instanceof IFile){
			IFile file = (IFile)resource;
			String postfix = file.getFileExtension();
			if(!postfix.equals("c")){
				showDialog();
				return;
			}
			
			String input = file.getRawLocation().toString().replace("/", "\\");
			//System.out.println(input);
			String[] args = {"C2M.exe", input, file.getName() + ".m"};
			//System.out.println(args[1]);
			
			File wd = project.getFolder("src").getRawLocation().toFile();
			
			try {
				Process p = Runtime.getRuntime().exec(args, null, wd);
				InputStream in = p.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String line;
				while((line = br.readLine()) != null){
					MessageConsoleFactory.printToConsole(line,true);
				}
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
	
	private void showDialog(){
		String[] btns = {"OK"};
		MessageDialog dialog = new MessageDialog(window.getShell(),"MSVL Project", null,"Please choose a C source file in directory \"src-c\"", MessageDialog.WARNING,btns,0); 
		dialog.open();
	}
}
