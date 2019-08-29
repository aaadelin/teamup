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
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `comment` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `content` varchar(255) DEFAULT NULL,
  `date_posted` datetime DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `parent_id` int(11) DEFAULT NULL,
  `post_id` int(11) DEFAULT NULL,
  `parent_comm` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK8kcum44fvpupyw6f5baccx25c` (`user_id`),
  KEY `FKde3rfu96lep00br5ov0mdieyt` (`parent_id`),
  KEY `FKs1slvnkuemjsq2kj4h3vhx7i1` (`post_id`),
  KEY `FKotvu4pnh5vtuhr4jn40i5hibx` (`parent_comm`)
) ENGINE=MyISAM AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment`
--

LOCK TABLES `comment` WRITE;
/*!40000 ALTER TABLE `comment` DISABLE KEYS */;
INSERT INTO `comment` VALUES (1,'Didnt work out as intended, I broke more stuff than I fixed. Apparently it is not easy because of the side menu, there still has to be a way. Maybe remove the side menu. Or add another way to do it. To center the columns in the screen remaining','2019-10-01 00:00:00','Didnt work',3,NULL,20,NULL),(2,'Bootstrap won\'t always work. If jquery is commented out -> saved -> uncommented it will work, but not for long. Looltips still work, but they\'re not as appealing.','2019-07-26 13:21:07','Bootstrap problems',3,NULL,36,NULL),(3,'I don\'t think it is worth it for the log in. Maybe for the tasks, but they fetch pretty fast...','2019-08-12 20:50:56','Is it worth it?',3,NULL,48,NULL),(4,'The back-end has been secured with a SSL certificare and now runs on port 8443. HTTPS','2019-08-15 16:24:51','SSL encrypt',3,NULL,54,NULL),(5,'No need to do this anymore. \nShow page loading is because of app.js which is 11 MB(!!!) and its size will shrink if the project will be buid.','2019-08-20 19:59:47','No need',1,NULL,14,NULL),(6,'Only one version will be created. Which one?','2019-08-20 20:35:22','One version',3,NULL,42,NULL),(7,'Search in tasks is done. Next is for teams and projects.','2019-08-20 20:36:53','The search feature for task is done',3,NULL,52,NULL),(8,'Leave it for later but create it on task loading and refreshing because it doesn\'t look very pleasant when something changes. ','2019-08-21 15:46:00','Create it',3,NULL,48,NULL),(9,'Fix the timeline spacing.\nFetch the tasks for the piechart','2019-08-21 22:09:58','Tomorrow',3,NULL,44,NULL),(10,'Event triggers are added for when user \n-creates a task \n-creates a comment\n-creates a user\n-updates a task\n-updates a user\n-updates a comment\n-deletes a user','2019-08-24 20:03:33','Event triggers added',3,NULL,66,NULL),(11,'Update photo path to /api/user/{id}/photo','2019-08-25 14:22:23','Update photo path',3,NULL,69,NULL),(12,'Problem with endline. if i put \"pre\" it will not add css and if i replace endline with \"br\" it will not affect all lines.\nMore research','2019-08-25 15:14:29','\\n problem',3,NULL,72,NULL),(13,'The cause was that the tasks didn\'t refresh properly. fixed it with for in refresh for eash page for eash category.\nAn improvement may be updting only the affected categories','2019-08-25 21:08:30','Cause',3,NULL,61,NULL),(14,'Looks good for the moment.\nAny improvements?','2019-08-25 21:10:16','Looks good for the moment',3,NULL,49,NULL),(15,'A problem is that if a wanted to access url is set and the pages refreshes the \"wanted to access\" is still there.\nShould this be fixed?','2019-08-25 21:12:07','Problem ',3,NULL,45,NULL),(16,'Date paginate sunt pe:\n- Tasks page\n- History pe profile page.\n\nAr mai trebui creeate pe search page la taskuri si utilizatori.','2019-08-25 21:13:26','Until now',3,NULL,22,NULL),(17,'Using replace with regex worked just fine','2019-08-25 22:29:38','Fixed',3,NULL,72,NULL),(18,'Now it shows those to everyone that accesses the profile','2019-08-25 22:30:56','Show tasks',3,NULL,67,NULL),(20,'Closed because is duplicate of <a href=\"http://192.168.0.150:8080/task?taskId=66\"> this one </a>','2019-08-25 22:35:05','Closed',3,NULL,65,NULL),(21,'Logs are now saved to teamUpLogs.log\nMaybe configure not to have more than a maximum size? Is this possible?','2019-08-25 22:48:12','Saved',3,NULL,76,NULL),(22,'Moved checkbox there.\nFilter is not working properly all the time.\nAdded sort buttons, soon will be implemented.\nWill create another (minimalistic) way to see tasks. ','2019-08-27 17:56:48','Update',3,NULL,20,NULL),(23,'Split GET controller into 3:\n<ul>\n<li>user controller</li>\n<li>task controller</li>\n<li>the rest</li>\n</ul>','2019-08-27 21:23:21','Split get controller into 3',3,NULL,82,NULL),(24,'Updated in TaskPost, Departments in CreateTask.','2019-08-28 16:31:25','Updated',3,NULL,86,NULL),(25,'Schimbarea \"v-if\" cu \"v-show\" la afisarea categoriei ar trebui sa fixeze problema.','2019-08-28 20:37:20','Posibil fix:',3,NULL,64,NULL);
/*!40000 ALTER TABLE `comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hibernate_sequence`
--

DROP TABLE IF EXISTS `hibernate_sequence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `hibernate_sequence` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hibernate_sequence`
--

LOCK TABLES `hibernate_sequence` WRITE;
/*!40000 ALTER TABLE `hibernate_sequence` DISABLE KEYS */;
INSERT INTO `hibernate_sequence` VALUES (193),(193),(193),(193),(193);
/*!40000 ALTER TABLE `hibernate_sequence` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `post`
--

DROP TABLE IF EXISTS `post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `post` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `task_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK5bgat86mp7c0pun36x11esmi8` (`task_id`)
) ENGINE=MyISAM AUTO_INCREMENT=92 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `post`
--

LOCK TABLES `post` WRITE;
/*!40000 ALTER TABLE `post` DISABLE KEYS */;
INSERT INTO `post` VALUES (1,1),(2,2),(3,3),(4,4),(5,5),(6,6),(7,7),(8,8),(9,9),(10,10),(11,11),(12,12),(13,13),(14,14),(15,15),(16,16),(17,17),(18,18),(19,19),(20,20),(21,21),(22,22),(23,23),(24,24),(25,25),(26,26),(27,27),(28,28),(29,29),(30,30),(31,31),(32,32),(33,33),(34,34),(35,35),(36,36),(37,37),(38,38),(39,39),(40,40),(41,41),(42,42),(43,43),(44,44),(45,45),(46,46),(47,47),(48,48),(49,49),(50,50),(51,51),(52,52),(53,53),(54,54),(55,55),(56,56),(57,57),(58,58),(59,59),(60,60),(61,61),(62,62),(63,63),(64,64),(65,65),(66,66),(67,67),(68,68),(69,69),(70,70),(71,71),(72,72),(76,73),(77,74),(78,75),(79,76),(80,77),(81,78),(82,79),(83,80),(84,81),(85,82),(86,83),(87,84),(88,85),(89,86),(90,87),(91,88);
/*!40000 ALTER TABLE `post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `project`
--

DROP TABLE IF EXISTS `project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `project` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `deadline` datetime DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `owner_id` int(11) DEFAULT NULL,
  `version` varchar(50) DEFAULT '0.0.1',
  PRIMARY KEY (`id`),
  KEY `FK9ydhxbq67a3m0ek560r2fq38g` (`owner_id`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `project`
--

LOCK TABLES `project` WRITE;
/*!40000 ALTER TABLE `project` DISABLE KEYS */;
INSERT INTO `project` VALUES (1,'2019-10-01 00:00:00','Lucrare Licenta','Lucrare Licenta Mirea Adelin',1,'0.0.1'),(2,'2020-12-31 00:00:00','Nou proiect pentru teste','Mock project',1,'0.0.1');
/*!40000 ALTER TABLE `project` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `task`
--

DROP TABLE IF EXISTS `task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `task` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `deadline` datetime DEFAULT NULL,
  `department` int(11) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `difficulty` int(11) NOT NULL,
  `done_at` datetime DEFAULT NULL,
  `last_changed` datetime DEFAULT NULL,
  `priority` int(11) NOT NULL,
  `summary` varchar(255) DEFAULT NULL,
  `task_status` int(11) DEFAULT NULL,
  `task_type` int(11) DEFAULT NULL,
  `project_id` int(11) DEFAULT NULL,
  `reporter_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKk8qrwowg31kx7hp93sru1pdqa` (`project_id`),
  KEY `FKkm56sdvq22y1ikcgao47pirs0` (`reporter_id`)
) ENGINE=MyISAM AUTO_INCREMENT=89 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `task`
--

LOCK TABLES `task` WRITE;
/*!40000 ALTER TABLE `task` DISABLE KEYS */;
INSERT INTO `task` VALUES (1,'2019-07-14 21:55:31','2019-09-29 00:00:00',1,'BACK-end',1,NULL,'2019-08-12 21:57:03',1,'Partea de back-end',1,1,1,1),(2,'2019-07-16 21:41:00','2019-07-20 20:41:00',0,'Creating task errors',3,'2019-08-12 20:45:29','2019-08-12 20:45:29',3,'Creating task errors',5,1,1,3),(3,'2019-07-20 17:41:00','2019-07-20 23:41:00',0,'Validarea datelor de input pentru adaugarea de task pe front si back',2,'2019-08-12 20:43:38','2019-08-12 20:43:38',1,'Validare date pe front si back',5,0,1,3),(4,'2019-07-18 20:41:00','2019-07-22 20:41:00',0,'Create notification for adding task',3,'2019-08-12 20:44:01','2019-08-12 20:44:01',2,'Create notification',5,3,1,3),(5,'2019-07-17 09:41:00','2019-07-24 20:41:00',0,'Add date time picker in add task form',1,'2019-08-12 20:44:28','2019-08-12 20:44:28',1,'Add date time picker ',5,3,1,3),(6,'2019-07-19 20:41:00','2019-07-24 20:41:00',0,'Create task page for viewing all the details',2,NULL,'2019-08-12 20:43:09',1,'Create task page ',2,2,1,3),(7,'2019-07-20 20:41:00','2019-07-26 09:41:00',0,'Modificare status task atat prin dropdown option din descrierea detaliata cat si prin drag and drop',1,NULL,'2019-08-21 15:07:22',2,'Modificare status task',2,3,1,3),(8,'2019-07-20 20:10:54','2019-07-23 12:41:00',0,'Second type of view for task list',1,NULL,'2019-07-23 18:52:39',3,'Second type of view',0,3,1,3),(9,'2019-07-20 21:07:39','2019-07-24 21:06:00',0,'Assign/Unassign la Task',2,NULL,'2019-07-23 17:52:42',1,'Assign/Unassign la Task',0,3,1,1),(10,'2019-07-20 21:10:22','2019-07-21 21:09:00',0,'Assign to me button doesn\'t work yet. Make it happen',3,NULL,'2019-07-22 20:56:26',2,'Make \"assign to me\" button work',3,2,1,1),(11,'2019-07-21 12:35:13','2019-07-22 12:34:57',0,'This is a task created without selecting any assignees',3,NULL,'2019-07-21 09:35:13',1,'Creating a task without assignees',0,0,1,1),(12,'2019-07-21 20:46:12','2019-07-24 20:45:54',0,'Add users list and reporter in task page and the possibility to change them or unassign all',2,'2019-08-12 20:42:15','2019-08-12 20:42:15',1,'Add users in task page',5,3,1,3),(13,'2019-07-22 20:03:21','2019-07-23 20:02:50',4,'Add more persons to one task',3,NULL,'2019-07-24 20:38:15',3,'Add more persons to one task',3,0,1,1),(14,'2019-07-23 20:05:09','2019-08-14 20:04:43',0,'Optimize page loading by verifying if the user is authenticated or not and changing the \"beforeMounted\" with \"mounted\" or something else. And maybe change routing to use dynamic loading of pages.',1,NULL,'2019-08-08 22:44:04',2,'Optimize page loading',0,2,1,3),(15,'2019-07-23 20:06:03','2019-08-08 20:05:46',0,'Add remove button near user in create task',2,NULL,'2019-08-12 16:38:03',1,'Add remove button near user ',3,3,1,3),(16,'2019-07-23 20:06:36','2019-08-08 20:06:19',0,'Add remove and add button in task detalied view for assignees',2,NULL,'2019-08-13 19:27:09',3,'Add remove button in task detalied view',3,3,1,3),(17,'2019-07-23 20:07:45','2019-08-21 20:07:29',5,'Make Tasks page using row and cols in bootstrap. Center the columns',2,NULL,'2019-08-20 20:14:21',2,'Re-arrange task page',2,3,1,3),(18,'2019-07-23 20:09:07','2019-07-23 21:55:42',5,'Change input from description with a text-area with 3 rows ',3,NULL,'2019-07-24 21:09:57',1,'Update \'create task\'',3,3,1,3),(19,'2019-07-23 20:09:53','2019-08-29 20:08:46',5,'Add sorting methods for tasks in tasks page',1,NULL,'2019-08-28 16:22:52',3,'Sorting tasks page',2,3,1,3),(20,'2019-07-23 20:12:16','2019-08-12 20:11:55',5,'Make left menu from task page usable moving \'Also see reported issues\' there and adding filters and sorting options. And also an option to switch views (for when the next view is created) and see all tasks, not only the ones that contains him.',1,NULL,'2019-08-28 16:23:03',1,'Make left menu usable ',2,3,1,3),(21,'2019-07-23 20:27:11','2019-07-23 20:55:50',5,'On click on an editable field open edit view',3,NULL,'2019-07-24 21:13:53',1,'Update task post',3,3,1,3),(22,'2019-07-23 20:28:36','2019-09-01 20:28:13',0,'Creeare paginare de date. Adaugare optiune de \'show more\' sau \'infinity scroll\'',3,NULL,'2019-08-20 16:23:40',3,'Date paginate',2,3,1,3),(23,'2019-07-23 20:30:30','2019-07-25 20:30:12',5,'Change \'Created at\' field in task post not to show the T. Will need backend changes',3,NULL,'2019-07-24 21:11:56',3,'Change \'Created at\'',3,2,1,3),(24,'2019-07-23 20:32:38','2019-07-23 20:32:30',0,'Testing a task creation with new Text area\nHello.',3,'2019-08-12 20:48:02','2019-08-12 20:48:02',3,'Testing a task creation',5,0,1,3),(25,'2019-07-23 21:00:40','2019-08-08 21:00:19',0,'Make DoneAt to change when task status is set to \'closed\'',3,NULL,'2019-08-13 19:27:06',3,'Make GetDoneAt to change when task status is set to \'closed\'',3,2,1,3),(26,'2019-07-23 21:04:32','2019-07-23 21:04:26',0,'NewCreatedAtDummy',3,'2019-08-12 20:42:47','2019-08-12 20:42:47',3,'NewCreatedAtDummy',5,0,1,3),(27,'2019-07-23 21:27:50','2019-07-24 21:27:10',0,'At task creation automatically create a post for it. Will be useful when adding admin comments',3,NULL,'2019-07-24 21:53:53',2,'Create post for task',3,2,1,3),(28,'2019-07-24 18:18:55','2019-07-26 18:18:33',5,'Eye button next to passsword fields to see the pass',2,'2019-08-12 20:45:06','2019-08-12 20:45:06',3,'Eye button for password fields',5,3,1,3),(29,'2019-07-24 18:19:55','2019-08-10 18:19:41',0,'Add validation on front-end so that passwords coincide',3,NULL,'2019-08-21 09:01:45',1,'Front-end validation for mathcing passwords.',3,2,1,3),(30,'2019-07-24 18:21:03','2019-08-12 22:20:36',5,'Login notification for retry-ing if an error appeared',2,NULL,'2019-08-21 09:10:15',1,'Login improvement',3,3,1,3),(31,'2019-07-24 18:22:06','2019-08-13 21:21:50',5,'Data inserted in form does not dissapear if it is invalid',2,NULL,'2019-08-21 09:10:20',3,'Form improvenemt',3,2,1,3),(32,'2019-07-24 18:23:00','2019-08-13 18:22:40',5,'Add red border input at login for starters and then at creating forms',2,NULL,'2019-08-13 19:26:51',1,'Red border input',3,3,1,3),(33,'2019-07-24 18:24:00','2019-08-14 18:23:50',0,'Validate that deadlines cannot be after the deadline of the project or before the current day',2,NULL,'2019-08-20 16:23:46',3,'Deadline validation for issues',2,2,1,3),(34,'2019-07-24 18:24:41','2019-08-17 18:24:27',0,'Tasks cannot be changed by more than one stage',1,NULL,'2019-08-13 19:26:48',2,'Task stages fix',3,2,1,3),(35,'2019-07-24 18:25:45','2019-08-17 18:25:32',0,'Show bordered div where the task can be added in the GUI',1,NULL,'2019-07-24 18:25:45',2,'Bordered div where task can be added',0,3,1,3),(36,'2019-07-24 18:26:39','2019-07-26 18:26:21',5,'Tooltip with a part of description when hovered over a task',2,NULL,'2019-07-24 21:11:50',1,'Tooltip for tasks',2,3,1,3),(37,'2019-07-24 18:29:28','2019-07-24 18:29:18',0,'DummyTask',3,NULL,'2019-08-12 21:55:02',3,'DummyTask',2,1,1,3),(38,'2019-07-24 19:36:42','2019-08-11 19:36:13',5,'Change taskbox component with span and check if you cand make it not drag anything but the main div to be moved',2,NULL,'2019-08-21 09:10:55',3,'Drag&drop update span&div',3,2,1,3),(39,'2019-07-25 19:12:09','2019-07-26 19:11:53',0,'Make addition of comments possible',2,NULL,'2019-08-13 19:28:56',1,'Create comments',3,2,1,3),(40,'2019-07-25 19:24:15','2019-07-26 19:23:59',0,'Add parent comment to be able to add replyes',3,NULL,'2019-08-07 21:38:45',3,'Add parent comment in comment entity',2,2,1,3),(41,'2019-07-25 20:35:45','2019-08-13 20:35:28',0,'Change dates from program with exception of deadlines and the ones that are not based on the current time to be added from backend. Simplicity?',3,NULL,'2019-08-13 18:43:34',3,'Change dates from program to be added from backend',2,2,1,3),(42,'2019-07-25 21:27:31','2019-08-27 20:35:42',5,'Create home page with its two versions',1,NULL,'2019-08-22 13:37:39',2,'Create home page',0,0,1,3),(43,'2019-07-25 21:38:49','2019-09-04 21:38:28',5,'Create projects page with two columns:\nWorking on and all (that are availabe to me -- TODO)',1,NULL,'2019-08-27 20:06:32',2,'Create projects page',2,0,1,3),(44,'2019-07-25 21:40:13','2019-09-05 21:39:55',5,'Create profile page where you can see a summary, change your password and upload another photo for your profile. \nSee your latest comments and the projects you\'re on',1,NULL,'2019-08-25 21:25:13',3,'Create profile page',2,0,1,3),(45,'2019-08-08 11:27:38','2019-08-09 11:27:01',0,'Add wanted to access url. If uset tries to access a url that cannot be accessed without login it will be saved and redirected to that one after he logges in successfully',2,NULL,'2019-08-11 21:06:35',2,'Wanted to access',2,3,1,3),(46,'2019-08-11 20:48:00','2019-10-14 20:47:41',0,'Create notifications and updates using websockets AND/OR an option to make requests to the server every X seconds',1,NULL,'2019-08-11 20:47:59',2,'Notifications',0,3,1,3),(47,'2019-08-11 21:10:31','2020-01-12 21:10:13',0,'Allowing a task to be split into subtasks? and the subtasks can be assigned only to the ones that are assigned to the \"parent\" task.\nMicro-tasking',1,NULL,'2019-08-21 15:03:22',3,'Subtasking',0,3,1,3),(48,'2019-08-11 21:12:24','2019-08-25 21:12:06',0,'Add loading screen when logging in and (maybe) when fetching tasks?',2,NULL,'2019-08-11 21:12:24',3,'Loading screen',0,3,1,3),(49,'2019-08-12 21:04:33','2019-09-02 21:03:57',5,'Remake comment list appearance adding username, date posted and MAYBE user photo',3,NULL,'2019-08-25 21:10:25',3,'Reformat comments appearance',3,2,1,3),(50,'2019-08-12 21:53:57','2020-01-27 21:53:40',5,'Maybe create a dark mode',1,NULL,'2019-08-21 15:05:26',3,'Dark Mode',0,3,1,3),(51,'2019-08-13 20:09:07','2019-09-30 20:08:48',0,'maxDate does not have effect when applied immediatly but when closed and reopen is takes the last setup',2,NULL,'2019-08-13 20:09:07',3,'Fix maxDate in DatePicker',0,2,1,3),(52,'2019-08-13 21:24:53','2019-09-05 21:23:46',0,'Create the first version of search page',2,NULL,'2019-08-27 17:41:54',2,'Create search page',1,3,1,3),(53,'2019-08-13 21:29:40','2019-09-10 21:29:25',5,'Rearrange Task\'s Post details columns and make it look better',1,NULL,'2019-08-27 20:07:46',3,'Rearrange TaskPost',2,3,1,3),(54,'2019-08-14 17:13:04','2019-08-17 17:12:37',0,'Encrypt password on front-end and decrypt it on backend for higher security',2,NULL,'2019-08-21 15:07:10',1,'Password protection',2,3,1,3),(55,'2019-08-17 17:21:56','2019-08-18 21:21:44',0,'Move authentication check out from controllers',2,NULL,'2019-08-21 07:58:38',3,'Move authentication check out from controllers',3,3,1,3),(56,'2019-08-18 22:09:49','2019-08-21 22:09:15',5,'Calendar in TaskPost has a problem showing incorrect starting date.',2,NULL,'2019-08-20 21:03:02',1,'Deadline change',2,1,1,3),(57,'2019-08-19 17:05:07','2019-08-20 17:04:56',0,'Logout after X minutes where X can be set by user (or not, default=60);',1,NULL,'2019-08-21 09:10:07',2,'Auto Logout',3,3,1,3),(58,'2019-08-19 17:31:02','2019-08-21 17:30:45',0,'Add\"Joined\" filed to contain the date of creation',2,NULL,'2019-08-21 09:01:20',1,'Add \"Joined\" in user at creation',3,3,1,3),(59,'2019-08-19 22:13:41','2019-08-19 22:13:33',0,'dummy task',3,NULL,'2019-08-20 20:17:25',3,'dummy task',0,0,1,1),(60,'2019-08-20 21:04:17','2019-08-23 21:04:01',5,'Show task status in brackets in search.\nE.g.: Creare status [CLOSED]',3,NULL,'2019-08-27 17:42:05',3,'Show status in search',3,3,1,3),(61,'2019-08-21 09:11:44','2019-08-23 09:11:28',5,'Investigate drop problem',1,NULL,'2019-08-21 15:14:37',1,'Drop problem investigate',2,2,1,3),(62,'2019-08-21 16:42:02','2019-09-04 16:41:46',5,'Optiune de filtrare a taskurilor dupa status',2,NULL,'2019-08-25 21:20:52',3,'Filtrare in search',1,3,1,3),(63,'2019-08-21 16:43:06','2019-09-11 16:42:51',5,'Adaugare optiune de sortare a taskurilor in search in functie de data creeari, data ultimei modificari',1,NULL,'2019-08-28 18:47:34',2,'Adaugare Sortare in Search',1,3,1,3),(64,'2019-08-21 16:46:45','2019-09-04 16:46:29',5,'Creeare posibilitate de a face drag&drop la taskuri in alta categorie si cand aceasta este restransa',2,NULL,'2019-08-28 16:33:26',2,'Drop cu categorie restransa',0,3,1,3),(65,'2019-08-21 19:03:28','2019-08-28 19:03:15',0,'Add history of events to user',2,NULL,'2019-08-25 22:33:57',2,'Add history to user',5,3,1,3),(66,'2019-08-22 12:56:54','2019-08-29 12:56:24',0,'Add elements to user activity when user mades a change to a task, creates a task.',2,NULL,'2019-08-24 20:17:36',3,'Add history elements to user activity',2,3,1,3),(67,'2019-08-22 19:49:03','2019-09-12 19:48:48',5,'When in user\'s profile, if clicked on a category in piechart open a popup containing tasks in that category if you\'re the user.',2,NULL,'2019-08-25 22:32:54',2,'Popup with tasks of category',2,3,1,3),(68,'2019-08-22 19:51:08','2019-09-06 19:50:53',5,'chose what to show in piechart.\nIf teamlead you can see your team\'s status or you can only see from a specific time (one week ago)',1,NULL,'2019-08-22 19:51:08',3,'Different piecharts',0,3,1,3),(69,'2019-08-22 19:51:55','2019-09-26 19:51:39',0,'Redesign according to: <a href=\"https://hackernoon.com/restful-api-designing-guidelines-the-best-practices-60e1d954e7c9\">this<a>',2,NULL,'2019-08-25 15:35:40',3,'Redesign REST API endpoints',1,2,1,3),(70,'2019-08-24 16:22:30','2019-08-27 16:22:00',0,'Upload photo on profile',2,NULL,'2019-08-25 21:07:27',3,'Upload photo',3,3,1,3),(71,'2019-08-24 19:38:43','2019-08-25 19:38:25',0,'Update password in user profile form',3,NULL,'2019-08-25 21:07:24',1,'Update password',3,2,1,3),(72,'2019-08-24 20:07:12','2019-08-28 20:06:54',5,'Update text formatting in comments and task descriptions to accept new lines and maybe urls',2,NULL,'2019-08-25 22:29:59',3,'Update text formatting in comments',3,2,1,3),(73,'2019-08-25 21:26:36','2019-09-01 21:26:15',0,'Config Spring to save logs in file',3,NULL,'2019-08-26 18:06:05',3,'Save logs in file',3,3,1,3),(74,'2019-08-25 21:29:07','2019-09-08 21:28:49',0,'On time chart for each user to see tasks that have been \'approved\' before deadline',2,NULL,'2019-08-25 21:29:07',3,'OnTime Chart',0,3,1,3),(75,'2019-08-25 21:30:34','2019-09-30 21:30:09',5,'In tasks page, make columns\' height equal to the one of the page and scroll on each column individually',1,NULL,'2019-08-25 21:30:34',3,'[OPTIONAL] Scroll on column',0,3,1,3),(76,'2019-08-25 22:25:46','2019-09-30 22:25:26',0,'Compress image on backend if it\'s larger than 4MB',2,NULL,'2019-08-25 22:25:46',3,'Compress image on backend',0,3,1,3),(77,'2019-08-26 16:50:02','2019-08-30 16:49:32',0,'Make \'Create team\' ',3,NULL,'2019-08-26 16:50:02',2,'Make \'Create team\' ',0,2,1,3),(78,'2019-08-26 16:51:33','2019-09-06 16:51:04',0,'Make \'Create project\' available first in admin menu and then in user\'s menu',2,NULL,'2019-08-26 16:51:33',3,'Make \'Create project\' ',0,3,1,3),(79,'2019-08-26 18:59:06','2019-09-02 18:57:39',0,'Redesign controllers \nSplit them by entity?\nE.g. RestGetTaskController, RestGetProjectController...',2,NULL,'2019-08-27 21:22:02',3,'[HOME] Redesign controllers',1,3,1,3),(80,'2019-08-26 20:36:37','2019-09-16 20:36:18',5,'Create small (minimalistic) view option for tasks.\nIt contains only the priority and the name',3,NULL,'2019-08-28 18:49:29',3,'Create small view',0,3,1,3),(81,'2019-08-27 17:26:40','2019-09-03 17:26:27',4,'Write unit tests',3,NULL,'2019-08-27 17:26:40',3,'[HOME] write tests',0,0,1,3),(82,'2019-08-27 19:22:39','2019-09-30 00:00:00',5,'Group events in timeline by date',2,NULL,'2019-08-27 19:22:39',3,'Group events in timeline by date',0,3,1,3),(83,'2019-08-27 21:33:09','2019-08-29 21:32:29',5,'update enum values in selects. Replace the underscore (_) with space ( ).',3,NULL,'2019-08-28 16:31:31',2,'Update enums in select',2,2,1,3),(84,'2019-08-28 16:12:07','2019-09-18 16:11:40',5,'More generalization for fetch methods in tasks page where they are dependent on status\n\nMaybe create an array of statuses and iterate over it',2,NULL,'2019-08-28 16:12:07',2,'More generalization in Tasks page',0,2,1,3),(85,'2019-08-28 18:04:38','2019-08-29 18:03:04',5,'Paginarea taskurilor in search in grupuri de 5 sau 10 cu butoane de next si previous',2,NULL,'2019-08-28 21:18:44',2,'Paginare in search',2,3,1,3),(86,'2019-08-28 21:06:45','2019-08-30 21:06:26',5,'User MAX_RESULTS constant from repository for fetch results counting. Will be useful if the number is changing.',3,NULL,'2019-08-28 21:06:45',1,'Use MAX_RESULTS from Repository',0,2,1,3),(87,'2019-08-28 21:25:03','2019-09-30 21:21:53',0,'Edit number of items per page?\nPaginated search in tasks?',1,NULL,'2019-08-28 21:25:03',3,'Edit number of items per page',0,3,1,3),(88,'2019-08-28 21:27:24','2019-09-18 21:27:09',5,'Deadline in calendar does not change reactively when a project is selected.\nFix this problem or add a front validation and a note with selected project\'s deadline for more intuitivity',2,NULL,'2019-08-28 21:27:24',2,'Deadline problem in create task',0,2,1,3);
/*!40000 ALTER TABLE `task` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `task_assignees`
--

DROP TABLE IF EXISTS `task_assignees`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `task_assignees` (
  `task_id` int(11) NOT NULL AUTO_INCREMENT,
  `assignees_id` int(11) NOT NULL,
  KEY `FK1kh4qhthi165derld1ngiutrl` (`assignees_id`),
  KEY `FKo4yer4djkjpmp3hrghfn0hh9` (`task_id`)
) ENGINE=MyISAM AUTO_INCREMENT=117 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `task_assignees`
--

LOCK TABLES `task_assignees` WRITE;
/*!40000 ALTER TABLE `task_assignees` DISABLE KEYS */;
INSERT INTO `task_assignees` VALUES (1,1),(2,1),(3,1),(4,1),(5,1),(10,1),(6,1),(14,1),(7,1),(8,1),(9,1),(10,1),(11,1),(29,1),(13,1),(13,3),(33,1),(33,3),(14,3),(14,1),(15,3),(16,1),(16,3),(17,1),(17,3),(18,1),(18,3),(19,1),(19,3),(20,3),(20,1),(21,3),(22,3),(23,1),(23,3),(24,1),(25,1),(25,3),(26,3),(26,1),(27,3),(28,3),(28,1),(29,1),(29,3),(30,1),(30,3),(31,3),(31,1),(32,1),(32,3),(33,1),(33,3),(34,1),(34,3),(35,1),(35,3),(36,1),(36,3),(37,1),(38,1),(38,3),(39,1),(39,3),(40,1),(40,3),(41,3),(41,1),(42,3),(42,1),(43,1),(43,3),(67,3),(44,1),(45,3),(45,1),(46,1),(46,3),(47,1),(47,3),(48,1),(48,3),(49,1),(49,3),(113,1),(113,3),(116,1),(116,3),(50,1),(50,3),(51,1),(51,3),(65,3),(52,3),(53,1),(53,3),(54,1),(54,3),(55,3),(55,1),(56,3),(56,1),(57,1),(57,3),(58,3),(58,1),(59,1),(60,3),(61,3),(62,3),(63,3),(64,3),(66,3),(44,3),(68,3),(69,3),(70,3),(71,3),(72,3),(73,3),(74,3),(75,3),(76,3),(77,3),(78,3),(79,3),(80,3),(81,3),(82,3),(83,3),(84,3),(85,3),(86,3),(87,3),(88,3);
/*!40000 ALTER TABLE `task_assignees` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `team`
--

DROP TABLE IF EXISTS `team`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `team` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `department` int(11) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `location` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `leader_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKbxs8rhdluvnucyymbjowulrl6` (`leader_id`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `team`
--

LOCK TABLES `team` WRITE;
/*!40000 ALTER TABLE `team` DISABLE KEYS */;
INSERT INTO `team` VALUES (1,0,'Devs','Cluj-Napoca','Devs Cluj',NULL),(2,0,'Interns','Cluj-Napoca','Interns Cluj',NULL);
/*!40000 ALTER TABLE `team` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `first_name` varchar(255) DEFAULT NULL,
  `hash_key` varchar(255) DEFAULT NULL,
  `is_active` bit(1) NOT NULL,
  `last_active` datetime DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `photo` varchar(255) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `team_id` int(11) DEFAULT NULL,
  `new_photo` varchar(255) DEFAULT NULL,
  `minutes_until_logout` int(11) DEFAULT '60',
  `joined_at` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKbmqm8c8m2aw1vgrij7h0od0ok` (`team_id`)
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'Alex','dd24cbcba8c6ffce23089d09680be532',_binary '\0','2019-08-20 20:17:26','Relu','b8b28fcfe009057f2ef7362b1e91fe7a',NULL,2,'ralex',1,NULL,60,'2019-07-01'),(2,'Sys','F61599449EF3BA054C5AE43675D46A92',_binary '\0','2019-08-25 16:32:23','Admin','5f4dcc3b5aa765d61d8327deb882cf99',NULL,5,'admin',NULL,NULL,60,'2019-07-01'),(3,'Adelin','912FC8B10632573D0F7AD378D0A91889',_binary '','2019-08-29 08:36:20','Mirea','3a1c94d56271c14e07f477fa49c740e3','3',2,'amirea',1,NULL,60,'2019-07-01'),(4,'Test','5EC79E7A94C3E998D3C3BE6EF46713D7',_binary '\0','2019-08-25 15:46:24','User','E10ADC3949BA59ABBE56E057F20F883E',NULL,0,'tuser',2,NULL,10000,'2019-07-01');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_event`
--

DROP TABLE IF EXISTS `user_event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `user_event` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `event_type` int(11) DEFAULT NULL,
  `time` datetime DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKk3smcqwou8absq8qjt3wk4vy9` (`user_id`)
) ENGINE=MyISAM AUTO_INCREMENT=90 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_event`
--

LOCK TABLES `user_event` WRITE;
/*!40000 ALTER TABLE `user_event` DISABLE KEYS */;
INSERT INTO `user_event` VALUES (1,'Created',0,'2019-07-01 00:00:00',3),(2,'Created first task',0,'2019-07-02 00:00:00',3),(3,'Updated task Regesign REST API endpoints',1,'2019-08-24 13:30:40',3),(4,'Updated task Sorting tasks page',1,'2019-08-24 13:30:54',3),(5,'Updated task Sorting tasks page',1,'2019-08-24 13:31:00',3),(6,'Created task Upload photo',0,'2019-08-24 16:22:30',3),(7,'Updated task Upload photo',1,'2019-08-24 16:32:31',3),(8,'Updated task Upload photo',1,'2019-08-24 19:37:30',3),(9,'Created task Update password',0,'2019-08-24 19:38:43',3),(10,'Added comment \"Event trig\" at task Add history elements to user activity',0,'2019-08-24 20:03:33',3),(11,'Created task Update text formatting in comments',0,'2019-08-24 20:07:12',3),(12,'Updated task Add history elements to user activity',1,'2019-08-24 20:17:36',3),(13,'Added comment \"Update photo path\" at task Regesign REST API endpoints',0,'2019-08-25 14:21:18',3),(14,'Updated task Update text formatting in comments',1,'2019-08-25 14:35:51',3),(15,'Added comment \"\\n problem\" at task Update text formatting in comments',0,'2019-08-25 15:14:25',3),(16,'Updated task Redesign REST API endpoints',1,'2019-08-25 15:34:27',3),(17,'Updated task Redesign REST API endpoints',1,'2019-08-25 15:35:40',3),(18,'Updated task Update password',1,'2019-08-25 15:39:43',3),(19,'Updated task Update password',1,'2019-08-25 21:03:11',3),(20,'Updated task Update password',1,'2019-08-25 21:07:24',3),(21,'Updated task Upload photo',1,'2019-08-25 21:07:27',3),(22,'Added comment \"Cause\" at task Drop problem investigate',0,'2019-08-25 21:08:30',3),(23,'Added comment \"Looks good for the moment\" at task Reformat comments appearance',0,'2019-08-25 21:10:16',3),(24,'Updated task Reformat comments appearance',1,'2019-08-25 21:10:25',3),(25,'Added comment \"Problem \" at task Wanted to access',0,'2019-08-25 21:12:06',3),(26,'Added comment \"Until now\" at task Date paginate',0,'2019-08-25 21:13:26',3),(27,'Updated task Create profile page',1,'2019-08-25 21:19:23',3),(28,'Updated task Show status in search',1,'2019-08-25 21:20:31',3),(29,'Updated task Filtrare in search',1,'2019-08-25 21:20:52',3),(30,'Updated task Create profile page',1,'2019-08-25 21:25:13',3),(31,'Created task Save logs in file',0,'2019-08-25 21:26:36',3),(32,'Created task OnTime Chart',0,'2019-08-25 21:29:07',3),(33,'Created task [OPTIONAL] Scroll on column',0,'2019-08-25 21:30:34',3),(34,'Created task Compress image on backend',0,'2019-08-25 22:25:46',3),(35,'Added comment \"Fixed\" at task Update text formatting in comments',0,'2019-08-25 22:29:38',3),(36,'Updated task Update text formatting in comments',1,'2019-08-25 22:29:46',3),(37,'Updated task Update text formatting in comments',1,'2019-08-25 22:29:53',3),(38,'Updated task Update text formatting in comments',1,'2019-08-25 22:29:59',3),(39,'Updated task Popup with tasks of category',1,'2019-08-25 22:30:50',3),(40,'Added comment \"Show tasks\" at task Popup with tasks of category',0,'2019-08-25 22:30:56',3),(42,'Updated task Popup with tasks of category',1,'2019-08-25 22:32:54',3),(43,'Updated task Add history to user',1,'2019-08-25 22:33:56',3),(44,'Added comment \"Closed\" at task Add history to user',0,'2019-08-25 22:35:05',3),(45,'Added comment \"Saved\" at task \"Save logs in file\"',0,'2019-08-25 22:48:12',3),(46,'Created task \"Make \'Create team\' \"',0,'2019-08-26 16:50:02',3),(47,'Created task \"Make \'Create project\' \"',0,'2019-08-26 16:51:33',3),(48,'Updated task \"Save logs in file\"',1,'2019-08-26 18:05:00',3),(49,'Updated task \"Save logs in file\"',1,'2019-08-26 18:06:05',3),(50,'Created task \"[HOME]\"',0,'2019-08-26 18:59:06',3),(51,'Created task \"Create small view\"',0,'2019-08-26 20:36:37',3),(52,'Created task \"[HOME] write tests\"',0,'2019-08-27 17:26:40',3),(53,'Updated task \"Create search page\"',1,'2019-08-27 17:41:42',3),(54,'Updated task \"Create search page\"',1,'2019-08-27 17:41:44',3),(55,'Updated task \"Create search page\"',1,'2019-08-27 17:41:52',3),(56,'Updated task \"Create search page\"',1,'2019-08-27 17:41:54',3),(57,'Updated task \"Show status in search\"',1,'2019-08-27 17:42:03',3),(58,'Updated task \"Show status in search\"',1,'2019-08-27 17:42:05',3),(59,'Added comment \"Update\" at task \"Make left menu usable \"',0,'2019-08-27 17:56:48',3),(60,'Updated task \"Create projects page\"',1,'2019-08-27 17:58:59',3),(61,'Created task \"Group events in timeline by date\"',0,'2019-08-27 19:22:39',3),(62,'Updated task \"Create projects page\"',1,'2019-08-27 20:06:32',3),(63,'Updated task \"Sorting tasks page\"',1,'2019-08-27 20:06:55',3),(64,'Updated task \"Make left menu usable \"',1,'2019-08-27 20:07:10',3),(65,'Updated task \"Rearrange TaskPost\"',1,'2019-08-27 20:07:38',3),(66,'Updated task \"Rearrange TaskPost\"',1,'2019-08-27 20:07:46',3),(67,'Updated task \"Sorting tasks page\"',1,'2019-08-27 20:08:53',3),(68,'Updated task \"Create small view\"',1,'2019-08-27 21:18:30',3),(69,'Updated task \"[HOME] Redesign controllers\"',1,'2019-08-27 21:22:02',3),(70,'Added comment \"Split get controller into 3\" at task \"[HOME] Redesign controllers\"',0,'2019-08-27 21:23:21',3),(71,'Created task \"Update enums in select\"',0,'2019-08-27 21:33:09',3),(72,'Created task \"More generalization in Tasks page\"',0,'2019-08-28 16:12:07',3),(73,'Updated task \"Sorting tasks page\"',1,'2019-08-28 16:22:52',3),(74,'Updated task \"Make left menu usable \"',1,'2019-08-28 16:23:03',3),(75,'Updated task \"Update enums in select\"',1,'2019-08-28 16:25:24',3),(76,'Added comment \"Updated\" at task \"Update enums in select\"',0,'2019-08-28 16:31:25',3),(77,'Updated task \"Update enums in select\"',1,'2019-08-28 16:31:31',3),(78,'Updated task \"Drop cu categorie restransa\"',1,'2019-08-28 16:33:26',3),(79,'Created task \"Paginare in search\"',0,'2019-08-28 18:04:38',3),(80,'Updated task \"Adaugare Sortare in Search\"',1,'2019-08-28 18:47:34',3),(81,'Updated task \"Create small view\"',1,'2019-08-28 18:49:29',3),(82,'Added comment \"Posibil fix:\" at task \"Drop cu categorie restransa\"',0,'2019-08-28 20:37:20',3),(83,'Created task \"Use MAX_RESULTS from Repository\"',0,'2019-08-28 21:06:44',3),(84,'Updated task \"Paginare in search\"',1,'2019-08-28 21:18:18',3),(85,'Updated task \"Paginare in search\"',1,'2019-08-28 21:18:30',3),(86,'Updated task \"Paginare in search\"',1,'2019-08-28 21:18:44',3),(87,'Created task \"Edit number of items per page\"',0,'2019-08-28 21:22:15',3),(88,'Created task \"Edit number of items per page\"',0,'2019-08-28 21:25:03',3),(89,'Created task \"Deadline problem in create task\"',0,'2019-08-28 21:27:24',3);
/*!40000 ALTER TABLE `user_event` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'teams'
--

--
-- Dumping routines for database 'teams'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-08-29  8:39:04
