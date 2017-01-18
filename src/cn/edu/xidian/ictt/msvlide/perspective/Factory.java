package cn.edu.xidian.ictt.msvlide.perspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.wizards.newresource.BasicNewFileResourceWizard;
import org.eclipse.ui.wizards.newresource.BasicNewFolderResourceWizard;

public class Factory implements IPerspectiveFactory{

	private static final String MSVL_PERSPECTIVE_ID = "cn.edu.xidian.ictt.msvlide.perspective";
	private static final String MSVL_BUILD_ACTIONSET_ID = "cn.edu.xidian.ictt.msvlide.ActionSet.Build";
	private static final String MSVL_CONVERT_ACTIONSET_ID = "cn.edu.xidian.ictt.msvlide.ActionSet.Convert";
	private static final String MSVL_CHECK_ACTIONSET_ID = "cn.edu.xidian.ictt.msvlide.ActionSet.Checker";
	private static final String NEW_PROJECT_WIZARD_ID = "cn.edu.xidian.ictt.msvlide.newwizards.project";
	private static final String NEW_MAINFILE_WIZARD_ID = "cn.edu.xidian.ictt.msvlide.newwizards.CreateMainFile";
	private static final String NEW_FUNCFILE_WIZARD_ID = "cn.edu.xidian.ictt.msvlide.newwizards.CreateFuncFile";
	private static final String NEW_HEADERFILE_WIZARD_ID = "cn.edu.xidian.ictt.msvlide.newwizards.CreateHeaderFile";
	private static final String NEW_PROPERTY_WIZARD_ID = "cn.edu.xidian.ictt.msvlide.newwizards.CreatePropertyFile";
	
	@Override
	public void createInitialLayout(IPageLayout layout) {

		layout.addPerspectiveShortcut(MSVL_PERSPECTIVE_ID);
		
		String editorArea = layout.getEditorArea();
		
		layout.addView(IPageLayout.ID_EDITOR_AREA,IPageLayout.RIGHT,0.75f,editorArea);
		layout.addView(IPageLayout.ID_PROJECT_EXPLORER, IPageLayout.LEFT, 0.25f, editorArea);
		IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.75f, editorArea);
		bottom.addView(IPageLayout.ID_PROBLEM_VIEW);
		
		layout.addActionSet(MSVL_CONVERT_ACTIONSET_ID);
		layout.addActionSet(MSVL_BUILD_ACTIONSET_ID);
		layout.addActionSet(MSVL_CHECK_ACTIONSET_ID);
		
		layout.addNewWizardShortcut(BasicNewFolderResourceWizard.WIZARD_ID);
		layout.addNewWizardShortcut(BasicNewFileResourceWizard.WIZARD_ID);
		layout.addNewWizardShortcut(NEW_PROJECT_WIZARD_ID);
		layout.addNewWizardShortcut(NEW_MAINFILE_WIZARD_ID);
		layout.addNewWizardShortcut(NEW_FUNCFILE_WIZARD_ID);
		layout.addNewWizardShortcut(NEW_HEADERFILE_WIZARD_ID);
		layout.addNewWizardShortcut(NEW_PROPERTY_WIZARD_ID);
		
		layout.addPerspectiveShortcut(MSVL_PERSPECTIVE_ID);
	}

}
