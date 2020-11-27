CREATE TABLE IF NOT EXISTS Types(
    id INT PRIMARY KEY AUTO_INCREMENT,
    type VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS Basket(
    id INT PRIMARY KEY AUTO_INCREMENT,
    typeId INT,
    age INT,
    characteristics VARCHAR(2000),
    FOREIGN KEY (typeId) REFERENCES Types(id)
);