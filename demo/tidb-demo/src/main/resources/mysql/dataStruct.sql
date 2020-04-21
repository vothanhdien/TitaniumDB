CREATE SCHEMA TransactionLog;
USE TransactionLog;
CREATE TABLE `CreatedOrderLog` (
  `orderID` bigint(20) NOT NULL,
  `sourceID` bigint(20) DEFAULT 0,
  `targetID` bigint(20) DEFAULT 0,
  `amount` int(11) DEFAULT 0,
  `orderTime` bigint(20) DEFAULT 0,
  PRIMARY KEY (`orderID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;