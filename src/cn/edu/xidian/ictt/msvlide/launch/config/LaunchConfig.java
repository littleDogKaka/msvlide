package cn.edu.xidian.ictt.msvlide.launch.config;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;

import cn.edu.xidian.ictt.msvlide.project.util.PType;
import cn.edu.xidian.ictt.msvlide.project.util.Property;

public class LaunchConfig {
	public static final String LAUNCH_CONFIG_TYPE_ID = "cn.edu.xidian.ictt.msvlide.launch.ConfigurationType";
	public static final String LAUNCH_CONFIG_KEY_MODE = "MSVL_MODE";
	public static final String LAUNCH_CONFIG_KEY_PROJECT_NAME = "MSVL_PROJECT_NAME";
	public static final String LAUNCH_CONFIG_KEY_RUN_FILENAME = "MSVL_RUN_FILENAME";
	public static final String LAUNCH_CONFIG_KEY_WD = "MSVL_RUN_WD";
	public static final String LAUNCH_CONFIG_KEY_ARGS = "MSVL_RUN_ARGS";
	
	public static final String LAUNCH_CONFIG_MODE_RUN = "run";
	public static final String LAUNCH_CONFIG_MODE_DEBUG = "debug";
	public static final String LAUNCH_CONFIG_MODE_PMC = "pmc";
	public static final String LAUNCH_CONFIG_MODE_UMC = "umc";
	
	public static final String LAUNCH_CONFIG_NAME_SUMULATION = "MSVL Application";
	public static final String LAUNCH_CONFIG_NAME_VERIFICATION_PMC = "Parallel Model Checking";
	public static final String LAUNCH_CONFIG_NAME_VERIFICATION_UMC = "Unified Model Checking";
	
	public static ILaunchManager Manager;
	static{
		Manager = DebugPlugin.getDefault().getLaunchManager();
	}
	
	public static ILaunchConfiguration find(String mode, IProject project, String name) {
        ILaunchConfiguration configuration = null;
        try {
            ILaunchConfiguration[] configs = Manager.getLaunchConfigurations();
            for (ILaunchConfiguration config: configs) {
                if (config.getName().equals(name(mode,project))) {
                	ILaunchConfigurationWorkingCopy wc = config.getWorkingCopy();
                	configuration = setWC(wc,project,mode,name);
                	break;
                }
            }
        } catch (CoreException e) {
            e.printStackTrace();
        }
        if (configuration == null) {
            configuration = create(mode,project,name);
        }
        return configuration;
    }

    private static ILaunchConfiguration create(String mode, IProject project,String name) {
        ILaunchConfiguration config = null;
        ILaunchConfigurationType type = Manager.getLaunchConfigurationType(LaunchConfig.LAUNCH_CONFIG_TYPE_ID);
        try {
            if (type != null) {
                ILaunchConfigurationWorkingCopy wc = type.newInstance(null, Manager.generateLaunchConfigurationName(name(mode,project)));
                config = setWC(wc,project,mode,name);
            }
        } catch (CoreException e) {
            e.printStackTrace();
        }
        return config;
    }

    private static String name(String mode, IProject project){
    	String rtn = project.getName() + "-";
    	if(mode.equals(LAUNCH_CONFIG_MODE_UMC)){
    		return rtn + LAUNCH_CONFIG_NAME_VERIFICATION_UMC;
    	}else if(mode.equals(LAUNCH_CONFIG_MODE_PMC)){
    		return rtn + LAUNCH_CONFIG_NAME_VERIFICATION_PMC;
    	}else{
    		return rtn + LAUNCH_CONFIG_NAME_SUMULATION;
    	}
    }
    
    private static ILaunchConfiguration setWC(ILaunchConfigurationWorkingCopy wc, IProject project, String mode, String name){
    	ILaunchConfiguration config = null;
    	try {
    		wc.setAttribute(LAUNCH_CONFIG_KEY_MODE, mode);
            wc.setAttribute(LAUNCH_CONFIG_KEY_PROJECT_NAME, project.getName());
            wc.setAttribute(LAUNCH_CONFIG_KEY_RUN_FILENAME,name);
			wc.setAttribute(LAUNCH_CONFIG_KEY_WD,Property.get(project, PType.WORKINGDIR));
			wc.setAttribute(LAUNCH_CONFIG_KEY_ARGS,Property.get(project, PType.CMDLINEARGS));
	        config = wc.doSave();
		} catch (CoreException e) {
			e.printStackTrace();
		}
        return config;
    }
}