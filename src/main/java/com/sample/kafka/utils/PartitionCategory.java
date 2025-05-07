package com.sample.kafka.utils;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class PartitionCategory {
    private static final Map<String, Integer> CATEGORY_PARTITION_MAP = Map.of(
            Categories.ELECTRONICS, 0,
            Categories.CLOTHING, 1,
            Categories.FURNITURE, 3,
            Categories.FOOD, 4,
            Categories.BOOKS, 5
    );

    private static final List<Integer> TRADING = List.of(2, 6);

    public static int getPartitionForCategory(String category) {
        if(Categories.TRADING.equals(category)){
            return TRADING.get(new Random().nextInt(TRADING.size()));
        }
        return CATEGORY_PARTITION_MAP.getOrDefault(category, 0);
    }
}
