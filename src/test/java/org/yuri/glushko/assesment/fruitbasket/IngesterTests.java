package org.yuri.glushko.assesment.fruitbasket;

import org.junit.jupiter.api.Test;
import org.yuri.glushko.assesment.fruitbasket.dao.FruitBasketDAO;
import org.yuri.glushko.assesment.fruitbasket.exceptions.WrongDataFormatException;
import org.yuri.glushko.assesment.fruitbasket.service.FruitBasketDataIngester;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class IngesterTests {

    @Test
    public void ingesterNonExistantFileTest() {
        FruitBasketDAO mockDB = mock(FruitBasketDAO.class);
        FruitBasketDataIngester ingester = new FruitBasketDataIngester(mockDB);
        Exception e = assertThrows(WrongDataFormatException.class, () -> {
            ingester.ingestCSV("test_set22.csv");
        });
    }
}
