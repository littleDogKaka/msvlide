package cn.edu.xidian.ictt.msvlide.ui.wizard;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

import cn.edu.xidian.ictt.msvlide.project.nature.MSVLNature;
import cn.edu.xidian.ictt.msvlide.project.util.MSetting;
import cn.edu.xidian.ictt.msvlide.project.util.Property;


public class CreateVHDLProject extends BasicNewProjectResourceWizard{

	
	
	@Override
	public void addPages() {
		super.addPages();
		IWizardPage[] pages = super.getPages();
		pages[0].setTitle("New VHDL-MSVL Project");
		pages[0].setDescription("Create a VHDL to MSVL Project");
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection currentSelection) {
		super.init(workbench, currentSelection);
		super.setWindowTitle("MSVL Project");
		super.setNeedsProgressMonitor(true);
	}

	@Override
	public boolean performFinish() {
		boolean result = super.performFinish();
		if(!result){
			return false;
		}
		IProject project = super.getNewProject();
		if(project == null){
			return false;
		}
		try {
			MSVLNature nature = new MSVLNature();
			nature.setProject(project);
			nature.configure();
			
			IFolder srcFolder = project.getFolder(MSetting.FOLDER_SRC);
			IFolder binFolder = project.getFolder(MSetting.FOLDER_BIN);
			//IFolder src_c_Folder = project.getFolder(MSetting.FOLDER_SRC_C);
			//IFolder src_verlog_Folder = project.getFolder(MSetting.FOLDER_SRC_VERILOG);
			IFolder src_vhdl_Folder = project.getFolder(MSetting.FOLDER_SRC_VHDL);
			
			IFolder out_umc = project.getFolder(MSetting.FOLDER_UMC);
			IFolder out_pmc = project.getFolder(MSetting.FOLDER_PMC);
			
			srcFolder.create(true, true, null);
			binFolder.create(true, true, null);
			//src_c_Folder.create(true, true, null);
			//src_verlog_Folder.create(true, true, null);
			src_vhdl_Folder.create(true, true, null);
			out_umc.create(true, true, null);
			out_pmc.create(true, true, null);
			
			//IFile main = srcFolder.getFile(MSetting.FILE_MAIN_NAME + MSetting.FILE_MAIN_SUFFIX);
			//main.create(new ByteArrayInputStream(MSetting.FILE_MAIN_INIT.getBytes()), true, null);
			
//			getShell().getDisplay().asyncExec(new Runnable() {
//				public void run() {
//					IWorkbenchPage page = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
//					try {
//						IDE.openEditor(page, main, true);
//					} catch (PartInitException e) {}
//				}
//			});
			
			Property.init(project);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return result;
	}

}
