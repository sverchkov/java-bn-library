/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.isp.sverchkov.collections;

/**
 *
 * @author YUS24
 */
public class Bucket<Thing> implements Tuple<Thing,Nothing> {
    public final Thing thing;
    public Bucket( Thing thing ){
        this.thing = thing;
    }
    @Override
    public Thing getFirst() {
        return thing;
    }
    @Override
    public Nothing getRest() {
        return Nothing.nothing;
    }
}
