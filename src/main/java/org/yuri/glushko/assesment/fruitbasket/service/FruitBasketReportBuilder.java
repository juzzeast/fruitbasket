package org.yuri.glushko.assesment.fruitbasket.service;

import org.yuri.glushko.assesment.fruitbasket.dao.FruitBasketDAO;
import org.yuri.glushko.assesment.fruitbasket.exceptions.DatabaseException;

import java.util.List;
import java.util.Map;

public class FruitBasketReportBuilder {
    private FruitBasketDAO basket;

    public FruitBasketReportBuilder(FruitBasketDAO basketDao) {
        basket = basketDao;
    }

    // Retrieves data by calling DAO object & formats a report
    public String buildReport() {
        int fruitsTotal;
        int fruitTypesTotal;
        Map<String,Integer> oldestFruits;
        Map<String,Integer> fruitCountByType;
        List<String> fruitsByTraits;
        try {
            fruitsTotal = basket.fruitsTotalCount();
            fruitTypesTotal = basket.fruitTypesTotalCount();
            oldestFruits = basket.oldestFruitsAndAge();
            fruitCountByType = basket.fruitTypesCountDesc();
            fruitsByTraits = basket.fruitTypesCountByTraitsDesc();
        } catch(DatabaseException de) {
            return "Not able to build report\n" + de.getMessage();
        }

        StringBuilder report = new StringBuilder();
        report.append("Total number of fruit:\n")
                .append(fruitsTotal).append('\n').append('\n')
                .append("Total types of fruit:\n")
                .append(fruitTypesTotal).append('\n').append('\n')
                .append("Oldest fruit & age:\n");
        for(Map.Entry<String,Integer> e : oldestFruits.entrySet()) {
            report.append(e.getKey())
                    .append(':').append(' ')
                    .append(e.getValue().toString()).append('\n');
        }
        report.append('\n').append("The number of each type of fruit in descending order:\n");
        for(Map.Entry<String,Integer> e : fruitCountByType.entrySet()) {
            report.append(e.getKey())
                    .append(':').append(' ')
                    .append(e.getValue().toString()).append('\n');
        }
        report.append('\n').append("The various characteristics (count, color, shape, etc.) of each fruit by type:\n");
        for(int i=0; i<fruitsByTraits.size(); i++) {
            report.append(fruitsByTraits.get(i)).append('\n');
        }
        return report.toString();
    }
}
