package uk.ac.gla.jagora.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class RandomChoice {

	public static <T> T chooseRandomElement (Collection<T> elements){
		List<T> asList = new ArrayList<T>(elements);
		Collections.shuffle(asList);
		return asList.get(0);
	}

}
