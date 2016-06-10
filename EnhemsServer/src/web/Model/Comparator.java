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
public abstract class Comparator {

    /**
     * Constructor, sets attributes
     *
     * @param min min value of an interval
     * @param max max value of an interval
     */
    public Comparator(int min, int max) {
        this.min = min;
        this.max = max;
    }

    /**
     * @param i value to compare
     * @return true if value i is inside interval
     */
    public abstract boolean Compare(int i);

    protected int min, max;
}
