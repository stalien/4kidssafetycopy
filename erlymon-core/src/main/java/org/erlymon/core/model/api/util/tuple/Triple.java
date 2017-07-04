package org.erlymon.core.model.api.util.tuple;

/**
 * Created by pese on 7/29/16.
 */
public class Triple<A,B,C,D> {
    public A first;
    public B second;
    public C third;
    public D fourth;

    public Triple(A first, B second, C third, D fourth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
    }
}
