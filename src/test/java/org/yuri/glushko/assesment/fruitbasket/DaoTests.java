package org.yuri.glushko.assesment.fruitbasket;

import org.junit.jupiter.api.Test;
import org.yuri.glushko.assesment.fruitbasket.dao.FruitBasketDAO;
import org.yuri.glushko.assesment.fruitbasket.exceptions.DatabaseException;
import org.yuri.glushko.assesment.fruitbasket.exceptions.WrongDataFormatException;
import org.yuri.glushko.assesment.fruitbasket.service.FruitBasketDataIngester;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;


public class DaoTests {

    @Test
    public void daoIngestCorrectRecordTest() throws DatabaseException, WrongDataFormatException{
        FruitBasketDAO db = new FruitBasketDAO();
        String[] l = {"apple", "6", "red", "sweet"};
        db.ingestRecord(Arrays.asList(l));
        assertEquals(1, db.fruitsTotalCount());
    }

    @Test
    public void daoIngestCorrectRecord2Test() throws DatabaseException, WrongDataFormatException{
        FruitBasketDAO db = new FruitBasketDAO();
        String[] l = {"apple", "6", "red", "sweet", "delicious"};
        db.ingestRecord(Arrays.asList(l));
        assertEquals(1, db.fruitsTotalCount());
    }

    @Test
    public void daoIngestIncorrectRecordTest() throws DatabaseException, WrongDataFormatException{
        FruitBasketDAO db = new FruitBasketDAO();
        String[] l = {"apple", "6", "red"};
        Exception e = assertThrows(WrongDataFormatException.class, () -> {
            db.ingestRecord(Arrays.asList(l));
        });
    }

    @Test
    public void daoIngestIncorrectRecord2Test() throws DatabaseException, WrongDataFormatException{
        FruitBasketDAO db = new FruitBasketDAO();
        String[] l = {"", "6", "red"};
        Exception e = assertThrows(WrongDataFormatException.class, () -> {
            db.ingestRecord(Arrays.asList(l));
        });
    }

    @Test
    public void daoIngestIncorrectRecord3Test() throws DatabaseException, WrongDataFormatException{
        FruitBasketDAO db = new FruitBasketDAO();
        String[] l = {"apple", "hgjkdhf", "red", "sweet"};
        Exception e = assertThrows(WrongDataFormatException.class, () -> {
            db.ingestRecord(Arrays.asList(l));
        });
    }

    @Test
    public void daoIngestIncorrectRecord4Test() throws DatabaseException, WrongDataFormatException{
        FruitBasketDAO db = new FruitBasketDAO();
        String[] l = {"apple", "6", "red", ""};
        Exception e = assertThrows(WrongDataFormatException.class, () -> {
            db.ingestRecord(Arrays.asList(l));
        });
    }

    @Test
    public void daoTotalCountTest() throws DatabaseException, WrongDataFormatException{
        FruitBasketDAO db = new FruitBasketDAO();
        String[] l1 = {"apple", "6", "red", "sweet"};
        String[] l2 = {"apple", "5", "green", "tart"};
        db.ingestRecord(Arrays.asList(l1));
        db.ingestRecord(Arrays.asList(l2));
        assertEquals(2, db.fruitsTotalCount());
    }

    @Test
    public void daoTotalFruitCountTest() throws DatabaseException, WrongDataFormatException{
        FruitBasketDAO db = new FruitBasketDAO();
        String[] l1 = {"apple", "6", "red", "sweet"};
        String[] l2 = {"apple", "5", "green", "tart"};
        String[] l3 = {"pear", "1", "yellow", "juicy"};
        db.ingestRecord(Arrays.asList(l1));
        db.ingestRecord(Arrays.asList(l2));
        db.ingestRecord(Arrays.asList(l3));
        assertEquals(2, db.fruitTypesTotalCount());
    }

    @Test
    public void oldestFruitsAndAgeOneTest() throws DatabaseException, WrongDataFormatException{
        FruitBasketDAO db = new FruitBasketDAO();
        String[] l1 = {"apple", "6", "red", "sweet"};
        String[] l2 = {"apple", "5", "green", "tart"};
        String[] l3 = {"pear", "1", "yellow", "juicy"};
        db.ingestRecord(Arrays.asList(l1));
        db.ingestRecord(Arrays.asList(l2));
        db.ingestRecord(Arrays.asList(l3));
        Map<String,Integer> r = db.oldestFruitsAndAge();
        assertEquals(1, r.size());
        assertTrue(r.containsKey("apple"));
        assertEquals(Integer.valueOf(6), r.get("apple"));
    }

    @Test
    public void oldestFruitsAndAgeTwoTest() throws DatabaseException, WrongDataFormatException{
        FruitBasketDAO db = new FruitBasketDAO();
        String[] l1 = {"apple", "6", "red", "sweet"};
        String[] l2 = {"apple", "5", "green", "tart"};
        String[] l3 = {"pear", "6", "yellow", "juicy"};
        db.ingestRecord(Arrays.asList(l1));
        db.ingestRecord(Arrays.asList(l2));
        db.ingestRecord(Arrays.asList(l3));
        Map<String,Integer> r = db.oldestFruitsAndAge();
        assertEquals(2, r.size());
        assertTrue(r.containsKey("apple"));
        assertEquals(Integer.valueOf(6), r.get("apple"));
        assertTrue(r.containsKey("pear"));
        assertEquals(Integer.valueOf(6), r.get("pear"));
    }
}
