/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web.Model;

/**
 *
 * @author Stjepan
 */
public class ComparatorFactory {

    /**
     * @param min min value of an interval
     * @param max max value of an interval
     * @return Comparator for given min and max values
     */
    public static Comparator GetComparator(int min, int max) {
        if (min < max) {
            return new InsideComparator(min, max);
        }
        return new OutsideComparator(min, max);
    }

}
