package cn.edu.xidian.ictt.msvlide.ui.wizard;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;

import cn.edu.xidian.ictt.msvlide.project.util.MSetting;

public class CreatePropertyFile extends CreateFile {

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.setWindowTitle("MSVL Project - Create Property File");
		super.page = new CreateFilePage(selection, 
				"Create MSVL Property File", "Create a new file with " + MSetting.FILE_PROPERTY_SUFFIX + " extension.", 
				"new", 
				MSetting.FILE_PROPERTY_SUFFIX, 
				MSetting.FOLDER_SRC);
		super.initContent = MSetting.FILE_PROPERTY_INIT;
	}

}
