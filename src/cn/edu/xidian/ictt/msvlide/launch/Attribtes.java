package cn.edu.xidian.ictt.msvlide.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;


public class Attribtes {
	
	public static final String LAUNCH_CONFIGURATION_NAME = "MSVL Application";
	public static final String LAUNCH_CONFIGURATION_TYPE = "cn.edu.xidian.ictt.msvlide.launch.ConfigurationType";
	//public static final String LAUNCH_CONFIGURATION_CHANGE = "LAUNCH_CONFIGURATION_CHANGE";
	
	public static ILaunchManager Manager = DebugPlugin.getDefault().getLaunchManager();
	
	public static ILaunchConfiguration findLaunchConfig(String mode) {
        ILaunchConfiguration configuration = null;
        try {
            ILaunchConfiguration[] configs = Manager.getLaunchConfigurations();
            for (ILaunchConfiguration config: configs) {
                if (config.getName().equals(LAUNCH_CONFIGURATION_NAME)) {
                	configuration = config;
                	break;
                }
            }
        } catch (CoreException e) {
            e.printStackTrace();
        }
        if (configuration == null) {
            configuration = createConfig();
        }
        return configuration;
    }

    /**
     * Creates a new configuration associated with the given file.
     * 
     * @return ILaunchConfiguration
     */
    public static ILaunchConfiguration createConfig() {
        ILaunchConfiguration config = null;
        ILaunchConfigurationType type = Manager.getLaunchConfigurationType(Attribtes.LAUNCH_CONFIGURATION_TYPE);
        try {
            if (type != null) {
                ILaunchConfigurationWorkingCopy wc = type.newInstance(null, Manager.generateLaunchConfigurationName(LAUNCH_CONFIGURATION_NAME));
                wc.setAttribute(LAUNCH_CONFIGURATION_NAME, LAUNCH_CONFIGURATION_NAME);
                //wc.setAttribute(LAUNCH_CONFIGURATION_CHANGE, 0);
                //wc.setAttribute(CMD_ARGUMENTS, Property.get(project, PType.CMDLINEARGS));
                //wc.setAttribute(WORKING_DIR, project.getLocation().toString());
                config = wc.doSave();
            }
        } catch (CoreException e) {
            e.printStackTrace();
        }
        return config;
    }

}
