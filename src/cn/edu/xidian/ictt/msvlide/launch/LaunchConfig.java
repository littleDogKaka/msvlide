package cn.edu.xidian.ictt.msvlide.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;


public class LaunchConfig {
	public static final String LAUNCH_CONFIGURATION_MODE_KEY = "MSVL_MODE";
	public static final String LAUNCH_CONFIGURATION_MODE_RUN = "run";
	public static final String LAUNCH_CONFIGURATION_MODE_DEBUG = "debug";
	public static final String LAUNCH_CONFIGURATION_MODE_PMC = "pmc";
	public static final String LAUNCH_CONFIGURATION_MODE_UMC = "umc";
	
	public static final String LAUNCH_CONFIGURATION_PROJECT_NAME_KEY = "MSVL_PROJECT_NAME";
	
	private static final String LAUNCH_CONFIGURATION_NAME_SUMULATION = "MSVL Application";
	private static final String LAUNCH_CONFIGURATION_NAME_VERIFICATION_PMC = "Parallel Model Checking";
	private static final String LAUNCH_CONFIGURATION_NAME_VERIFICATION_UMC = "Unified Model Checking";
	
	private static final String LAUNCH_CONFIGURATION_TYPE_KEY = "cn.edu.xidian.ictt.msvlide.launch.ConfigurationType";
	
	public static ILaunchManager Manager = DebugPlugin.getDefault().getLaunchManager();
	
	public static ILaunchConfiguration find(String mode, String projectName) {
        ILaunchConfiguration configuration = null;
        try {
            ILaunchConfiguration[] configs = Manager.getLaunchConfigurations();
            for (ILaunchConfiguration config: configs) {
                if (config.getName().equals(name(mode))) {
                	configuration = config;
                	break;
                }
            }
        } catch (CoreException e) {
            e.printStackTrace();
        }
        if (configuration == null) {
            configuration = create(mode,projectName);
        }else{
        	 try {
				ILaunchConfigurationWorkingCopy wc = configuration.getWorkingCopy();
				wc.setAttribute(LAUNCH_CONFIGURATION_MODE_KEY, mode);
                wc.setAttribute(LAUNCH_CONFIGURATION_PROJECT_NAME_KEY, projectName);
                configuration = wc.doSave();
			} catch (CoreException e) {
				e.printStackTrace();
			}
        }
        return configuration;
    }

    /**
     * Creates a new configuration associated with the given file.
     * 
     * @return ILaunchConfiguration
     */
    private static ILaunchConfiguration create(String mode, String projectName) {
        ILaunchConfiguration config = null;
        ILaunchConfigurationType type = Manager.getLaunchConfigurationType(LaunchConfig.LAUNCH_CONFIGURATION_TYPE_KEY);
        try {
            if (type != null) {
                ILaunchConfigurationWorkingCopy wc = type.newInstance(null, Manager.generateLaunchConfigurationName(name(mode)));
                wc.setAttribute(LAUNCH_CONFIGURATION_MODE_KEY, mode);
                wc.setAttribute(LAUNCH_CONFIGURATION_PROJECT_NAME_KEY, projectName);
                config = wc.doSave();
            }
        } catch (CoreException e) {
            e.printStackTrace();
        }
        return config;
    }

    private static String name(String mode){
    	if(mode.equals(LAUNCH_CONFIGURATION_MODE_UMC)){
    		return LAUNCH_CONFIGURATION_NAME_VERIFICATION_UMC;
    	}else if(mode.equals(LAUNCH_CONFIGURATION_MODE_PMC)){
    		return LAUNCH_CONFIGURATION_NAME_VERIFICATION_PMC;
    	}else{
    		return LAUNCH_CONFIGURATION_NAME_SUMULATION;
    	}
    }
    
}
