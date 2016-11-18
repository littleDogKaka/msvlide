package cn.edu.xidian.ictt.msvlide.console;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;

public class RunConsole{
	
	private String name = null;
	private IOConsole console = null;
	private boolean isRun = false;
	private boolean isExit = false;
	private int exitValue = -1;
	
	public RunConsole(String name,IOConsole console){
		this.name = name;
		this.console = console;
	}
	
	public void Run(String[] cmds, File wdir,ILaunch launch){
		isRun = true;
		try {
			console.activate();
			IOConsoleOutputStream outputDisplay = console.newOutputStream();
			IOConsoleOutputStream errorDisplay = console.newOutputStream();
			
			Process p = DebugPlugin.exec(cmds, wdir);
			
			IProcess process= DebugPlugin.newProcess(launch, p, "My Process");
			//process.
//			new Thread(new ReadWrite(p.getErrorStream(), errorDisplay)).start();
//			if(input == null){
//				new Thread(new ReadWrite(console.getInputStream(),p.getOutputStream()));//???????
//			}else{
//				new Thread(new ReadWrite(input,p.getOutputStream()));
//			}
//			if(output == null){
//				new Thread(new ReadWrite(p.getInputStream(),outputDisplay)).start();
//			}else{
//				new Thread(new ReadWrite(p.getInputStream(),output)).start();
//			}

			p.waitFor();
			isExit = true;
			exitValue = p.exitValue();

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		isRun = false;
	}
	
	public boolean isRun(){
		return isRun;
	}
	
	public boolean isExit(){
		return isExit;
	}
	
	public int exitVule(){
		return exitValue;
	}

	public String getName(){
		return name;
	}
	
	class ReadWrite implements Runnable{

		private OutputStream output;
		private InputStream input;
		
		public ReadWrite(InputStream input, OutputStream output){
			this.input = input;
			this.output = output;
		}
		
		@Override
		public void run() {
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(input));
				String line;
				while ((line = reader.readLine()) != null) {
					output.write(line.getBytes());
					output.write("\n".getBytes());
					output.flush();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
