-- MySQL dump 10.13  Distrib 9.3.0, for macos15.2 (arm64)
--
-- Host: localhost    Database: horoscope
-- ------------------------------------------------------
-- Server version	9.3.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `horoscope`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `horoscope` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `horoscope`;

--
-- Table structure for table `user_profile`
--

DROP TABLE IF EXISTS `user_profile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_profile` (
  `id` binary(16) NOT NULL,
  `name` varchar(255) NOT NULL,
  `date_of_birth` date NOT NULL,
  `time_of_birth` time DEFAULT NULL,
  `place_of_birth` varchar(255) DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `timezone` varchar(255) DEFAULT NULL,
  `sun_sign_id` int NOT NULL,
  `rising_sign` varchar(255) DEFAULT NULL,
  `moon_sign` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `city` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_user_sunsign` (`sun_sign_id`),
  CONSTRAINT `fk_user_sunsign` FOREIGN KEY (`sun_sign_id`) REFERENCES `zodiac_sign` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_profile`
--

LOCK TABLES `user_profile` WRITE;
/*!40000 ALTER TABLE `user_profile` DISABLE KEYS */;
INSERT INTO `user_profile` VALUES (_binary '\'\ä+AuA–\æ4œA),”','Venessa','2001-04-02','11:00:00','Nairobi',-1.286389,36.817223,'Africa/Nairobi',1,'Gemini','Cancer','2025-09-07 09:53:03','2025-09-08 00:48:55',NULL),(_binary 'ˆ²ý*OB¢A0ª\êl\\','Ebony','1997-12-10','14:17:00','London',51.5072,-0.1276,'Europe/London',9,'Taurus','Taurus','2025-09-07 15:37:55','2025-09-08 00:50:23',NULL),(_binary 'Àþ\çG\÷¸H\'–D4²”','Amel','1998-04-21','23:34:00','London',51.509865,-0.118092,'Europe/London',2,'Sagittarius','Aquarius','2025-09-06 15:37:20','2025-09-07 15:15:29',NULL),(_binary '–EºK …l\ÇGy\öE#','Tara','1987-05-01','06:05:00','Dublin',53.3498,-6.2603,'Europe/Dublin',2,'Taurus','Gemini','2025-09-08 01:12:46','2025-09-08 01:13:06',NULL),(_binary '\Þ^q¶L¼«¸¢ŒÏ•\í','Aiden','1969-12-22',NULL,NULL,NULL,NULL,NULL,10,NULL,NULL,'2025-09-08 00:59:51','2025-09-08 00:59:51',NULL),(_binary '\ßS*JV¾+\Ú\Û&Œ\Ø?','Timnit','1949-07-01',NULL,NULL,NULL,NULL,NULL,4,NULL,NULL,'2025-09-08 01:00:14','2025-09-08 01:00:14',NULL),(_binary '\'ÁkW\ìüJ¬\ËQ©ƒu','Leo','1993-08-01','17:22:00','Cairo',30.0444,31.2357,'Africa/Cairo',5,'Capricorn','Capricorn','2025-09-08 01:14:31','2025-09-08 01:14:53',NULL),(_binary '*\Õú´\ÍJJƒ˜_\ô[o\Ö','Lina','1989-10-01','11:11:00','Madrid',40.4168,-3.7038,'Europe/Madrid',7,'Scorpio','Libra','2025-09-08 01:15:41','2025-09-08 01:15:59',NULL),(_binary ':­N\Æ?E\æx\ãA…_¦F','Aasha','1997-03-24',NULL,NULL,NULL,NULL,NULL,1,NULL,NULL,'2025-09-08 01:00:38','2025-09-08 01:00:38',NULL),(_binary ']\äS\ÖCHX—-Do\ê(','Shannon','1990-01-01','08:30:00','London',-1.286389,-0.1276,'Europe/London',10,'Aquarius','Pisces','2025-09-08 00:17:34','2025-09-08 00:19:09',NULL),(_binary 'eŠ\ê\Ìv¨A¬¼\Ç\ÒÐ´¯›\ö','Zara','2025-09-03',NULL,NULL,NULL,NULL,NULL,6,NULL,NULL,'2025-09-08 01:02:58','2025-09-08 01:02:58',NULL),(_binary 'hbŸ¬’Ac¨yx\ó\Õ\Û','Cora','2000-07-01','02:03:00','Toronto',43.6532,-79.3832,'America/Toronto',4,'Taurus','Cancer','2025-09-08 01:13:55','2025-09-08 01:14:18',NULL),(_binary 'pD\ôifrMì‘…˜>ù\àÈ‹','Sage','1994-12-01',NULL,NULL,NULL,NULL,NULL,9,NULL,NULL,'2025-09-08 01:18:12','2025-09-08 01:18:12',NULL),(_binary '™\÷(\Ö\ä’D\ã®\ìŸš’X','Soren','1951-11-01',NULL,NULL,NULL,NULL,NULL,8,NULL,NULL,'2025-09-08 01:16:24','2025-09-08 01:16:24',NULL),(_binary '¬\0\å§kˆ@…±ƒR¯@z0C','Roli','1998-01-07','13:15:00','Lagos',6.465422,3.406448,'Africa/Lagos',10,'Aries','Taurus','2025-09-07 15:24:54','2025-09-08 00:53:25',NULL),(_binary '¶ x~\Æ@u®\Zo†\ï­,','Ken','1996-01-24',NULL,NULL,NULL,NULL,NULL,11,NULL,NULL,'2025-09-08 01:01:15','2025-09-08 01:01:15',NULL),(_binary '\Çq\Ù1ü\ÞF×¹@#E\Ô\Z\"','Aria','1992-04-01','09:14:00','Athens',37.9838,23.7275,'Europe/Athens',1,'Taurus','Pisces','2025-09-08 01:11:30','2025-09-08 01:12:26',NULL),(_binary '\ÔG£[DZ¦…Ú¦¾¦','Love','1995-02-14',NULL,NULL,NULL,NULL,NULL,11,NULL,NULL,'2025-09-08 01:04:01','2025-09-08 01:04:01',NULL),(_binary '\îOh§O\ô®»\àt–«­','Gabe','1995-06-01','13:45:00','Berlin',52.52,13.405,'Europe/Berlin',3,'Virgo','Cancer','2025-09-08 01:13:17','2025-09-08 01:13:43',NULL),(_binary '\ñ…O\å¦L%°\æz\È\í„H','Vera','1999-09-01','04:59:00','Cape Town',-33.9249,18.4241,'Africa/Johannesburg',6,'Cancer','Taurus','2025-09-08 01:15:06','2025-09-08 01:15:29',NULL);
/*!40000 ALTER TABLE `user_profile` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `zodiac_sign`
--

DROP TABLE IF EXISTS `zodiac_sign`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `zodiac_sign` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `element` varchar(255) DEFAULT NULL,
  `modality` varchar(255) DEFAULT NULL,
  `ruling_planet` varchar(255) DEFAULT NULL,
  `traits` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `zodiac_sign`
--

LOCK TABLES `zodiac_sign` WRITE;
/*!40000 ALTER TABLE `zodiac_sign` DISABLE KEYS */;
INSERT INTO `zodiac_sign` VALUES (1,'Aries','Fire','Cardinal','Mars','confident, bold, competitive, energetic'),(2,'Taurus','Earth','Fixed','Venus','patient, grounded, loyal, comfort-seeking'),(3,'Gemini','Air','Mutable','Mercury','curious, witty, adaptable, communicative'),(4,'Cancer','Water','Cardinal','Moon','nurturing, intuitive, protective, sensitive'),(5,'Leo','Fire','Fixed','Sun','magnetic, expressive, generous, proud'),(6,'Virgo','Earth','Mutable','Mercury','methodical, helpful, analytical, practical'),(7,'Libra','Air','Cardinal','Venus','diplomatic, fair-minded, sociable, aesthetic'),(8,'Scorpio','Water','Fixed','Mars/Pluto','intense, perceptive, private, determined'),(9,'Sagittarius','Fire','Mutable','Jupiter','optimistic, philosophical, adventurous'),(10,'Capricorn','Earth','Cardinal','Saturn','ambitious, disciplined, responsible'),(11,'Aquarius','Air','Fixed','Saturn/Uranus','original, humanitarian, independent'),(12,'Pisces','Water','Mutable','Jupiter/Neptune','empathetic, imaginative, spiritual');
/*!40000 ALTER TABLE `zodiac_sign` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-09-08  2:22:53
