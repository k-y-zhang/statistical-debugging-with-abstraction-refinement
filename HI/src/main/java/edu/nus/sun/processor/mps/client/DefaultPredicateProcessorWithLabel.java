package edu.nus.sun.processor.mps.client;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import sun.processor.core.IDataSetConstructor;
import sun.processor.core.IDataSetProcessor;
import sun.processor.core.IProfileProcessor;
import sun.processor.core.IProfileReader;
import sun.processor.core.Processor;
import sun.processor.core.Processor.BackEnd;
import sun.processor.predicate.constructor.PredicateDataSetConstructor;
import sun.processor.predicate.processor.PredicateDataSetMappingOutputter;
import sun.processor.predicate.processor.PredicateDataSetProtoBufOutputter;
import sun.processor.profile.LabelPrinterProfileProcessor;
import edu.nus.sun.processor.predicate.PredicateProfileReaderWithLabel;

public class DefaultPredicateProcessorWithLabel extends
		AbstractProcessorWithLabels {

	public static final String MPS_PB = "mps-ds.pb";

	protected final File sitesInfoPath;
	
	public DefaultPredicateProcessorWithLabel(File profileFolder, File resultOutputFolder, File sitesInfoPath) {
		super(profileFolder, resultOutputFolder);
		this.sitesInfoPath = sitesInfoPath;
	}

	
	@Override
	protected List<BackEnd> createBackends(final File resultOutputFolder) {
		final ArrayList<BackEnd> list = new ArrayList<BackEnd>();

		// final File intraResultFolder = new File(resultOutputFolder, "intra");
		final File intraResultFolder = resultOutputFolder;
		if (!intraResultFolder.exists()) {
			intraResultFolder.mkdirs();
		}
		list.add(this.createIntraBackend(intraResultFolder));

		return list;
	}

	private BackEnd createIntraBackend(File resultOutputFolder) {
		String folder = resultOutputFolder.getAbsolutePath();
		IDataSetConstructor constructor = new PredicateDataSetConstructor();

		ArrayList<IDataSetProcessor> processors = new ArrayList<IDataSetProcessor>();

		processors.add(new PredicateDataSetMappingOutputter(new File(folder,
				"profile.mapping"), new File(folder, "predicate.mapping")));
		processors.add(new PredicateDataSetProtoBufOutputter(new File(folder,
				MPS_PB)));

		return new BackEnd(constructor, processors);
	}

	@Override
	protected List<IProfileProcessor> createProfileProcessors(
			final File resultOutputFolder) {
		final List<IProfileProcessor> pps = new ArrayList<IProfileProcessor>();
		pps.add(new LabelPrinterProfileProcessor(new File(resultOutputFolder,
				"incorrect-profiles.txt")));
		return pps;
	}

	@Override
	protected IProfileReader createProfileReader(final File profileFolder) {
		return new PredicateProfileReaderWithLabel(profileFolder, this.sitesInfoPath);
	}

}
