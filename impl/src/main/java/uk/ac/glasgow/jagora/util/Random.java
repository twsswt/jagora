package uk.ac.glasgow.jagora.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *  A class for randomising elements of collection
 */
public class Random extends java.util.Random {

	/****/
	private static final long serialVersionUID = -5969712456351224128L;

	public Random(Integer seed) {
		super(seed);
	}

	public <T> T chooseElement (Collection<T> elements){
		if (elements.size() < 1) return null;
		List<T> asList = new ArrayList<T>(elements);
		Collections.shuffle(asList, this);
		return asList.get(0);
	}

}
