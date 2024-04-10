package Framework.Support;

import javafx.util.Pair;

import java.util.ArrayList;

public class PairArray <T, D> {
    private ArrayList<Pair<T,D>> pairs;

    public PairArray() {
        pairs = new ArrayList<>();
    }

    public void add(T first, D second) {
        pairs.add(new Pair<>(first, second));
    }

    public Pair<T,D> get(int index) {
        return pairs.get(index);
    }

    public T getFirst(int index) {
        return pairs.get(index).getKey();
    }

    public D getSecond(int index) {
        return pairs.get(index).getValue();
    }

    public int size() {
        return pairs.size();
    }

    public void clear() {
        pairs.clear();
    }

    public void remove(int index) {
        pairs.remove(index);
    }

    public void remove(T first, D second) {
        pairs.remove(new Pair<>(first, second));
    }

    public boolean contains(T first, D second) {
        return pairs.contains(new Pair<>(first, second));
    }

    public boolean contains(Pair<T,D> pair) {
        return pairs.contains(pair);
    }

}
