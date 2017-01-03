package cn.edu.xidian.ictt.msvlide.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
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

public class VHDL2MSVLAction implements IWorkbenchWindowActionDelegate{

	private IWorkbenchWindow window;
	private IFile file;
	
	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
	
	@Override
	public void run(IAction action) {
		if(window == null){
			return;
		}
		if(file == null){
			showDialog();
			return;
		}
		String filename = file.getName();
		if(!filename.endsWith(".vhd")){
			showDialog();
			return;
		}
		
		String input = file.getRawLocation().toString().replace("/", "\\");
		File wd = file.getProject().getFolder("src").getRawLocation().toFile();
		String[] args = {"VHDL2MSVL.exe", input, wd.getAbsolutePath() + "\\" + file.getName() + ".m"};
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
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		if(selection == null){
			file = null;
			return;
		}
		if (selection instanceof IStructuredSelection) {
			Object obj = ((IStructuredSelection) selection).getFirstElement();
			if (obj instanceof IFile){
				file = (IFile) obj;
			}
		}else {
			file = MProject.getActiveEditorInput();
		}
	}

	@Override
	public void dispose() {}

	private void showDialog(){
		String[] btns = {"OK"};
		MessageDialog dialog = new MessageDialog(window.getShell(),"MSVL Project", null,"Please choose a VHDL source file in directory \"src-VHDL\"", MessageDialog.WARNING,btns,0); 
		dialog.open();
	}
}
