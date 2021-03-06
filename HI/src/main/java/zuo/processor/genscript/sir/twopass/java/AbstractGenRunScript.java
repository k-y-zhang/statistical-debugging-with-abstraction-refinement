package zuo.processor.genscript.sir.twopass.java;

import zuo.processor.genscript.client.twopass.GenSirScriptClient;
import zuo.util.file.FileUtility;

import java.io.*;
import java.util.Map;

public abstract class AbstractGenRunScript {
	public static final int ROUNDS = 3;
	
	final String subVersion;
	final String version;
	final String subject;
	final String compileCommand;
	
	final String sourceDir;
	final String executeDir;
	final String outputDir;
	
	final String scriptDir;
	
	public final static String runinfo = "echo \">>>>>>> running test ";
	public static final String EXE = "EXE";
	
	protected final Map<Integer, String> inputsMap;
	protected final Map<Integer, String> inputsCompMap;
	
	protected final String startTimeCommand = "stime=\"$(date +%s%N)\"";
	protected final String endTimeCommand = "time=\"$(($(date +%s%N)-stime))\"\n" +
			"echo \"Time in seconds: $((time/1000000000)) \nTime in milliseconds: $((time/1000000))\"";
	
	
	public AbstractGenRunScript(String sub, String ver, String subV, String cc, String source, String execute, String output, String script){
		this.subject = sub;
		this.version = ver;
		this.subVersion = subV;
		this.compileCommand = cc;
		this.sourceDir = source;
		this.executeDir = execute;
		this.outputDir = output;
		this.scriptDir = script;
		
		inputsMap = FileUtility.readInputsMap(GenSirScriptClient.rootDir + subject + "/testplans.alt/" + "inputs.map");
		inputsCompMap = FileUtility.readInputsMap(GenSirScriptClient.rootDir + subject + "/testplans.alt/" + "inputsComp.map");
	}
	
    public abstract void genRunScript() throws IOException;
    protected abstract void mkOutDir();
    
    public static void printToFile(String scr, String folder, String file){
		PrintWriter pout = null;
		try{
			File fd = new File(folder);
			if(!fd.exists()){
				fd.mkdir();
			}
			pout = new PrintWriter(new BufferedWriter(new FileWriter(folder + "/" + file)));
			pout.print(scr);
		}
		catch(IOException e){
			e.printStackTrace();
		}
		finally{
			pout.close();
		}
	}
	
}
