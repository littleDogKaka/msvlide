package cn.edu.xidian.ictt.msvlide.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;


public class Shortcut implements ILaunchShortcut{
	 
	@Override
	public void launch(ISelection selection, String mode) {
		ILaunchConfiguration config = Attribtes.findLaunchConfig(mode);
		try {
			config.launch(mode, null, true);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void launch(IEditorPart editor, String mode) {
		ILaunchConfiguration config = Attribtes.findLaunchConfig(mode);
		try {
			config.launch(mode, null, true);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
}
