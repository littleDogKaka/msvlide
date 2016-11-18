package cn.edu.xidian.ictt.msvlide.project.util;

public enum PType {
	ISOUTPUT("ISOUTPUT"),
	MAXSTATUS("MAXSTATUS"),
	INTERVAL("INTERVAL"),
	CMDLINEARGS("CMDLINEARGS"),
	WORKINGDIR("WORKINGDIR");
	
	private String value;
	
	private PType(String value){
		this.value = value;
	}

	@Override
	public String toString() {
		return this.value;
	}
}
