package edu.illinois.cs.cogcomp.edison.features.lrec.srl.Nom.Classifier;

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.View;
import edu.illinois.cs.cogcomp.core.io.IOUtils;
import edu.illinois.cs.cogcomp.edison.features.FeatureExtractor;
import edu.illinois.cs.cogcomp.edison.features.lrec.FeatureGenerators;
import edu.illinois.cs.cogcomp.edison.features.lrec.ProjectedPath;
import edu.illinois.cs.cogcomp.edison.features.lrec.srl.Nom.Classifier.WordContext;
import edu.illinois.cs.cogcomp.edison.features.manifest.FeatureManifest;
import edu.illinois.cs.cogcomp.edison.features.Feature;
import edu.illinois.cs.cogcomp.edison.utilities.EdisonException;
import edu.illinois.cs.cogcomp.core.utilities.DummyTextAnnotationGenerator;
import junit.framework.TestCase;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TestWordContext extends TestCase {

	private static List<TextAnnotation> tas;

	static {
		try {
			tas = IOUtils.readObjectAsResource(WordContext.class, "test.ta");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	public final void test() throws Exception {

		System.out.println("WordContext Feature Extractor");
		// Using the first TA and a constituent between span of 30-40 as a test
//		TextAnnotation ta = tas.get(2);
//		int i = 0;
//		for (TextAnnotation Ta:tas) {
//			System.out.println("The current ta has index: " + i);
//			System.out.println(Ta.getText());
//			System.out.println();
//			i++;
//		}
//		View TOKENS = ta.getView("TOKENS");
//
//		System.out.println("GOT TOKENS FROM TEXTAnn");
//
//		List<Constituent> testlist = TOKENS.getConstituentsCoveringSpan(0, 20);
//
//		for (Constituent c : testlist) {
//			System.out.println(c.getSurfaceForm());
//		}
//
//		System.out.println("Testlist size is " + testlist.size());
		String[] viewsToAdd = {};
		TextAnnotation ta = DummyTextAnnotationGenerator.generateAnnotatedTextAnnotation(viewsToAdd,false);
		int i = 0;

		System.out.println("This textannoation annotates the text: " + ta.getText());

		View TOKENS = ta.getView("TOKENS");

		System.out.println("GOT TOKENS FROM TEXTAnn");

		List<Constituent> testlist = TOKENS.getConstituentsCoveringSpan(0, 20);

		for (Constituent c : testlist) {
			System.out.println(c.getSurfaceForm());
		}

		System.out.println("SRL output");
		int SRLFexCount = 0;
		
		Writer writer = null;
		FileOutputStream fexfile = new FileOutputStream("testinput.fex");
//		String fexcontent = "(define word-context\n"
//				+ "\t(context :size 2\n" + "\t:include-index true\n"
//				+ "\t:ignore-center true\n"
//				+ "word))";
		
		String fexcontent = "(define word-context (context :size 2 :include-index true :ignore-center true word))";
		
		try {
			writer = new BufferedWriter(new OutputStreamWriter(fexfile, "utf-8"));
			writer.write(fexcontent);
		}catch(IOException ex){
		}finally{
			try{
				writer.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		FeatureManifest featureManifest;
		FeatureExtractor fex;
		
		featureManifest = new FeatureManifest(new FileInputStream("testinput.fex"));
		FeatureManifest.setFeatureExtractor("hyphen-argument-feature", FeatureGenerators.hyphenTagFeature);
		FeatureManifest.setTransformer("parse-left-sibling", FeatureGenerators.getParseLeftSibling(ViewNames.PARSE_STANFORD));
		FeatureManifest.setTransformer("parse-right-sibling", FeatureGenerators.getParseRightSibling(ViewNames.PARSE_STANFORD));
		FeatureManifest.setFeatureExtractor("pp-features", FeatureGenerators.ppFeatures(ViewNames.PARSE_STANFORD));

		FeatureManifest.setFeatureExtractor("projected-path", new ProjectedPath(ViewNames.PARSE_STANFORD));
		
		featureManifest.useCompressedName();
		featureManifest.setVariable("*default-parser*", ViewNames.PARSE_STANFORD);
		
		fex = featureManifest.createFex();
		
		
		ArrayList<Set<Feature>> featslist = new ArrayList<Set<Feature>>();

		for (Constituent test : testlist)
			featslist.add(fex.getFeatures(test));

		if (featslist.isEmpty()) {
			System.out.println("Feats list is returning NULL.");
		}

		System.out.println("Printing list of Feature set");

		for (Set<Feature> feats : featslist) {
			for (Feature f : feats){
				System.out.println(f.getName());
				SRLFexCount += f.getName().split("/n").length;
			}
			System.out.println();
		}

		System.out.println("GOT FEATURES YES!");
		
		System.out.println("-------------------------------------------------");
		
		System.out.println("Edison output");
		int EdisonFexCount = 0;
		WordContext wc = new WordContext(); 
	
		featslist.clear();

		for (Constituent test : testlist)
			featslist.add(wc.getFeatures(test));

		if (featslist.isEmpty()) {
			System.out.println("Feats list is returning NULL.");
		}

		System.out.println("Printing list of Feature set");

		for (Set<Feature> feats : featslist) {
			for (Feature f : feats){
				System.out.println(f.getName());
				EdisonFexCount += f.getName().split("/n").length;
			}
		}

		System.out.println("GOT FEATURES YES!");
		
		assertEquals(SRLFexCount,EdisonFexCount);
	}

	private void testFex(FeatureExtractor fex, boolean printBoth, String... viewNames) throws EdisonException {

		for (TextAnnotation ta : tas) {
			for (String viewName : viewNames)
				if (ta.hasView(viewName))
					System.out.println(ta.getView(viewName));
		}
	}
}
