package co.unruly.control.linklist;

import co.unruly.control.Pair;

import java.util.Iterator;
import java.util.NoSuchElementException;

class LinkListIterator<T> implements Iterator<T> {

    private LinkList<T> currentNode;

    public LinkListIterator(LinkList<T> currentNode) {
        this.currentNode = currentNode;
    }

    @Override
    public boolean hasNext() {
        return currentNode.read((x, xs) -> true, () -> false);
    }

    @Override
    public T next() {
        final Pair<T, LinkList<T>> values = currentNode.read(Pair::new, () -> {
            throw new NoSuchElementException();
        });
        this.currentNode = values.right;
        return values.left;
    }
}
