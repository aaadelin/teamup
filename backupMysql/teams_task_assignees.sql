CREATE DATABASE  IF NOT EXISTS `teams` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `teams`;
-- MySQL dump 10.13  Distrib 8.0.16, for Win64 (x86_64)
--
-- Host: localhost    Database: teams
-- ------------------------------------------------------
-- Server version	8.0.16

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
 SET NAMES utf8 ;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `task_assignees`
--

DROP TABLE IF EXISTS `task_assignees`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `task_assignees` (
  `task_id` int(11) NOT NULL,
  `assignees_id` int(11) NOT NULL,
  KEY `FK1kh4qhthi165derld1ngiutrl` (`assignees_id`),
  KEY `FKo4yer4djkjpmp3hrghfn0hh9` (`task_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `task_assignees`
--

LOCK TABLES `task_assignees` WRITE;
/*!40000 ALTER TABLE `task_assignees` DISABLE KEYS */;
INSERT INTO `task_assignees` VALUES (1,1),(4,1),(5,1),(8,1),(9,1),(10,1),(13,1),(15,1),(16,1),(19,1),(20,1),(21,1),(28,1),(30,1),(32,1),(32,27),(34,1),(34,27),(36,27),(36,1),(37,27),(38,1),(38,27),(39,1),(39,27),(40,1),(40,27),(41,1),(41,27),(42,1),(42,27),(43,27),(44,27),(45,1),(45,27),(47,1),(56,1),(56,27),(57,27),(57,1),(65,27),(68,27),(68,1),(69,1),(69,27),(70,1),(70,27),(71,27),(71,1),(72,1),(72,27),(73,1),(73,27),(74,1),(74,27),(75,1),(75,27),(76,1),(76,27),(77,1),(79,1),(79,27),(83,1),(83,27),(86,1),(86,27),(88,27),(88,1),(91,1),(91,27),(93,1),(93,27),(95,1),(95,27),(102,27),(102,1),(105,1),(105,27),(107,1),(107,27),(109,1),(109,27),(112,1),(112,27),(113,1),(113,27),(116,1),(116,27),(118,1),(118,27),(123,1),(123,27),(125,1),(125,27),(127,1),(127,27),(129,1),(129,27);
/*!40000 ALTER TABLE `task_assignees` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-08-16 12:40:42
