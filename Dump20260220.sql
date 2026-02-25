-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: universita
-- ------------------------------------------------------
-- Server version	8.0.45

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `appello`
--

DROP TABLE IF EXISTS `appello`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `appello` (
  `idAppello` int NOT NULL AUTO_INCREMENT,
  `Data` date NOT NULL,
  `Materia` int DEFAULT NULL,
  PRIMARY KEY (`idAppello`),
  KEY `da appello a corso_idx` (`Materia`),
  CONSTRAINT `da appello a corso` FOREIGN KEY (`Materia`) REFERENCES `corso` (`idcorso`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `appello`
--

LOCK TABLES `appello` WRITE;
/*!40000 ALTER TABLE `appello` DISABLE KEYS */;
INSERT INTO `appello` VALUES (2,'2026-10-30',1),(3,'2026-03-26',1),(5,'2026-11-29',3),(6,'2026-12-28',4),(7,'2026-03-26',4);
/*!40000 ALTER TABLE `appello` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `corso`
--

DROP TABLE IF EXISTS `corso`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `corso` (
  `idcorso` int NOT NULL AUTO_INCREMENT,
  `Materia` varchar(25) DEFAULT NULL,
  `Cattedra` int DEFAULT NULL,
  PRIMARY KEY (`idcorso`),
  KEY `da corso a prof_idx` (`Cattedra`),
  CONSTRAINT `da corso a prof` FOREIGN KEY (`Cattedra`) REFERENCES `professore` (`idProfessore`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `corso`
--

LOCK TABLES `corso` WRITE;
/*!40000 ALTER TABLE `corso` DISABLE KEYS */;
INSERT INTO `corso` VALUES (1,'Analisi I',1),(3,'Analisi II',1),(4,'Fisica I',4),(7,'Fisica II',4);
/*!40000 ALTER TABLE `corso` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `esiti`
--

DROP TABLE IF EXISTS `esiti`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `esiti` (
  `idVoto` int NOT NULL AUTO_INCREMENT,
  `voto` int NOT NULL,
  `dataRegistrazione` date DEFAULT (curdate()),
  `stato` varchar(20) DEFAULT 'attesa',
  `idStud` int DEFAULT NULL,
  `idApp` int DEFAULT NULL,
  PRIMARY KEY (`idVoto`),
  KEY `fk_esiti_studente` (`idStud`),
  KEY `fk_esiti_appello` (`idApp`),
  CONSTRAINT `fk_esiti_appello` FOREIGN KEY (`idApp`) REFERENCES `appello` (`idAppello`) ON DELETE CASCADE,
  CONSTRAINT `fk_esiti_studente` FOREIGN KEY (`idStud`) REFERENCES `studente` (`Matricola`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `esiti`
--

LOCK TABLES `esiti` WRITE;
/*!40000 ALTER TABLE `esiti` DISABLE KEYS */;
INSERT INTO `esiti` VALUES (1,18,'2026-02-19','rifiutato',1,2),(2,30,'2026-02-20','accettato',1,3),(3,25,'2026-02-20','accettato',4,7);
/*!40000 ALTER TABLE `esiti` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prenotazione`
--

DROP TABLE IF EXISTS `prenotazione`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `prenotazione` (
  `idpren` int NOT NULL AUTO_INCREMENT,
  `stud_prenotato` int DEFAULT NULL,
  `app_prenotato` int DEFAULT NULL,
  PRIMARY KEY (`idpren`),
  KEY `da prenotazione a stud_idx` (`stud_prenotato`),
  KEY `da prenotazione ad app_idx` (`app_prenotato`),
  CONSTRAINT `da prenotazione a stud` FOREIGN KEY (`stud_prenotato`) REFERENCES `studente` (`Matricola`),
  CONSTRAINT `da prenotazione ad app` FOREIGN KEY (`app_prenotato`) REFERENCES `appello` (`idAppello`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `prenotazione`
--

LOCK TABLES `prenotazione` WRITE;
/*!40000 ALTER TABLE `prenotazione` DISABLE KEYS */;
INSERT INTO `prenotazione` VALUES (3,1,2),(8,1,5),(9,1,3),(10,4,7);
/*!40000 ALTER TABLE `prenotazione` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `professore`
--

DROP TABLE IF EXISTS `professore`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `professore` (
  `idProfessore` int NOT NULL AUTO_INCREMENT,
  `username` varchar(45) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `tipo_utente` char(1) DEFAULT 'p',
  `nome` varchar(45) DEFAULT NULL,
  `cognome` varchar(45) DEFAULT NULL,
  `stato` varchar(20) DEFAULT 'ATTESA',
  PRIMARY KEY (`idProfessore`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `professore`
--

LOCK TABLES `professore` WRITE;
/*!40000 ALTER TABLE `professore` DISABLE KEYS */;
INSERT INTO `professore` VALUES (1,'prof1','$2a$10$eJQrZwYrZMNCTPptjOlr9uKQjJEBEbvZ0stD9X3OT8vm6EUMV5sWK','p','prof1','prof1','APPROVATO'),(2,'Orazio','$2a$10$9QRrjcqYZWfAqW3X5Wznm.u5Et/VOEDqNjRZk/SSw4CbfVydP9dc2','p','Orazio','Dazio','RIFIUTATO'),(4,'prof2','$2a$10$867QEx.cdDxcCJhD6bsSae1a5IdRubrTDvZijfXdVkcVYCYCF3t1C','p','prof2','prof2','APPROVATO');
/*!40000 ALTER TABLE `professore` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `segreteria`
--

DROP TABLE IF EXISTS `segreteria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `segreteria` (
  `id_seg` int NOT NULL AUTO_INCREMENT,
  `user` varchar(45) NOT NULL,
  `pass` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_seg`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `segreteria`
--

LOCK TABLES `segreteria` WRITE;
/*!40000 ALTER TABLE `segreteria` DISABLE KEYS */;
INSERT INTO `segreteria` VALUES (2,'admin1','$2a$10$jmBmX7ZiiwTnTe6YZdJQye3.Oamwm7jD26X/e6C6ICqfiQDQIYhN2'),(3,'admin2','$2a$10$0/MFuS8bDrFrbtV1VIzwKOvunzNM0nWOCBJVpsgyzG8dMlUJCAgqK');
/*!40000 ALTER TABLE `segreteria` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `studente`
--

DROP TABLE IF EXISTS `studente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `studente` (
  `Matricola` int NOT NULL AUTO_INCREMENT,
  `username` varchar(10) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `tipo_utente` char(1) DEFAULT 's',
  `nome` varchar(45) DEFAULT NULL,
  `cognome` varchar(45) DEFAULT NULL,
  `stato` varchar(20) DEFAULT 'ATTESA',
  PRIMARY KEY (`Matricola`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `studente`
--

LOCK TABLES `studente` WRITE;
/*!40000 ALTER TABLE `studente` DISABLE KEYS */;
INSERT INTO `studente` VALUES (1,'stud1','$2a$10$ykf2QQV2bpHfO6hQAh5U/O6qoTzpj7Gq9YrdA0PljrsnmEDRP2VbC','s','stud1','stud1','APPROVATO'),(2,'ALBERT','$2a$10$2heMGzX00WusfQ0RwS9GjOO8i57kCn1w19iVpQooywYW37Aa6OEn.','s','Alberico','De Paolo','RIFIUTATO'),(4,'stud2','$2a$10$7w1wLg4pPrQjyZfIMPVFz.NuJxTWS7eFjKRjhTSLbXN4Z66tr.dx6','s','stud2','stud2','APPROVATO');
/*!40000 ALTER TABLE `studente` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-02-20 17:32:57
