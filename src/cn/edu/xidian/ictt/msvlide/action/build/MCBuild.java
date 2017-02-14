package cn.edu.xidian.ictt.msvlide.action.build;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import cn.edu.xidian.ictt.msvlide.project.builder.MSVLBuilder;
import cn.edu.xidian.ictt.msvlide.project.builder.PMCBuilder;
import cn.edu.xidian.ictt.msvlide.project.builder.UMCBuilder;
import cn.edu.xidian.ictt.msvlide.project.util.MSetting;

public class MCBuild implements IRunnableWithProgress{

	IProject project;
	String pFilename;
	String mode;
	
	public MCBuild(IProject project, String mode, String pFilename){
		this.project = project;
		this.mode = mode;
		this.pFilename = pFilename;
	}
	
	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException{
		try{
			HashMap<String,String> map = new HashMap<String,String>();
			map.put(MSetting.BUILD_MAP_KEY_MODE, mode);
			map.put(MSetting.BUILD_MAP_KEY_FILE_PROPERTY, pFilename);
			
			String builderID;
			if(mode.equals(MSetting.BUILD_MODE_PMC_M) || mode.equals(MSetting.BUILD_MODE_PMC_S))
				builderID = PMCBuilder.BUILDER_ID;
			else if(mode.equals(MSetting.BUILD_MODE_UMC_M) || mode.equals(MSetting.BUILD_MODE_UMC_S))
				builderID = UMCBuilder.BUILDER_ID;
			else 
				builderID = MSVLBuilder.BUILDER_ID;
			
			project.build(PMCBuilder.FULL_BUILD, builderID, map, monitor);
		}catch(CoreException e){
			e.printStackTrace();
		}
	}

}
