package me.videogame.recaf.iterator;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Iterator;

public class NodeListIterator implements Iterator<Node> {
    private final NodeList list;

    private int nextItem = 0;

    public NodeListIterator(NodeList list) {
        this.list = list;
    }

    @Override
    public boolean hasNext() {
        return nextItem != list.getLength();
    }

    @Override
    public Node next() {
        Node item = list.item(nextItem);
        nextItem++;
        return item;
    }
}
