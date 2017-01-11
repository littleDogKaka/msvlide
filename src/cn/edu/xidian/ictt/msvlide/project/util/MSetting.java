package cn.edu.xidian.ictt.msvlide.project.util;

public class MSetting {
	public static final String FOLDER_BIN = "bin";
	public static final String FOLDER_SRC = "src";
	public static final String FOLDER_SRC_C = "src-c";
	public static final String FOLDER_SRC_VERILOG = "src-verilog";
	public static final String FOLDER_SRC_VHDL = "src-VHDL";
	public static final String FOLDER_OUT_PMC = "out-pmc";
	public static final String FOLDER_OUT_UMC = "out-umc";
	
	public static final String FILE_MAIN_NAME = "main.m";
	public static final String FILE_MAIN_INIT = "frame(x) and (\n\tint x <== 123456 and skip;\n\toutput(x) and skip\n)";
	public static final String FILE_MAIN_SUFFIX = ".m";
	public static final String FILE_FUNC_SUFFIX = ".func";
	public static final String FILE_HEADER_SUFFIX = ".h";
	public static final String FILE_RUNNABLE_SUFFIX = ".exe";
	
	public static final String CONVERT_C_TO_MSVL = "C2M.exe";
	public static final String FILE_C_SUFFIX = ".c";
	public static final String CONVERT_VERILOG_TO_MSVL = "V2M.exe";
	public static final String FILE_VERILOG_SUFFIX = ".v";
	public static final String CONVERT_VHDL_TO_MSVL = "VHDL2MSVL.exe";
	public static final String FILE_VHDL_SUFFIX = ".vhd";
	
	public static final String MSVL_COMPILER = "MC";
	
	public static final String PMCHECKER = "PMC_MSV.exe";
	public static final String PMC_RUNFILE_NAME = "IRResult.exe";
	public static final String UMCHECKER = "UMC_MSV.exe";
	public static final String UMC_RUNFILE_NAME = "IRResult.exe";
}
