package cn.edu.xidian.ictt.msvlide.ui.wizard;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;

import cn.edu.xidian.ictt.msvlide.project.util.MSetting;


public class CreateHeaderFile extends CreateFile{

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.setWindowTitle("MSVL Project - Create Header File");
		super.page = new CreateFilePage(selection, 
				"Create MSVL Header File", "Create a new file with " + MSetting.FILE_HEADER_SUFFIX + " extension.", 
				"new", 
				MSetting.FILE_HEADER_SUFFIX, 
				MSetting.FOLDER_SRC);
		super.initContent = MSetting.FILE_HEADER_INIT;
	}

}
