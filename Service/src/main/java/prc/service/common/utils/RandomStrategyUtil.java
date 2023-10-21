package prc.service.common.utils;

import javafx.util.Pair;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class RandomStrategyUtil<K, V extends Number> {
    private TreeMap<Double, K> weightMap = new TreeMap<>();

    public RandomStrategyUtil(List<Pair<K, V>> list) {
        for (Pair<K, V> pair : list) {
            double lastWeight = this.weightMap.size() == 0 ? 0 : this.weightMap.lastKey();
            this.weightMap.put(pair.getValue().doubleValue() + lastWeight, pair.getKey());
        }
    }

    public K random() {
        double randomWeight = this.weightMap.lastKey() * Math.random();
        SortedMap<Double, K> tailMap = this.weightMap.tailMap(randomWeight, false);
        return this.weightMap.get(tailMap.firstKey());
    }
}
