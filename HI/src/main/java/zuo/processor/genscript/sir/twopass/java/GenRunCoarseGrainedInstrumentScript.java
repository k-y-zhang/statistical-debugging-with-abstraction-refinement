package zuo.processor.genscript.sir.twopass.java;

import zuo.util.file.FileUtility;

import java.io.File;
import java.util.Iterator;
import java.util.List;


public class GenRunCoarseGrainedInstrumentScript extends AbstractGenRunScript implements GenRunInstrumentScript {
	final String traceDir;
	
	final List<Integer> failingTests;
	final List<Integer> passingTests;
	
//	private final Set<Integer> indices;
	
	public GenRunCoarseGrainedInstrumentScript(String sub,String _, String ver, String subV, String cc, String sD, String eD, String oD, String scD, String tD, String failing, String passing ) {
		super(sub, ver, subV, cc, sD, eD, oD, scD);
		this.traceDir = tD;
		this.mkOutDir();
		this.failingTests = FileUtility.readInputsArray(failing);
		this.passingTests = FileUtility.readInputsArray(passing);
		
//		this.indices = FileCollection.readIndices(indices);
	}

	@Override
	public void genRunScript() {
		StringBuffer code = new StringBuffer();
		code.append(compileCommand + "\n");
		code.append("echo script: " + subVersion + "\n");
		code.append("export VERSIONSDIR=" + executeDir + "\n");
		code.append("export TRACESDIR=" + traceDir + "\n");
		code.append("rm $TRACESDIR/o*profile\n");

		stmts(code);
		code.append(startTimeCommand + "\n");
		for(int j = 0; j < ROUNDS; j++){
			stmts(code);
		}
		code.append(endTimeCommand + " >& " + outputDir + "time\n");
		
		code.append("rm -f ../outputs/*\n");
//		code.append("rm $TRACESDIR/o*profile\n");
		
		printToFile(code.toString(), scriptDir, version + "_" + subVersion + "_cg.sh");
	}

	private void stmts(StringBuffer code) {
		for (Iterator<Integer> it = failingTests.iterator(); it.hasNext();) {
			int index = it.next();
//			assert(this.indices.contains(index));
			code.append(runinfo + index + "\"\n");// running info
			code.append("export SAMPLER_FILE=$TRACESDIR/o" + index + ".fprofile\n");
			code.append(inputsMap.get(index));
			code.append("\n");
		}
		
		for (int i = 0; i < passingTests.size(); i++) {
			int index = passingTests.get(i);
//			if(this.indices.contains(index)){
				code.append(runinfo + index + "\"\n");// running info
				code.append("export SAMPLER_FILE=$TRACESDIR/o" + index + ".pprofile\n");
				code.append(inputsMap.get(index));
				code.append("\n");
//			}
		}
	}

	@Override
	protected void mkOutDir() {
		//make directory for outputs
		File fo = new File(outputDir);
		if(!fo.exists()){
			fo.mkdirs();
		}
		
		//make directory for traces
		File ft = new File(traceDir);
		if(!ft.exists()){
			ft.mkdirs();
		}
	}

}
