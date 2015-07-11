package uk.ac.glasgow.jagora.test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ivo on 08/07/15.
 */
public class Test {



    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        list.add(5);
        int one = list.remove(0);
        int two = list.remove(0);

        System.out.println("1 is" + one + "two is " + two); // Display the string.
    }

}
