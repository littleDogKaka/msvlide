package cn.edu.xidian.ictt.msvlide.action.convert;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import cn.edu.xidian.ictt.msvlide.launch.config.LaunchConfig;
import cn.edu.xidian.ictt.msvlide.project.util.MProject;
import cn.edu.xidian.ictt.msvlide.project.util.MSetting;

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
		if(!filename.endsWith(MSetting.FILE_VHDL_SUFFIX)){
			showDialog();
			return;
		}
		
		String args = file.getRawLocation().toOSString() +" "+ file.getProject().getFolder(MSetting.FOLDER_SRC).getRawLocation().toOSString() + "\\" + file.getName() + MSetting.FILE_MAIN_SUFFIX;
		try {
			ILaunchConfiguration config = LaunchConfig.find(LaunchConfig.LAUNCH_CONFIG_MODE_CONVERT);
			ILaunchConfigurationWorkingCopy wc = config.getWorkingCopy();
			wc.setAttribute(LaunchConfig.LAUNCH_CONFIG_KEY_MODE, LaunchConfig.LAUNCH_CONFIG_MODE_CONVERT);
			wc.setAttribute(LaunchConfig.LAUNCH_CONFIG_KEY_PROJECT_NAME, file.getProject().getName());
			wc.setAttribute(LaunchConfig.LAUNCH_CONFIG_KEY_RUN_FILENAME, MSetting.CONVERT_VHDL_TO_MSVL);
			wc.setAttribute(LaunchConfig.LAUNCH_CONFIG_KEY_WD, file.getProject().getFolder(MSetting.FOLDER_SRC).getRawLocation().toOSString());
			wc.setAttribute(LaunchConfig.LAUNCH_CONFIG_KEY_ARGS, args);
			config = wc.doSave();
			try {
				config.launch(LaunchConfig.LAUNCH_CONFIG_MODE_CONVERT, null, true);
			} catch (CoreException e) {
				e.printStackTrace();
			}
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
		MessageDialog dialog = new MessageDialog(window.getShell(),"MSVL Project", null,"Please choose a VHDL source file in directory " + MSetting.FOLDER_SRC_VHDL, MessageDialog.WARNING,btns,0); 
		dialog.open();
	}
}
