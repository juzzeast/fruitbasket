package org.yuri.glushko.assesment.fruitbasket.service;

import org.yuri.glushko.assesment.fruitbasket.dao.FruitBasketDAO;
import org.yuri.glushko.assesment.fruitbasket.exceptions.DatabaseException;
import org.yuri.glushko.assesment.fruitbasket.exceptions.WrongDataFormatException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class FruitBasketDataIngester {
    private FruitBasketDAO basket;

    public FruitBasketDataIngester(FruitBasketDAO basketDao) {
        basket = basketDao;
    }

    // Parses the CSV file & feeds it to DAO's ingestion methods
    // Data correctness control is on DAO object
    public void ingestCSV(String fileName) throws DatabaseException, WrongDataFormatException {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                basket.ingestRecord(Arrays.asList(values));
            }
        } catch(IOException ioe) {
            throw new WrongDataFormatException("Can't read input file: " + ioe.getMessage());
        }
    }
}
