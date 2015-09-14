package uk.ac.glasgow.jagora.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 *  A class for randomising elements of collection
 */
public class CollectionsRandom {

	public static <T> T chooseElement (Collection<T> elements, Random random){
		if (elements.size() < 1) return null;
		List<T> asList = new ArrayList<T>(elements);
		return asList.get(random.nextInt(asList.size()));
	}

}
