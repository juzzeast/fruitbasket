The compiled jar with dependencies included into the package for your convenience.

Usage: java -jar fruitbasket-1.0-jar-with-dependencies.jar -i test1.csv

Solution is built around in-memory database (H2) where data from csv file is being ingested.

However, the DB interaction part is encapsulated inside DAO object, so the actual implementation could be any.

That DAO is used by simple Service objects that represents the high-level operations.
These services, in turn, are being called by app's main.

The project is built with Maven:

mvn clean package

Tests (JUnit 5) are also driven by maven:

mvn test

Main logic is in DAO object, so tests are concentrated on it.

What I would do to further elevate the solution:
- check the the data fields to contain only alphanumeric chars
- generalize DAO methods (e.g. add "order" param to method that counts fruits by types)
- inside DAO object, I would move actual queries from the methods they're used in to constants stored in a separate helper object, to decouple it from H2 syntax
- use Inversion of Control framework, like Spring, to decouple the components
- add Javadoc