package zuo.processor.genscript.sir.twopass.java;

public abstract class AbstractGenRunAllScript {
	final String version;
	final String subject;
	final String scriptDir;
	
	public AbstractGenRunAllScript(String version, String subject, String scriptDir) {
		super();
		this.version = version;
		this.subject = subject;
		this.scriptDir = scriptDir;
	}
	
	public abstract void genRunAllScript();
	

}
