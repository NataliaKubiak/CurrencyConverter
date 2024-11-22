PRAGMA foreign_keys=OFF;
BEGIN TRANSACTION;
CREATE TABLE Currencies(
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	code varchar UNIQUE,
	name varchar,
	sign varchar
);
INSERT INTO Currencies VALUES(1,'AUD','Australian dollar','A$');
INSERT INTO Currencies VALUES(2,'USD','United States Dollar','$');
INSERT INTO Currencies VALUES(3,'EUR','Euro','€');
INSERT INTO Currencies VALUES(4,'JPY','Japanese Yen','¥');
INSERT INTO Currencies VALUES(5,'GBP','British Pound Sterling','£');
INSERT INTO Currencies VALUES(6,'CAD','Canadian Dollar','C$');
INSERT INTO Currencies VALUES(7,'CHF','Swiss Franc','CHF');
INSERT INTO Currencies VALUES(8,'INR','Indian Rupee','₹');
INSERT INTO Currencies VALUES(9,'RUB','Russian Ruble','₽');
INSERT INTO Currencies VALUES(10,'PLN','Polish Zloty','PLN');
INSERT INTO Currencies VALUES(13,'KRW','South Korean Won','₩');
INSERT INTO Currencies VALUES(14,'BRL','Brazilian Real','R$');
INSERT INTO Currencies VALUES(15,'CNY','Chinese Yuan','¥');
CREATE TABLE ExchangeRates (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	BaseCurrencyId INTEGER,
	TargetCurrencyId INTEGER,
	Rate Decimal(6),
	FOREIGN KEY (BaseCurrencyId) REFERENCES currencies(id),
	FOREIGN KEY (TargetCurrencyId) REFERENCES currencies(id),
	UNIQUE (BaseCurrencyId, TargetCurrencyId)
);
INSERT INTO ExchangeRates VALUES(1,2,3,0.949999999999999956);
INSERT INTO ExchangeRates VALUES(2,2,1,1.550000000000000044);
INSERT INTO ExchangeRates VALUES(3,2,4,155.1699999999999875);
INSERT INTO ExchangeRates VALUES(4,2,5,0.7900000000000000355);
INSERT INTO ExchangeRates VALUES(5,2,6,1.409999999999999921);
INSERT INTO ExchangeRates VALUES(6,2,7,0.8900000000000000133);
INSERT INTO ExchangeRates VALUES(7,2,8,84.4200000000000017);
INSERT INTO ExchangeRates VALUES(8,2,9,99.75);
INSERT INTO ExchangeRates VALUES(10,2,13,1397.980000000000018);
INSERT INTO ExchangeRates VALUES(11,2,14,5.790000000000000035);
INSERT INTO ExchangeRates VALUES(12,2,15,7.240000000000000213);
INSERT INTO ExchangeRates VALUES(13,2,10,4.089999999999999858);
INSERT INTO ExchangeRates VALUES(14,3,10,4.330000000000000071);
DELETE FROM sqlite_sequence;
INSERT INTO sqlite_sequence VALUES('Currencies',17);
INSERT INTO sqlite_sequence VALUES('ExchangeRates',18);
COMMIT;
