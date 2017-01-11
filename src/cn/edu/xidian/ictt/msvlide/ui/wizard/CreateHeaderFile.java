package cn.edu.xidian.ictt.msvlide.ui.wizard;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class CreateHeaderFile extends Wizard implements INewWizard{

	private CreateHeaderFilePage page;
	private ISelection selection;
	
	@Override
	public void init(IWorkbench arg0, IStructuredSelection selection) {
		
	}

	@Override
	public boolean performFinish() {
		return false;
	}

}
