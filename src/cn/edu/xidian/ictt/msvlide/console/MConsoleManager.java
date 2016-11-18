package cn.edu.xidian.ictt.msvlide.console;

import java.util.HashMap;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IOConsole;

public class MConsoleManager {
	
	private static HashMap<String,IOConsole> map = new HashMap<String,IOConsole>();
	private static IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();
	
	// public
	public static RunConsole get(String name){
		if(map.containsKey(name)){
			return new RunConsole(name, map.get(name));
		}
		return new RunConsole(name, add(name));
	}
	
	public static void remove(RunConsole runConsole){
		remove(runConsole.getName());
	}
	
	public static boolean isExist(RunConsole runConsole){
		String name = runConsole.getName();
		return map.containsKey(name);
	}
	
	public static void removeAll(){
		manager.removeConsoles((IConsole[]) map.values().toArray());
		map.clear();
	}
	
	// private
	private static IOConsole add(String name){
		IOConsole console = new IOConsole(name,null);
		map.put(name, console);
		manager.addConsoles(new IOConsole[]{console});
		return console;
	}
	
	private static void remove(String name){
		IOConsole console = map.remove(name);
		if(console != null){
			manager.removeConsoles(new IOConsole[]{console});
		}
		console.destroy();
	}
}
