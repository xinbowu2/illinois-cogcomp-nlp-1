/**
 * This software is released under the University of Illinois/Research and
 *  Academic Use License. See the LICENSE file in the root folder for details.
 * Copyright (c) 2016
 *
 * Developed by:
 * The Cognitive Computation Group
 * University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.edison.features.lrec.srl.Nom.Classifier;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.edison.features.ContextFeatureExtractor;
import edu.illinois.cs.cogcomp.edison.features.Feature;
import edu.illinois.cs.cogcomp.edison.features.FeatureCollection;
import edu.illinois.cs.cogcomp.edison.features.FeatureExtractor;
import edu.illinois.cs.cogcomp.edison.features.factory.WordFeatureExtractorFactory;
import edu.illinois.cs.cogcomp.edison.utilities.EdisonException;

import java.util.Set;


/**
 *
 * @keywords  Ssemantic role labeling, srl, nominal, nom, classifier, pos, context
 * @author Xinbo Wu
 */
public class POSContext implements FeatureExtractor {
	private final FeatureCollection base;
	private final String name;

	public POSContext(){
		this("#posContext#");
	}

	public POSContext(String name){
		//ContextFeatureExtractor context = new ContextFeatureExtractor(2, true, true);
		this.name = name;
		this.base = new FeatureCollection(this.getName());

		ContextFeatureExtractor context = new ContextFeatureExtractor(2, true, true);
		context.addFeatureExtractor(WordFeatureExtractorFactory.pos);
		base.addFeatureExtractor(context);
	}

	@Override
	public Set<Feature> getFeatures(Constituent c) throws EdisonException {
		return base.getFeatures(c);
	}

	@Override
	public String getName() {
		return this.name;
	}
}