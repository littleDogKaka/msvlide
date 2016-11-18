package cn.edu.xidian.ictt.msvlide.ui.launch;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import cn.edu.xidian.ictt.msvlide.project.util.MProject;
import cn.edu.xidian.ictt.msvlide.project.util.PType;
import cn.edu.xidian.ictt.msvlide.project.util.Property;

public class ConfigTabMain extends AbstractLaunchConfigurationTab{

	private static final String ERRORMESSAGE = "Please input valid number";
	
	private Text currentProjectName;
	
	private Button isOutputBtn;
	private Text maxStatusNumberText;
	private Text statusIntervalText;
	private Text commandLineArgs;
	private Text workingDirectory;
	
	private boolean[] valid = {true,true};
	
	@Override
	public void createControl(Composite parent) {
		Composite panel = new Composite(parent,SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.verticalSpacing = 9;
		panel.setLayout(layout);

		createVerticalSpacer(panel, 1);
		
		Label cpnLabel = new Label(panel,SWT.NULL); 
		cpnLabel.setText("Project: ");
		
		currentProjectName = new Text(panel,SWT.BORDER | SWT.SINGLE);
		GridData cpnGD = new GridData(GridData.FILL_HORIZONTAL);
		currentProjectName.setLayoutData(cpnGD);
		
		createVerticalSpacer(panel, 1);
		// Build options
		Group boGroup = new Group(panel,SWT.NULL);
		boGroup.setText("Build options: ");
		
		GridLayout boglayout = new GridLayout();
		boglayout.numColumns = 3;
		boglayout.verticalSpacing = 3;
		boGroup.setLayout(boglayout);
		
		GridData boGroupData = new GridData(GridData.FILL_HORIZONTAL);
		boGroup.setLayoutData(boGroupData);
		boGroupData.horizontalSpan = 3;
		
		isOutputBtn = new Button(boGroup,SWT.CHECK);
		isOutputBtn.setText("Output Status");
		GridData btnGd = new GridData(GridData.FILL_HORIZONTAL);
		btnGd.horizontalSpan = 3;
		isOutputBtn.setLayoutData(btnGd);
		isOutputBtn.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(isOutputBtn.getSelection()){
					maxStatusNumberText.setEnabled(true);
					statusIntervalText.setEnabled(true);
				}else{
					maxStatusNumberText.setEnabled(false);
					statusIntervalText.setEnabled(false);
				}
				updateLaunchConfigurationDialog();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		Label maxStatusNumberLabel = new Label(boGroup,SWT.NULL);
		maxStatusNumberLabel.setText("Max status number: ");
		maxStatusNumberLabel.setLayoutData(new GridData());
		
		maxStatusNumberText = new Text(boGroup,SWT.BORDER | SWT.SINGLE);
		GridData msnGd = new GridData(GridData.FILL_HORIZONTAL);
		msnGd.horizontalSpan = 2;
		maxStatusNumberText.setLayoutData(msnGd);
		maxStatusNumberText.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				try{
					Integer.parseInt(maxStatusNumberText.getText().trim(),10);
					if(valid[1]){
						setErrorMessage(null);
					}
					valid[0] = true;
				}catch(NumberFormatException ee){
					valid[0] = false;
					setErrorMessage(ERRORMESSAGE);
				}
				updateLaunchConfigurationDialog();
			}
		});
		
		Label statusIntervalLabel = new Label(boGroup,SWT.NONE);
		statusIntervalLabel.setText("Status interval: ");
		statusIntervalLabel.setLayoutData(new GridData());
		
		statusIntervalText = new Text(boGroup,SWT.BORDER | SWT.SINGLE);
		GridData siGd = new GridData(GridData.FILL_HORIZONTAL);
		siGd.horizontalSpan = 2;
		statusIntervalText.setLayoutData(siGd);
		statusIntervalText.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				try{
					Integer.parseInt(statusIntervalText.getText().trim(),10);
					if(valid[0]){
						setErrorMessage(null);
					}
					valid[1] = true;
				}catch(NumberFormatException ee){
					valid[1] = false;
					setErrorMessage(ERRORMESSAGE);
				}
				updateLaunchConfigurationDialog();
			}
		});
		
		createVerticalSpacer(panel, 1);
		// Run arguments
		Group raGroup = new Group(panel,SWT.NULL);
		raGroup.setText("Run arguments:");
		
		GridLayout raglayout = new GridLayout();
		raglayout.numColumns = 3;
		raglayout.verticalSpacing = 3;
		raGroup.setLayout(raglayout);
		
		GridData raGroupData = new GridData(GridData.FILL_HORIZONTAL);
		raGroup.setLayoutData(raGroupData);
		raGroupData.horizontalSpan = 3;
		
		Label commandLineArgsLabel = new Label(raGroup,SWT.NONE);//
		commandLineArgsLabel.setText("Command-line arguments: ");
		commandLineArgsLabel.setLayoutData(new GridData());
		
		commandLineArgs = new Text(raGroup,SWT.BORDER | SWT.SINGLE);
		GridData claGd = new GridData(GridData.FILL_HORIZONTAL);
		claGd.horizontalSpan = 2;
		commandLineArgs.setLayoutData(claGd);
		
		Label workingDirectoryLabel = new Label(raGroup,SWT.NONE);
		workingDirectoryLabel.setText("Working directory: ");
		workingDirectoryLabel.setLayoutData(new GridData());
		
		workingDirectory = new Text(raGroup,SWT.BORDER | SWT.SINGLE);
		GridData wdGd = new GridData(GridData.FILL_HORIZONTAL);
		wdGd.horizontalSpan = 2;
		workingDirectory.setLayoutData(wdGd);
		workingDirectory.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		workingDirectory.setEnabled(false);
		
		createVerticalSpacer(panel, 2);
		
		Label NOTE = new Label(panel,SWT.NONE);
		GridData noteGD = new GridData(GridData.FILL_HORIZONTAL);
		noteGD.horizontalSpan = 3;
		NOTE.setLayoutData(noteGD);
		NOTE.setText("Note:\n    You can through \"project's Properties->Build and Run\" to modify those arguments.");
		
		setControl(panel);
	}

	@Override
	public String getName() {
		return "Main";
	}

	@Override
	public void initializeFrom(ILaunchConfiguration config) {
		init();
	}
	
	private void init(){
		IProject project = MProject.get();
		if(project == null){
			isOutputBtn.setSelection(false);
			currentProjectName.setEnabled(false);
			isOutputBtn.setEnabled(false);
			maxStatusNumberText.setEnabled(false);
			statusIntervalText.setEnabled(false);
			commandLineArgs.setEnabled(false);
			
			workingDirectory.setEnabled(false);
			return;
		}else{
			currentProjectName.setEnabled(false);
			
			isOutputBtn.setEnabled(true);
			maxStatusNumberText.setEnabled(true);
			statusIntervalText.setEnabled(true);
			commandLineArgs.setEnabled(true);
			
			workingDirectory.setEnabled(false);
		}
		currentProjectName.setText(project.getName());
		try {
			if(Property.get(project, PType.ISOUTPUT).equals("true")){
				isOutputBtn.setSelection(true);
				maxStatusNumberText.setEnabled(true);
				statusIntervalText.setEnabled(true);
				
			}else{
				isOutputBtn.setSelection(false);
				maxStatusNumberText.setEnabled(false);
				statusIntervalText.setEnabled(false);
			}
			maxStatusNumberText.setText(Property.get(project, PType.MAXSTATUS));
			statusIntervalText.setText(Property.get(project, PType.INTERVAL));
			commandLineArgs.setText(Property.get(project, PType.CMDLINEARGS));
			workingDirectory.setText(Property.get(project, PType.WORKINGDIR));
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		//======================================
		currentProjectName.setEnabled(false);
		isOutputBtn.setEnabled(false);
		maxStatusNumberText.setEnabled(false);
		statusIntervalText.setEnabled(false);
		commandLineArgs.setEnabled(false);
		workingDirectory.setEnabled(false);
		//======================================
	}
	
	@Override
	public void performApply(ILaunchConfigurationWorkingCopy config) {
//		IProject project = MProject.get();
//		if(project != null){
//			storeProperty(project);
//		}
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy config) {
//		try {
//			IProject project = MProject.get();
//			if(project != null){
//				Property.init(project);
//			}
//			init();
//		} catch (CoreException e) {
//			e.printStackTrace();
//		}
	}
	
//	private void storeProperty(IProject project){
//		try{
//			Property.set(project, PType.ISOUTPUT, isOutputBtn.getSelection()?"true":"false");
//			Property.set(project, PType.MAXSTATUS, maxStatusNumberText.getText().trim());
//			Property.set(project, PType.INTERVAL, statusIntervalText.getText().trim());
//			Property.set(project, PType.CMDLINEARGS, commandLineArgs.getText());
//			Property.set(project, PType.WORKINGDIR, workingDirectory.getText().trim());
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//	}
}
