package cn.edu.xidian.ictt.msvlide.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DisplayOutput implements Runnable{
	
	private String filename;
	private InputStream input;
	
	public DisplayOutput(InputStream input,String filename){
		this.input = input;
		this.filename = filename;
	}
	
	@Override
	public void run() {
		try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String line;
            while ((line = reader.readLine()) != null) {
            	MConsole.print(filename + ": " + line, true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}
}
