package cn.edu.xidian.ictt.msvlide.ui.wizard;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;

import cn.edu.xidian.ictt.msvlide.project.util.MSetting;

/**
 * This is a sample new wizard. Its role is to create a new file 
 * resource in the provided container. If the container resource
 * (a folder or a project) is selected in the workspace 
 * when the wizard is opened, it will accept it as the target
 * container. The wizard creates one file with the extension
 * "func". If a sample multi-page editor (also available
 * as a template) is registered for the same extension, it will
 * be able to open it.
 */

public class CreateFuncFile  extends CreateFile  {
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.setWindowTitle("MSVL Project - Create Function File");
		super.page = new CreateFilePage(selection, 
				"Create MSVL Function File", "Create a new file with " + MSetting.FILE_FUNC_SUFFIX + " extension.", 
				"new", 
				MSetting.FILE_FUNC_SUFFIX, 
				MSetting.FOLDER_SRC);
		super.initContent = MSetting.FILE_FUNC_INIT;
	}
}