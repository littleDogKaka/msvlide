package cn.edu.xidian.ictt.msvlide.project.util;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;


public class Property {
	
	public static String get(IProject project, PType type) throws CoreException{
		return project.getPersistentProperty(new QualifiedName("",type.toString()));
	}
	
	public static void set(IProject project,PType type,String value) throws CoreException{
		project.setPersistentProperty(new QualifiedName("",type.toString()), value);
	}
	
	public static void init(IProject project) throws CoreException{
		project.setPersistentProperty(new QualifiedName("",PType.ISOUTPUT.toString()),"false");
		project.setPersistentProperty(new QualifiedName("",PType.MAXSTATUS.toString()),"1000000");
		project.setPersistentProperty(new QualifiedName("",PType.INTERVAL.toString()),"1");
		project.setPersistentProperty(new QualifiedName("",PType.CMDLINEARGS.toString()),"");
		project.setPersistentProperty(new QualifiedName("",PType.WORKINGDIR.toString()),project.getLocation().toString());
	}
	
	public static void setDefault(IProject _project) throws CoreException{
		init(_project);
	}

	
}
