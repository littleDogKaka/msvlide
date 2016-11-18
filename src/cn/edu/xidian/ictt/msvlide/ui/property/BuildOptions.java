package cn.edu.xidian.ictt.msvlide.ui.property;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;

import cn.edu.xidian.ictt.msvlide.project.util.MProject;
import cn.edu.xidian.ictt.msvlide.project.util.PType;
import cn.edu.xidian.ictt.msvlide.project.util.Property;

public class BuildOptions extends PropertyPage{

	public static final String TITLE = "Build options and Run arguments";
	private Button isOutputBtn;
	private Text maxStatusNumberText;
	private Text statusIntervalText;
	private Text commandLineArgs;
	private Text workingDirectory;
	private boolean isValid[] = {true,true};
	
	@Override
	protected Control createContents(Composite parent) {
		Composite panel = new Composite(parent,SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.verticalSpacing = 8;
		panel.setLayout(layout);

		// Build options
		Group boGroup = new Group(panel,SWT.NULL);
		boGroup.setText("Build options:");
		
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
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		Label maxStatusNumberLabel = new Label(boGroup,SWT.NULL);
		maxStatusNumberLabel.setText("Max status number:");
		maxStatusNumberLabel.setLayoutData(new GridData());
		
		maxStatusNumberText = new Text(boGroup,SWT.BORDER | SWT.SINGLE);
		GridData msnGd = new GridData(GridData.FILL_HORIZONTAL);
		msnGd.horizontalSpan = 2;
		maxStatusNumberText.setLayoutData(msnGd);
		maxStatusNumberText.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				try{
					setTitle(TITLE);
					isValid[0] = true;
					Integer.parseInt(maxStatusNumberText.getText().trim(),10);
				}catch(NumberFormatException ee){
					isValid[0] = false;
					setErrorMessage("Please input valid number");
				}
			}
		});
		
		Label statusIntervalLabel = new Label(boGroup,SWT.NONE);
		statusIntervalLabel.setText("Status interval:");
		statusIntervalLabel.setLayoutData(new GridData());
		
		statusIntervalText = new Text(boGroup,SWT.BORDER | SWT.SINGLE);
		GridData siGd = new GridData(GridData.FILL_HORIZONTAL);
		siGd.horizontalSpan = 2;
		statusIntervalText.setLayoutData(siGd);
		statusIntervalText.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				try{
					setTitle(TITLE);
					isValid[1] = true;
					Integer.parseInt(statusIntervalText.getText().trim(),10);
				}catch(NumberFormatException ee){
					isValid[1] = false;
					setErrorMessage("Please input valid number");
				}
			}
		});
		
		
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
		commandLineArgsLabel.setText("Command-line arguments:");
		commandLineArgsLabel.setLayoutData(new GridData());
		
		commandLineArgs = new Text(raGroup,SWT.BORDER | SWT.SINGLE);
		GridData claGd = new GridData(GridData.FILL_HORIZONTAL);
		claGd.horizontalSpan = 2;
		commandLineArgs.setLayoutData(claGd);
		
		Label workingDirectoryLabel = new Label(raGroup,SWT.NONE);
		workingDirectoryLabel.setText("Working directory:");
		workingDirectoryLabel.setLayoutData(new GridData());
		
		workingDirectory = new Text(raGroup,SWT.BORDER | SWT.SINGLE);
		GridData wdGd = new GridData(GridData.FILL_HORIZONTAL);
		wdGd.horizontalSpan = 2;
		workingDirectory.setLayoutData(wdGd);
		
		
		try{
			IProject project = MProject.get();
			String isOutput = Property.get(project, PType.ISOUTPUT);
			String maxstatus = Property.get(project, PType.MAXSTATUS);
			String interval = Property.get(project, PType.INTERVAL);
			String cmdline = Property.get(project, PType.CMDLINEARGS);
			String workdir = Property.get(project, PType.WORKINGDIR);
			maxStatusNumberText.setText(maxstatus);
			statusIntervalText.setText(interval);
			commandLineArgs.setText(cmdline);
			workingDirectory.setText(workdir);
			if(isOutput.equals("true")){
				isOutputBtn.setSelection(true);
			}else{
				isOutputBtn.setSelection(false);
				maxStatusNumberText.setEnabled(false);
				statusIntervalText.setEnabled(false);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		workingDirectory.setEnabled(false);
		return panel;
	}

	@Override
	protected void performApply() {
		if(isValid[0] && isValid [1]){
			storeProperty();
			super.performApply();
		}else{
			setErrorMessage("Properties are invalide");
		}
	}

	@Override
	protected void performDefaults() {
		isOutputBtn.setSelection(false);
		maxStatusNumberText.setEnabled(false);
		statusIntervalText.setEnabled(false);
		maxStatusNumberText.setText("1000000");
		statusIntervalText.setText("1");
		commandLineArgs.setText("");
		workingDirectory.setText(MProject.get().getLocation().toString());
		super.performDefaults();
	}

	@Override
	public boolean performOk() {
		if(isValid[0] && isValid [1]){
			storeProperty();
			return super.performOk();
		}else{
			setErrorMessage("Properties are invalide");
			return false;
		}
	}
	
	private boolean storeProperty(){
		boolean rtn = true;
		try{
			IProject project = MProject.get();
			Property.set(project, PType.ISOUTPUT, isOutputBtn.getSelection()?"true":"false");
			Property.set(project, PType.MAXSTATUS, maxStatusNumberText.getText().trim());
			Property.set(project, PType.INTERVAL, statusIntervalText.getText().trim());
			Property.set(project, PType.CMDLINEARGS, commandLineArgs.getText());
			Property.set(project, PType.WORKINGDIR, workingDirectory.getText().trim());
		}catch(Exception e){
			rtn = false;
			e.printStackTrace();
		}
		return rtn;
	}
}
