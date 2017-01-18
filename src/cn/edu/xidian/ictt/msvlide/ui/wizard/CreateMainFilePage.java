package cn.edu.xidian.ictt.msvlide.ui.wizard;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import cn.edu.xidian.ictt.msvlide.project.util.MSetting;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (m).
 */

public class CreateMainFilePage extends WizardPage {
	private Text containerText;

	private Text fileText;

	private Button button;
	
	private ISelection selection;
	

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public CreateMainFilePage(ISelection selection) {
		super("wizardPage");
		setTitle("Create MSVL Main File");
		setDescription("Create a new file with " + MSetting.FILE_MAIN_SUFFIX + " extension.");
		this.selection = selection;
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;
		Label label = new Label(container, SWT.NULL);
		label.setText("&Container:");

		containerText = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		containerText.setLayoutData(gd);
		containerText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		button = new Button(container, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowse();
			}
		});
		
		label = new Label(container, SWT.NULL);
		label.setText("&File name:");

		fileText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fileText.setLayoutData(gd);
		fileText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		initialize();
		dialogChanged();
		setControl(container);
	}

	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */

	private void initialize() {
		if (selection != null && selection.isEmpty() == false && selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() > 1)
				return;
			Object obj = ssel.getFirstElement();
			if (obj instanceof IResource) {
				String path = ((IResource)obj).getProject().getFolder(MSetting.FOLDER_SRC).getRawLocation().toString();
				containerText.setText(path);
				containerText.setEnabled(false);
				button.setEnabled(false);
			}else{
				containerText.setText("");
				containerText.setEnabled(true);
				button.setEnabled(true);
			}
		}
		fileText.setText(MSetting.FILE_MAIN_NAME + MSetting.FILE_MAIN_SUFFIX);
	}

	/**
	 * Uses the standard container selection dialog to choose the new value for
	 * the container field.
	 */

	private void handleBrowse() {
		ContainerSelectionDialog dialog = new ContainerSelectionDialog(
				getShell(), ResourcesPlugin.getWorkspace().getRoot(), false,
				"Select new file container");
		if (dialog.open() == ContainerSelectionDialog.OK) {
			Object[] result = dialog.getResult();
			if (result.length == 1) {
				String path = ((Path) result[0]).toString();
				containerText.setText(path);
			}
		}
	}

	/**
	 * Ensures that both text fields are set.
	 */

	private void dialogChanged() {
		IResource container = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(getContainerName()));
		
		String fileName = getFileName();

		if (getContainerName().length() == 0) {
			updateStatus("File container must be specified");
			return;
		}
		if (container == null || (container.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0) {
			updateStatus("File container must exist");
			return;
		}else{
			IProject project = container.getProject();
			IFolder folder = project.getFolder(MSetting.FOLDER_SRC);
			if(folder == null || !folder.exists()){
				updateStatus("Note: .m file must be placed in \"" + MSetting.FOLDER_SRC + "\"!");
				return;
			}else{
				String[] names = folder.getRawLocation().toFile().list();
				for(String name:names){
					if(name.endsWith(MSetting.FILE_MAIN_SUFFIX)){
						updateStatus("Note: There must be only one .m file in a MSVL Project!");
						return;
					}
				}
			}
		}
		
		if (!container.isAccessible()) {
			updateStatus("Project must be writable");
			return;
		}
		if (fileName.length() == 0) {
			updateStatus("File name must be specified");
			return;
		}
		if (fileName.replace('\\', '/').indexOf('/', 1) > 0) {
			updateStatus("File name must be valid");
			return;
		}
		if(!fileName.endsWith(MSetting.FILE_MAIN_SUFFIX)){
			updateStatus("File extension must be \"" + MSetting.FILE_MAIN_SUFFIX + "\"");
			return;
		}
		updateStatus(null);
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public String getContainerName() {
		IStructuredSelection ssel = (IStructuredSelection) selection;
		if (ssel.size() > 1)
			return "";
		Object obj = ssel.getFirstElement();
		if (obj instanceof IResource) {
			return "/" + ((IResource) obj).getProject().getName() + "/" + MSetting.FOLDER_SRC;
		}
		return containerText.getText();
	}

	public String getFileName() {
		return fileText.getText();
	}
}