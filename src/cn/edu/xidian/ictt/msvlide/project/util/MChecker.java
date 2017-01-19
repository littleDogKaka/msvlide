package cn.edu.xidian.ictt.msvlide.project.util;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;

import cn.edu.xidian.ictt.msvlide.console.MConsole;
import cn.edu.xidian.ictt.msvlide.util.NameFilter;

public class MChecker {
	
	public static int checkMainFile(IProject project){
		IFolder folder = project.getFolder(MSetting.FOLDER_SRC);
		if(folder == null || !folder.exists() || !folder.isAccessible()){
			MConsole.print("ERROR: " + MSetting.FOLDER_SRC + " cannot be accessed ", true);
			return -1;
		}
		
		String[] list = folder.getRawLocation().toFile().list(new NameFilter(MSetting.FILE_MAIN_SUFFIX));
		if(list.length > 1){
			MConsole.print("ERROR: There can only be one " + MSetting.FILE_MAIN_SUFFIX + " file in " + MSetting.FOLDER_SRC, true);
		}else if(list.length == 0){
			MConsole.print("ERROR: There must be one " + MSetting.FILE_MAIN_SUFFIX + " file in " + MSetting.FOLDER_SRC, true);
		}
		return list.length;
	}

}
