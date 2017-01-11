package cn.edu.xidian.ictt.msvlide.ui.launch;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationTabGroup;

public class ConfigTabGroup implements ILaunchConfigurationTabGroup{
	
	private ConfigTabMain mainTab;
	private CommonTab commonTab;
	
	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		this.mainTab = new ConfigTabMain();
		this.commonTab = new CommonTab();
	}

	@Override
	public void initializeFrom(ILaunchConfiguration config) {
		mainTab.initializeFrom(config);
		commonTab.initializeFrom(config);
	}
	
	@Override
	public void dispose() {}

	@Override
	public ILaunchConfigurationTab[] getTabs() {
		return new ILaunchConfigurationTab[]{mainTab,commonTab};
	}

	@Override
	public void launched(ILaunch launch) {
		System.out.println("Group launched");
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy config) {
		mainTab.performApply(config);
		commonTab.performApply(config);
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy config) {
		mainTab.setDefaults(config);
		commonTab.setDefaults(config);
	}

}
