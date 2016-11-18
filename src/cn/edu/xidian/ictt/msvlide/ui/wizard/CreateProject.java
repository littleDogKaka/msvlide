package cn.edu.xidian.ictt.msvlide.ui.wizard;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

import cn.edu.xidian.ictt.msvlide.project.builder.MSVLNature;
import cn.edu.xidian.ictt.msvlide.project.util.Property;


public class CreateProject extends BasicNewProjectResourceWizard{

	@Override
	public void addPages() {
		super.addPages();
		IWizardPage[] pages = super.getPages();
		pages[0].setTitle("New MSVL Project");
		pages[0].setDescription("Create a new MSVL Project");
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
			
			IFolder srcFolder = project.getFolder("src");
			IFolder outputFolder = project.getFolder("bin");
			srcFolder.create(true, true, null);
			outputFolder.create(true, true, null);
			
			IFile main = srcFolder.getFile("main.m");
			main.create(new ByteArrayInputStream("frame() and (\n\t\n)".getBytes()), true, null);
			
			Property.init(project);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return result;
	}

}
