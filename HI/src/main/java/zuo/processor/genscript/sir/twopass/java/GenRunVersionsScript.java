package zuo.processor.genscript.sir.twopass.java;

import zuo.processor.genscript.client.twopass.GenSirScriptClient;

import java.io.File;
import java.util.Iterator;


public class GenRunVersionsScript extends AbstractGenRunScript {
	
	public static final String SUBV = "SUBV";


	public GenRunVersionsScript(String sub,String _, String ver, String subV, String cc, String sD, String eD, String oD, String scD) {
		super(sub, ver, subV, cc, sD, eD, oD, scD);
	    this.mkOutDir();
	}


	@Override
	public void genRunScript() {
		StringBuffer code = new StringBuffer();
		code.append(compileCommand + "\n");// compiling
		code.append("echo script: " + subVersion + "\n");
		code.append("export VERSIONSDIR=" + executeDir + "\n");
		
		stmts(code);
		code.append(startTimeCommand + "\n");
		for(int j = 0; j < ROUNDS; j++){
			stmts(code);
		}
		code.append(endTimeCommand + " >& " + outputDir + "time\n");
		
		code.append("rm ../outputs/*\n");
		code.append("\n\n");
		
		code.append("rm $VERSIONSDIR/" + GenSirScriptClient.outCompFile + "\n");
		code.append("echo script: " + subVersion + "\n");
		for (Iterator<Integer> it = inputsCompMap.keySet().iterator(); it.hasNext();) {
			int index = it.next();
			code.append(runinfo + index + "\"\n");// running info
			code.append(inputsCompMap.get(index).replace(EXE, "$VERSIONSDIR/" + version + ".exe ").replace(SUBV, version + "/" + subVersion));//executables
			code.append("\n");
		}
		code.append("mv ../outputs/* " + outputDir + "\n");
		
		printToFile(code.toString(), scriptDir, version + "_" + subVersion + ".sh");
		
	}


	private void stmts(StringBuffer code) {
		for (Iterator<Integer> it = inputsMap.keySet().iterator(); it.hasNext();) {
			int index = it.next();
			code.append(runinfo + index + "\"\n");// running info
			code.append(inputsMap.get(index).replace(EXE, "$VERSIONSDIR/" + version + ".exe "));//executables
			code.append("\n");
		}
	}
	
	
	protected void mkOutDir(){
		File fp = new File(outputDir);
		if(!fp.exists()){
			fp.mkdirs();
		}
		File fo = new File(executeDir);
		if(!fo.exists()){
			fo.mkdirs();
		}
	}

}
