package cn.edu.xidian.ictt.msvlide.console;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleFactory;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class MConsole implements IConsoleFactory{

	private static MessageConsole msgConsole = new MessageConsole("MSVL Builder",null);
	private static boolean msgConsoleExists = false;
	
	@Override
	public void openConsole() {
		if(msgConsoleExists && msgConsole != null){
			msgConsole.activate();
		}
	}
	
	public static int print(String message, boolean activate) {
		if(msgConsole != null){
			MessageConsoleStream printer = msgConsole.newMessageStream();  
		    printer.setActivateOnWrite(activate);
		    printer.println(message);
		    return 0;
		}else{
			System.out.println("Test msgConsole == null");
		}
		return -1;
	}
	
	public static void clear(){
		if(msgConsole != null){
			msgConsole.clearConsole();
		}
	}
	
	public static void open(){
		if(msgConsole != null && !msgConsoleExists){
            IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();  
            IConsole[] existing = manager.getConsoles();  
            msgConsoleExists = false;
            for (int i = 0; i < existing.length; i++) {  
                if (msgConsole.equals(existing[i])){
                	msgConsoleExists = true;
                	break;
                } 
            }  
            if (!msgConsoleExists) {  
                manager.addConsoles(new IConsole[] { msgConsole });  
            }  
            //msgConsole.activate(); 
		}
	}
	
	public static void close(){
		 IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();
		 if(msgConsole != null){
			 manager.removeConsoles(new IConsole[]{ msgConsole });
			 msgConsoleExists = false;
		 }
	}
}
