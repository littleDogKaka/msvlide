package cn.edu.xidian.ictt.msvlide.action;

import java.io.File;

import org.eclipse.core.resources.IFile;
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

public class Verilog2MSVLAction implements IWorkbenchWindowActionDelegate{

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
		if(!filename.endsWith(MSetting.FILE_VERILOG_SUFFIX)){
			showDialog();
			return;
		}
		
		String input = file.getRawLocation().toString().replace("/", "\\");
		File wd = file.getProject().getFolder(MSetting.FOLDER_SRC).getRawLocation().toFile();
		String[] args = {MSetting.CONVERT_VERILOG_TO_MSVL, input, filename + MSetting.FILE_MAIN_SUFFIX};
		try {
			Process p = Runtime.getRuntime().exec(args, null, wd);
			new Thread(new DisplayOutput(p.getInputStream(), "Verilog2MSVL")).start();
			new Thread(new DisplayOutput(p.getErrorStream(), "Verilog2MSVL")).start();
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
		MessageDialog dialog = new MessageDialog(window.getShell(),"Verilog to MSVL", null,"Please select a Verilog source file in directory " + MSetting.FOLDER_SRC_VHDL, MessageDialog.WARNING,btns,0); 
		dialog.open();
		return;
	}
}
