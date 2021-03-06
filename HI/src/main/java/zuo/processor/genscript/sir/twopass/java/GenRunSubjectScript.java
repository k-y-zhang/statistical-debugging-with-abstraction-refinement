package zuo.processor.genscript.sir.twopass.java;

import java.io.File;
import java.util.Iterator;


public class GenRunSubjectScript extends AbstractGenRunScript {
	
	public GenRunSubjectScript(String sub,String _, String ver, String cc, String sD, String eD, String oD, String scD) {
		super(sub, ver, null, cc, sD, eD, oD, scD);
		this.mkOutDir();
	}


	@Override
	public void genRunScript() {
		StringBuffer code = new StringBuffer();
		code.append(compileCommand + "\n"); // compile subject program
		code.append("echo script: " + subject + "_" + version + "\n");
		code.append("export SUBJECTDIR=" + executeDir + "\n");
		
		for (Iterator<Integer> it = inputsMap.keySet().iterator(); it.hasNext();) {
			int index = it.next();
			code.append(runinfo + index + "\"\n");// running info
			code.append(inputsMap.get(index).replace(EXE, "$SUBJECTDIR/" + version + ".exe "));
			code.append("\n");
		}
		code.append("mv ../outputs/* " + outputDir + "\n");
		printToFile(code.toString(), scriptDir, version + ".sh");
		
	}


	@Override
	protected void mkOutDir() {
		File fd = new File(outputDir);
		if(!fd.exists()){
			fd.mkdirs();
		}
		File fdx = new File(executeDir);
		if(!fdx.exists()){
			fdx.mkdirs();
		}
	}
	

}
