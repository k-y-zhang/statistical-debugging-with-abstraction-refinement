package zuo.processor.genscript.client.twopass.java;

import sir.mts.MakeTestScript;
import zuo.processor.genscript.sir.twopass.java.*;
import zuo.processor.splitinputs.SirSplitInputs;
import zuo.util.file.FileUtility;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SienaGenSirScriptClient extends AbstractGenSirScriptClient {
	
	public final String subject;
	public final String sourceName;
	public final String version;
	public final String subVersion;
	public final String inputScript;
	public final String inputCompScript;
	
	public final String inputsMapFile;
	public final String inputsCompMapFile;
	
	final String ssourceDir;
	final String sexecuteDir;
	final String soutputDir;
	
	final String vsourceDir;
	final String vexecuteDir;
	final String voutputDir;
	final String vfoutputDir;
	final String vcoutputDir;
	final String vftraceDir;
	final String vctraceDir;
	final String vafoutputDir;
	final String vaftraceDir;
	final String vsfoutputDir;
	final String vsftraceDir;
	
	final String scriptDir;
	
	String compileSubject;
	String compileVersion;
	String compileFGInstrument;
	String compileCGInstrument;
	
    final static Map<Integer, Fault> faults = new HashMap<Integer, Fault>();
	
	public SienaGenSirScriptClient(String sub, String srcName, String ver, String subVer){
		subject = sub;
		sourceName = srcName;
		version = ver;
		subVersion = subVer;
		
		inputScript = rootDir + subject + "/scripts/" + subject + ".sh";
		inputCompScript = rootDir + subject + "/scripts/" + subject + "Comp.sh";
		
		inputsMapFile = rootDir + subject + "/testplans.alt/" + "inputs.map";
		inputsCompMapFile = rootDir + subject + "/testplans.alt/" + "inputsComp.map";

		
		ssourceDir = rootDir + subject + "/versions.alt/component/orig/" + version + "/";
		
		sexecuteDir = rootDir + subject + "/source/" + version + "/";
		soutputDir = rootDir + subject + "/outputs.alt/" + version + "/" + subject + "/";
		
		vsourceDir = rootDir + subject + "/versions.alt/component/seeded/" + version + "/";
		
		vexecuteDir = rootDir + subject + "/versions/" + version + "/" + subVersion + "/";
		voutputDir = rootDir + subject + "/outputs.alt/" + version + "/versions/" + subVersion + "/outputs/";
		vfoutputDir = rootDir + subject + "/outputs.alt/" + version + "/versions/" + subVersion + "/fine-grained/";
		vsfoutputDir = rootDir + subject + "/outputs.alt/" + version + "/versions/" + subVersion + "/fine-grained-sampled-";
		vafoutputDir = rootDir + subject + "/outputs.alt/" + version + "/versions/" + subVersion + "/fine-grained-adaptive/";
		vcoutputDir = rootDir + subject + "/outputs.alt/" + version + "/versions/" + subVersion + "/coarse-grained/";
		vftraceDir = rootDir + subject + "/traces/" + version + "/" + subVersion + "/fine-grained/";
		vsftraceDir = rootDir + subject + "/traces/" + version + "/" + subVersion + "/fine-grained-sampled-";
		vaftraceDir = rootDir + subject + "/traces/" + version + "/" + subVersion + "/fine-grained-adaptive/";
		vctraceDir = rootDir + subject + "/traces/" + version + "/" + subVersion + "/coarse-grained/";
		
		scriptDir = rootDir + subject + "/scripts/";
		
		initialCompileCommand();
		vcfoutputDir = rootDir + subject + "/outputs.alt/" + version + "/versions/" + subVersion + "/coarse-fine-grained/";
		vboostoutputDir = rootDir + subject + "/outputs.alt/" + version + "/versions/" + subVersion + "/boost/";
		vpruneminusboostoutputDir = rootDir + subject + "/outputs.alt/" + version + "/versions/" + subVersion + "/prune-minus-boost/";
		vpruneoutputDir = rootDir + subject + "/outputs.alt/" + version + "/versions/" + subVersion + "/prune/";

		vcftraceDir = rootDir + subject + "/traces/" + version + "/" + subVersion + "/coarse-fine-grained/";
		vboosttraceDir = rootDir + subject + "/traces/" + version + "/" + subVersion + "/boost/";
		vpruneminusboosttraceDir = rootDir + subject + "/traces/" + version + "/" + subVersion + "/prune-minus-boost/";
		vprunetraceDir = rootDir + subject + "/traces/" + version + "/" + subVersion + "/prune/";

		cgIndicesDir = rootDir + subject + "/versions/" + version + "/" + subVersion + "/predicate-dataset/cg/";
		boostFunctionsDir = rootDir + subject + "/versions/" + version + "/" + subVersion + "/predicate-dataset/boost/";
		pruneMinusBoostFunctionsDir = rootDir + subject + "/versions/" + version + "/" + subVersion + "/predicate-dataset/pruneMinusBoost/";
		pruneFunctionsDir = rootDir + subject + "/versions/" + version + "/" + subVersion + "/predicate-dataset/prune/";

	}
	String vcfoutputDir;
	String vboostoutputDir;
	String vpruneminusboostoutputDir;
	String vpruneoutputDir;
	String vcftraceDir;
	String vboosttraceDir;
	String vpruneminusboosttraceDir;
	String vprunetraceDir;
	String cgIndicesDir;
	String boostFunctionsDir;
	String pruneMinusBoostFunctionsDir;
	String pruneFunctionsDir;
	;
	public static final String mode = "2_0.05";
	private static final String BOOST_FUNCTIONS = "boost_functions_" + mode + ".txt";
	private static final String PRUNE_MINUS_BOOST_FUNCTIONS = "prune_minus_boost_functions_" + mode + ".txt";
	private static final String PRUNE_FUNCTIONS = "prune_functions_" + mode + ".txt";


	private void initialCompileCommand() {
//		//read faults (subversion)
		readFaults();
		System.out.println(faults.toString());
	}
	
	private String genCompileCommand(String executeDir, int index){
		String setEnv = "export experiment_root=" + rootDir + "\n";
		String rmdir = "rm -rf " + executeDir + "*\n";
		String mkdir = "mkdir -p " + executeDir + "siena/\n";
		String siena_app = "cp -r " + rootDir + subject + "/versions.alt/application/*" + " " + executeDir + "siena/\n";
		String siena_subject = "cp -r " + vsourceDir + "* " + executeDir + "siena/\n";
		String siena_testdriver = "cp " + rootDir + subject + "/testdrivers/*.java " + executeDir + "siena/\n";
		String seedNoFaults = seedFaultsCommand(executeDir, index);
		String compileCd = "cd " + executeDir + "\n";
		String compileCC = "find . -name \"*.java\" | javac @/dev/stdin/\n";
		String set_classpath = "unset CLASSPATH\nexport CLASSPATH=" + executeDir + "\n";
		
		return setEnv + rmdir + mkdir + siena_app + siena_subject + siena_testdriver + seedNoFaults + compileCd + compileCC + set_classpath;
	}
	
	private String seedFaultsCommand(String executeDir, int index) {
		StringBuilder builder = new StringBuilder();
		builder.append("cd " + executeDir + "siena/\n");
		for(int i: faults.keySet()){
			String fault_file = faults.get(i).getFault_file();
			builder.append(seeder).append(fault_file).append(" ").append(0).append(" ").append(fault_file.replaceAll("cpp", "java")).append("\n");
		}
		if(index != 0){
			Fault fault = faults.get(index);
			builder.append(seeder).append(fault.getFault_file()).append(" ").append(fault.getFault_order()).append(" ").append(fault.getFault_file().replaceAll("cpp", "java")).append("\n");
		}
		builder.append("echo seeding faults done!\n");
		
		return builder.toString();
	}



	public static void main(String[] args) throws IOException {
		String[][] subjects = {
				{"siena", null, "5"}, // grep v1_subv14
		};
		for (int i = 0; i < subjects.length; i++) {
			for(int j = 1; j <= Integer.parseInt(subjects[i][2]); j++){
				SienaGenSirScriptClient gc = new SienaGenSirScriptClient(subjects[i][0], subjects[i][1], "v" + j, null);
				gc.gen();
				faults.clear();
			}
		}
		
	}



	private void gen() throws IOException {
		AbstractGenRunScript gs;
		AbstractGenRunAllScript ga;
		String sf = rootDir + subject + "/testplans.alt/universe";
		
		//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
		
		//generate test scripts
		String[] argvs = {"-sf", sf, "-sn", inputScript, "-en", AbstractGenRunScript.EXE, "-ed", rootDir + subject, "-tg", "bsh", "-nesc", "-j"};
		MakeTestScript.main(argvs);
		FileUtility.constructSIRInputsMapFile(inputScript, inputsMapFile);
		
		String[] argvsC = {"-sf", sf, "-sn", inputCompScript, "-en", AbstractGenRunScript.EXE, "-ed", rootDir + subject, "-c", soutputDir, "-tg", "bsh", "-nesc", "-j"};
		MakeTestScript.main(argvsC);
		FileUtility.constructSIRInputsMapFile(inputCompScript, inputsCompMapFile);//read inputsMap
		
		//generate run subject and subversion scripts
		gs = new GenRunSubjectScript(subject, sourceName, version, genCompileCommand(sexecuteDir, 0), ssourceDir, sexecuteDir, soutputDir, scriptDir);
		gs.genRunScript();
		
		for(int index: faults.keySet()){
			SienaGenSirScriptClient gc = new SienaGenSirScriptClient(subject, sourceName, version, "subv" + index);
			
			System.out.println("generating run script for subVersion" + index);
			String vexecuteDir_version = gc.vexecuteDir + "version/";
			new GenRunVersionsScript(gc.subject, gc.sourceName, gc.version, gc.subVersion, gc.genCompileCommand(vexecuteDir_version, index), gc.vsourceDir, vexecuteDir_version, gc.voutputDir, gc.scriptDir).genRunScript();
		}
		
		//generate run all scripts  
		assert(FileUtility.readInputsMap(inputsMapFile).size() == FileUtility.readInputsMap(inputsCompMapFile).size());
		ga = new GenRunAllScript(version, subject, scriptDir, faults.size());
		ga.genRunAllScript();
		
		
		//=========================================================================================================================================================================//
		
		Set<Integer> subs = new HashSet<Integer>();
		//split inputs and generate run instrumented subversion scripts 
		for(int index: faults.keySet()){
			SienaGenSirScriptClient gc = new SienaGenSirScriptClient(subject, sourceName, version, "subv" + index);
			
			SirSplitInputs split = new SirSplitInputs(gc.inputsMapFile, gc.vexecuteDir, outCompFile);
			split.split();
			//collect the triggered faults
			if(split.getFailingTests().size() > 1 && split.getPassingTests().size() > 0
					&& !(gc.subject.equals("siena") && gc.version.equals("v5") && gc.subVersion.equals("subv2"))
			){}
			if(true){
				subs.add(index);
				
				System.out.println("generating run instrument script for subv" + index);
				
				String vexecuteDir_fg = gc.vexecuteDir + "fine-grained/";
				gs = new GenRunFineGrainedInstrumentScript(gc.subject, gc.sourceName, gc.version, gc.subVersion, gc.genInstrumentCommand(vexecuteDir_fg, index, "fg"), gc.vsourceDir, vexecuteDir_fg, 
						gc.vfoutputDir, gc.scriptDir, gc.vftraceDir, gc.vexecuteDir + "failingInputs.array", gc.vexecuteDir + "passingInputs.array");
				gs.genRunScript();
				
				String vexecuteDir_cg = gc.vexecuteDir + "coarse-grained/";
				gs = new GenRunCoarseGrainedInstrumentScript(gc.subject,gc.sourceName, gc.version, gc.subVersion, gc.genInstrumentCommand(vexecuteDir_cg, index, "cg"), gc.vsourceDir, vexecuteDir_cg, 
						gc.vcoutputDir, gc.scriptDir, gc.vctraceDir, gc.vexecuteDir + "failingInputs.array", gc.vexecuteDir + "passingInputs.array");
				gs.genRunScript();
				
//
//				String vexecuteDir_s1 = gc.vexecuteDir + "sample_1/";
//				gs = new GenRunSampledFineGrainedInstrumentScript(gc.subject, gc.sourceName, gc.version, gc.subVersion, gc.genInstrumentCommand(vexecuteDir_s1, index, "sample"), gc.vsourceDir, vexecuteDir_s1, gc.vsfoutputDir,
//						gc.scriptDir, gc.vsftraceDir, gc.vexecuteDir + "failingInputs.array", gc.vexecuteDir + "passingInputs.array", 1);
//				gs.genRunScript();
//				String vexecuteDir_s100 = gc.vexecuteDir + "sample_100/";
//				gs = new GenRunSampledFineGrainedInstrumentScript(gc.subject, gc.sourceName, gc.version, gc.subVersion, gc.genInstrumentCommand(vexecuteDir_s100, index, "sample"), gc.vsourceDir, vexecuteDir_s100, gc.vsfoutputDir,
//						gc.scriptDir, gc.vsftraceDir, gc.vexecuteDir + "failingInputs.array", gc.vexecuteDir + "passingInputs.array", 100);
//				gs.genRunScript();
//				String vexecuteDir_s10000 = gc.vexecuteDir + "sample_10000/";
//				gs = new GenRunSampledFineGrainedInstrumentScript(gc.subject, gc.sourceName, gc.version, gc.subVersion, gc.genInstrumentCommand(vexecuteDir_s10000, index, "sample"), gc.vsourceDir, vexecuteDir_s10000, gc.vsfoutputDir,
//						gc.scriptDir, gc.vsftraceDir, gc.vexecuteDir + "failingInputs.array", gc.vexecuteDir + "passingInputs.array", 10000);
//				gs.genRunScript();
//
//
//				String vexecuteDir_adaptive = gc.vexecuteDir + "adaptive/";
//				gs = new GenRunAdaptiveFineGrainedInstrumentScript(gc.subject, gc.sourceName, gc.version, gc.subVersion, gc.genCompileCommand(vexecuteDir_adaptive, index), gc.vsourceDir, vexecuteDir_adaptive,
//						gc.vafoutputDir, gc.scriptDir, gc.vaftraceDir, gc.vexecuteDir + "failingInputs.array", gc.vexecuteDir + "passingInputs.array", gc.version + "_" + gc.subVersion + "_C_LESS_FIRST_1_average");
//				gs.genRunScript();
//				gs = new zuo.processor.genscript.sir.twopass.GenRunCoarseFineGrainedInstrumentScript(gc.subject, gc.version, gc.subVersion, setEnv + export + gc.compileCFGInstrument, gc.vsourceDir, gc.vexecuteDir,
//						gc.vcfoutputDir, gc.scriptDir, gc.vcftraceDir, gc.vexecuteDir + "failingInputs.array", gc.vexecuteDir + "passingInputs.array");
//				gs.genRunScript();
				String vexecuteDir_boost = gc.vexecuteDir + "boost/";
				gs = new zuo.processor.genscript.sir.twopass.java.GenRunBoostFineGrainedInstrumentScript(gc.subject, gc.version, gc.subVersion, gc.genInstrumentCommand(vexecuteDir_boost, index, "boost"), gc.vsourceDir, gc.vexecuteDir,
						gc.vboostoutputDir, gc.scriptDir, gc.vboosttraceDir, gc.vexecuteDir + "failingInputs.array", gc.vexecuteDir + "passingInputs.array", gc.sourceName, new File(gc.boostFunctionsDir, BOOST_FUNCTIONS));
				gs.genRunScript();

				String vexecuteDir_pruneminusboost = gc.vexecuteDir + "prune-minus-boost/";
				gs = new zuo.processor.genscript.sir.twopass.java.GenRunPruneMinusBoostFineGrainedInstrumentScript(gc.subject, gc.version, gc.subVersion, gc.genInstrumentCommand(vexecuteDir_pruneminusboost, index, "boost"), gc.vsourceDir, gc.vexecuteDir,
						gc.vpruneminusboostoutputDir, gc.scriptDir, gc.vpruneminusboosttraceDir, gc.vexecuteDir + "failingInputs.array", gc.vexecuteDir + "passingInputs.array", gc.sourceName, new File(gc.pruneMinusBoostFunctionsDir, PRUNE_MINUS_BOOST_FUNCTIONS));
				gs.genRunScript();

				String vexecuteDir_prune = gc.vexecuteDir + "prune/";
				gs = new zuo.processor.genscript.sir.twopass.java.GenRunPruneFineGrainedInstrumentScript(gc.subject, gc.version, gc.subVersion, gc.genInstrumentCommand(vexecuteDir_prune, index, "boost"), gc.vsourceDir, gc.vexecuteDir,
						gc.vpruneoutputDir, gc.scriptDir, gc.vprunetraceDir, gc.vexecuteDir + "failingInputs.array", gc.vexecuteDir + "passingInputs.array", gc.sourceName, new File(gc.pruneFunctionsDir, PRUNE_FUNCTIONS));
				gs.genRunScript();
			}
			
		}
		
		//generate run all instrumented triggered subversion scripts
		ga = new GenRunAllInstrumentedScript(version, subject, scriptDir, subs);
		ga.genRunAllScript();

//		//generate run all sampled instrumented triggered subversion scripts
//		ga = new GenRunAllSampledInstrumentedScript(version, subject, scriptDir, subs, 1);
//		ga.genRunAllScript();
//		ga = new GenRunAllSampledInstrumentedScript(version, subject, scriptDir, subs, 100);
//		ga.genRunAllScript();
//		ga = new GenRunAllSampledInstrumentedScript(version, subject, scriptDir, subs, 10000);
//		ga.genRunAllScript();
//
//		//generate run all adaptive instrumented triggered subversion scripts
//		ga = new GenRunAllAdaptiveInstrumentedScript(version, subject, scriptDir, subs);
//		ga.genRunAllScript();
	}
	
	
	private String genInstrumentCommand(String executeDir, int index, String string) {
		String paras = null;
		
		if(string.equals("fg")){
			paras = " -sampler-scheme=branches -sampler-scheme=returns -sampler-scheme=scalar-pairs"
					+ " -sampler-out-sites=" + executeDir + "output.sites"
					+ " -cp " + executeDir 
					+ " -process-dir " + executeDir 
					+ " -d " + executeDir + "instrumented/"
					+ "\n";
		}
		else if(string.equals("cg")){
			paras = " -sampler-scheme=method-entries"
					+ " -sampler-out-sites=" + executeDir + "output.sites"
					+ " -cp " + executeDir 
					+ " -process-dir " + executeDir 
					+ " -d " + executeDir + "instrumented/"
					+ "\n";
		}
		else if(string.equals("boost")){
			paras = " -sampler-scheme=branches -sampler-scheme=returns -sampler-scheme=scalar-pairs"
					+ " ${FUNCTION_FILTERING}"
					+ " -sampler-out-sites=" + executeDir + "output.sites"
					+ " -cp " + executeDir 
					+ " -process-dir " + executeDir 
					+ " -d " + executeDir + "instrumented/"
					+ "\n";
		}
		else{
			System.err.println("Wrong para!");
		}
		
		String compileCommand = genCompileCommand(executeDir, index);
		String samplerCommand = "java -ea -cp " + jsampler + " edu.uci.jsampler.client.JSampler" + paras;
		String set_classpath = "unset CLASSPATH\nexport CLASSPATH=" + executeDir + "instrumented/:" + jsampler + "\n";
		
		return compileCommand + samplerCommand + set_classpath;
	}



	private void readFaults(){
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(new File(vsourceDir, "FaultSeeds.h")));
			String line;
			int index = 0;
			while((line = reader.readLine()) != null){
				assert(line.split(" ").length == 3 && line.split(" ")[0].startsWith("F"));
				faults.put(++index, new Fault(line.split(" ")[1], line.split(" ")[2]));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			if(reader != null){
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	
	static class Fault{
		final String fault_order;
		final String fault_file;
		
		public Fault(String order, String file){
			this.fault_order = order;
			this.fault_file = file;
		}

		public String getFault_order() {
			return fault_order;
		}

		public String getFault_file() {
			return fault_file;
		}
		
		public String toString(){
			return this.fault_order + "\t" + this.fault_file;
		}
	}

}
