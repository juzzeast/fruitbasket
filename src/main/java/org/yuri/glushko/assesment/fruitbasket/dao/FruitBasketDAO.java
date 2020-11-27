package org.yuri.glushko.assesment.fruitbasket.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yuri.glushko.assesment.fruitbasket.config.DBConnectionFactory;
import org.yuri.glushko.assesment.fruitbasket.exceptions.DatabaseException;
import org.yuri.glushko.assesment.fruitbasket.exceptions.WrongDataFormatException;

// This class encapsulates data operations logic, including record ingestion
public class FruitBasketDAO {
    Connection db;

    public FruitBasketDAO() throws DatabaseException {
        try {
            db = DBConnectionFactory.getConnection();
        } catch (SQLException se) {
            throw new DatabaseException("Can't create data storage: " + se.getMessage());
        }
    }

    // Inserts record into DB, split in two tables
    public void ingestRecord(List<String> r) throws DatabaseException, WrongDataFormatException {
        String insertFruit = "INSERT INTO Basket SET typeId = %d, age = %d, characteristics = '%s'";
        // Fail if records are missing fields
        if(r.size() < 4) {
            throw new WrongDataFormatException("Wrong data format: fields missing");
        }
        String fruitType = r.get(0).trim().toLowerCase();
        // Fail if fruit type is missing
        if(fruitType.equals("")) {
            throw new WrongDataFormatException("Wrong data format: fruit type is missing");
        }
        // Figure out what's fruit type ID
        // if there were no such fruit type, this method would insert it into the table
        // and then return the ID
        int typeId = typeIdByName(r.get(0));
        // Fail if age is wrong
        int age;
        try {
            age = Integer.parseInt(r.get(1));
        } catch(NumberFormatException nfe) {
            throw new WrongDataFormatException("Wrong data format: age is not a number");
        }
        // The characteristics are packed into one field, no matter how many are there
        StringBuilder traits = new StringBuilder();
        for(int i=2; i<r.size(); i++) {
            if(i>2) {
                traits.append(',');
            }
            String trait = r.get(i).toLowerCase().trim();
            // Fail if fruit mandatory characteristics are in wrong format
            if(trait.equals("")) {
                throw new WrongDataFormatException("Wrong data format: characteristics are wrong");
            }
            traits.append(trait);
        }
        insertFruit = String.format(insertFruit, typeId, age, traits);
        try {
            Statement st = db.createStatement();
            int inserted = st.executeUpdate(insertFruit);
            if(inserted < 1) {
                throw new DatabaseException(String.format("Can't insert fruit record: %s, %d, %s",
                        r.get(0), age, traits));
            }
        } catch (SQLException se) {
            throw new DatabaseException("Can't process fruit record: " + se.getMessage());
        }
    }

    // Counts the total number of fruits, regardless of the types
    public int fruitsTotalCount() throws DatabaseException {
        String query = "SELECT COUNT(id) AS cnt FROM Basket";
        int count = -1;
        try {
            Statement st = db.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                count = rs.getInt("cnt");
            }
        } catch (SQLException se) {
            throw new DatabaseException("Can't count fruits: " + se.getMessage());
        }
        return count;
    }

    // Counts number of distinct fruit types
    public int fruitTypesTotalCount() throws DatabaseException {
        String query = "SELECT COUNT(id) AS cnt FROM Types";
        int count = -1;
        try {
            Statement st = db.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                count = rs.getInt("cnt");
            }
        } catch (SQLException se) {
            throw new DatabaseException("Can't count fruit types: " + se.getMessage());
        }
        return count;
    }

    // Retrieves all the fruits with maximum age
    public Map<String,Integer> oldestFruitsAndAge() throws DatabaseException {
        String query = "SELECT Types.type AS type, Basket.age AS age FROM Basket " +
                "JOIN Types ON Basket.typeId = Types.Id " +
                "WHERE Basket.age = (SELECT MAX(bb.age) FROM Basket AS bb)";
        Map<String,Integer> result = new HashMap<String,Integer>();
        try {
            Statement st = db.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                String type = rs.getString("type");
                int age = rs.getInt("age");
                result.put(type, Integer.valueOf(age));
            }
        } catch (SQLException se) {
            throw new DatabaseException("Can't process max fruit ages: " + se.getMessage());
        }
        return result;
    }

    // Counts fruits by types, in descending order
    public Map<String,Integer> fruitTypesCountDesc() throws DatabaseException {
        String query = "SELECT Types.type AS type, COUNT(Basket.id) as cnt FROM Basket " +
                "JOIN Types ON Basket.typeId = Types.Id " +
                "GROUP BY Basket.typeId " +
                "ORDER BY cnt DESC" ;
        Map<String,Integer> result = new HashMap<String,Integer>();
        try {
            Statement st = db.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                String type = rs.getString("type");
                int count = rs.getInt("cnt");
                result.put(type, Integer.valueOf(count));
            }
        } catch (SQLException se) {
            throw new DatabaseException("Can't count fruits by types: " + se.getMessage());
        }
        return result;
    }

    // Yields list of fruits by types & characteristics with count, in descending order
    public List<String> fruitTypesCountByTraitsDesc() throws DatabaseException {
        String query = "SELECT COUNT(Basket.id) as cnt, Types.type AS type, Basket.characteristics as traits FROM Basket " +
                "JOIN Types ON Basket.typeId = Types.Id " +
                "GROUP BY Types.type, Basket.characteristics " +
                "ORDER BY cnt DESC, Basket.typeId ";
        List<String> result = new ArrayList<String>();
        try {
            Statement st = db.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                String type = rs.getString("type");
                int count = rs.getInt("cnt");
                String traits = rs.getString("traits");
                StringBuilder s = new StringBuilder();
                String toAdd = s.append(count)
                        .append(' ')
                        .append(type)
                        .append(':').append(' ')
                        .append(traits.replace(",", ", "))
                        .toString();
                result.add(toAdd);
            }
        } catch (SQLException se) {
            throw new DatabaseException("Can't count fruits by types: " + se.getMessage());
        }
        return result;
    }

    // Retrieves ID of the fruit type for insertion into main Basket table
    // If the given type does not exist in the Types table, it will be inserted there
    private int typeIdByName(String typeName) throws DatabaseException {
        int typeId = getTypeIdByName(typeName);
        if(typeId < 0) {
            String insertQuery = "INSERT INTO Types SET type = '" + typeName.toLowerCase() + "'";
            try {
                Statement st = db.createStatement();
                int inserted = st.executeUpdate(insertQuery);
                if(inserted < 1) {
                    throw new DatabaseException("Can't create new fruit type: "
                            + typeName + "has not been added to a database");
                }
                st.close();
                typeId = getTypeIdByName(typeName);
            } catch (SQLException se) {
                throw new DatabaseException("Can't create new fruit type: " + se.getMessage());
            }
        }
        return typeId;
    }

    private int getTypeIdByName(String typeName) throws DatabaseException{
        String query = "SELECT id FROM Types WHERE type = '" + typeName.toLowerCase() + "'";
        int id = -1;
        try {
            Statement st = db.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                id = rs.getInt("id");
            }
        } catch (SQLException se) {
            throw new DatabaseException("Can't retrieve fruit type: " + se.getMessage());
        }
        return id;
    }
}
