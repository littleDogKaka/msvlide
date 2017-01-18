package cn.edu.xidian.ictt.msvlide.project.util;

public class MSetting {
	public static final String FOLDER_BIN = "bin";
	public static final String FOLDER_SRC = "src";
	public static final String FOLDER_SRC_C = "src-c";
	public static final String FOLDER_SRC_VERILOG = "src-verilog";
	public static final String FOLDER_SRC_VHDL = "src-VHDL";
	public static final String FOLDER_PMC = "pmc";
	public static final String FOLDER_UMC = "umc";
	
	public static final String FILE_MAIN_NAME = "main";
	public static final String FILE_MAIN_SUFFIX = ".m";
	public static final String FILE_MAIN_INIT = "frame(x) and (\n\tint x <== 123456 and skip;\n\toutput(x) and skip\n)";
	
	public static final String FILE_FUNC_NAME = "new";
	public static final String FILE_FUNC_SUFFIX = ".func";
	public static final String FILE_FUNC_INIT = "";
	
	public static final String FILE_HEADER_NAME = "new";
	public static final String FILE_HEADER_SUFFIX = ".mh";
	public static final String FILE_HEADER_INIT = "";
	
	public static final String FILE_PROPERTY_NAME = "new";
	public static final String FILE_PROPERTY_SUFFIX = ".p";
	public static final String FILE_PROPERTY_INIT = "";
	
	public static final String FILE_INIT_NAME = "new";
	public static final String FILE_C_SUFFIX = ".c";
	public static final String FILE_VERILOG_SUFFIX = ".v";
	public static final String FILE_VHDL_SUFFIX = ".vhd";
	
	public static final String FILE_IR_SUFFIX = ".bc";
	public static final String FILE_RUNNABLE_SUFFIX = ".exe";
	
	public static final String CONVERT_C_TO_MSVL = "C2M";
	public static final String CONVERT_VERILOG_TO_MSVL = "V2M";
	public static final String CONVERT_VHDL_TO_MSVL = "VHDL2MSVL";
	public static final String MSVL_COMPILER = "MC";
	public static final String PMCHECKER = "PMC_MSV";
	public static final String PMC_RUNFILE_NAME = "IRResult.exe";
	public static final String UMCHECKER = "UMC_MSV";
	public static final String UMC_RUNFILE_NAME = "IRResult.exe";
	
	
	public static final String BUILD_MAP_KEY_FILE_MAIN = "BUILD_MAP_KEY_FILE_MAIN";
	public static final String BUILD_MAP_KEY_FILE_PROPERTY = "BUILD_MAP_KEY_FILE_PROPERTY";
	public static final String BUILD_MAP_KEY_MODE = "BUILD_MAP_KEY_MODE";
	public static final String BUILD_MODE_RUN = "BUILD_MODE_RUN";
	public static final String BUILD_MODE_PMC = "BUILD_MODE_PMC";
	public static final String BUILD_MODE_UMC = "BUILD_MODE_UMC";
}
