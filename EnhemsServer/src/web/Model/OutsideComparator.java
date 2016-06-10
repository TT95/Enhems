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
public class OutsideComparator extends Comparator {

    /**
     * Constructor
     *
     * @param min min value of an interval
     * @param max max value of an interval
     */
    public OutsideComparator(int min, int max) {
        super(min, max);
    }

    /**
     * @param i value to compare
     * @return true if value i is inside interval
     */
    @Override
    public boolean Compare(int i) {
        return i >= min || i <= max;
    }
}
