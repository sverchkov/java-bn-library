/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.isp.sverchkov.collections;

/**
 *
 * @author YUS24
 */
public class Nothing implements Tuple<Nothing,Nothing> {
    public static final Nothing nothing = null; // = new Nothing(); // SHOULD this be null or an object? 
    private Nothing(){}
    @Override
    public Nothing getFirst() {
        return nothing;
    }
    @Override
    public Nothing getRest() {
        return nothing;
    }
}
