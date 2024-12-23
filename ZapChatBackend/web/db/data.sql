-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               8.0.37 - MySQL Community Server - GPL
-- Server OS:                    Win64
-- HeidiSQL Version:             12.7.0.6850
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for zapchat
CREATE DATABASE IF NOT EXISTS `zapchat` /*!40100 DEFAULT CHARACTER SET utf8mb3 */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `zapchat`;

-- Dumping structure for table zapchat.file
CREATE TABLE IF NOT EXISTS `file` (
  `id` int NOT NULL AUTO_INCREMENT,
  `path` text NOT NULL,
  `single_chat_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_file_single_chat1_idx` (`single_chat_id`),
  CONSTRAINT `fk_file_single_chat1` FOREIGN KEY (`single_chat_id`) REFERENCES `single_chat` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- Dumping data for table zapchat.file: ~0 rows (approximately)

-- Dumping structure for table zapchat.group_chat
CREATE TABLE IF NOT EXISTS `group_chat` (
  `id` int NOT NULL AUTO_INCREMENT,
  `message_status_id` int NOT NULL,
  `datetime` datetime NOT NULL,
  `message_content_type_id` int NOT NULL,
  `group_member_id` int NOT NULL,
  `group_table_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_group_message_message_status1_idx` (`message_status_id`),
  KEY `fk_group_message_message_content_type1_idx` (`message_content_type_id`),
  KEY `fk_group_chat_group_member1_idx` (`group_member_id`),
  KEY `fk_group_chat_group_table1_idx` (`group_table_id`),
  CONSTRAINT `fk_group_chat_group_member1` FOREIGN KEY (`group_member_id`) REFERENCES `group_member` (`id`),
  CONSTRAINT `fk_group_chat_group_table1` FOREIGN KEY (`group_table_id`) REFERENCES `group_table` (`id`),
  CONSTRAINT `fk_group_message_message_content_type1` FOREIGN KEY (`message_content_type_id`) REFERENCES `message_content_type` (`id`),
  CONSTRAINT `fk_group_message_message_status1` FOREIGN KEY (`message_status_id`) REFERENCES `message_status` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table zapchat.group_chat: ~2 rows (approximately)
INSERT INTO `group_chat` (`id`, `message_status_id`, `datetime`, `message_content_type_id`, `group_member_id`, `group_table_id`) VALUES
	(1, 1, '2024-10-15 09:51:39', 1, 1, 1),
	(2, 1, '2024-10-15 10:03:15', 1, 2, 2),
	(3, 1, '2024-10-15 10:03:40', 1, 2, 2);

-- Dumping structure for table zapchat.group_chat_read
CREATE TABLE IF NOT EXISTS `group_chat_read` (
  `id` int NOT NULL AUTO_INCREMENT,
  `group_member_id` int NOT NULL,
  `group_chat_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_group_chat_read_group_member1_idx` (`group_member_id`),
  KEY `fk_group_chat_read_group_chat1_idx` (`group_chat_id`),
  CONSTRAINT `fk_group_chat_read_group_chat1` FOREIGN KEY (`group_chat_id`) REFERENCES `group_chat` (`id`),
  CONSTRAINT `fk_group_chat_read_group_member1` FOREIGN KEY (`group_member_id`) REFERENCES `group_member` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table zapchat.group_chat_read: ~0 rows (approximately)
INSERT INTO `group_chat_read` (`id`, `group_member_id`, `group_chat_id`) VALUES
	(1, 3, 1);

-- Dumping structure for table zapchat.group_file
CREATE TABLE IF NOT EXISTS `group_file` (
  `id` int NOT NULL AUTO_INCREMENT,
  `path` text NOT NULL,
  `group_chat_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_group_file_group_chat1_idx` (`group_chat_id`),
  CONSTRAINT `fk_group_file_group_chat1` FOREIGN KEY (`group_chat_id`) REFERENCES `group_chat` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- Dumping data for table zapchat.group_file: ~0 rows (approximately)

-- Dumping structure for table zapchat.group_member
CREATE TABLE IF NOT EXISTS `group_member` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `group_member_role_id` int NOT NULL,
  `group_table_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_user_has_group_user1_idx` (`user_id`),
  KEY `fk_group_member_group_member_role1_idx` (`group_member_role_id`),
  KEY `fk_group_member_group_table1_idx` (`group_table_id`),
  CONSTRAINT `fk_group_member_group_member_role1` FOREIGN KEY (`group_member_role_id`) REFERENCES `group_member_role` (`id`),
  CONSTRAINT `fk_group_member_group_table1` FOREIGN KEY (`group_table_id`) REFERENCES `group_table` (`id`),
  CONSTRAINT `fk_user_has_group_user1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table zapchat.group_member: ~2 rows (approximately)
INSERT INTO `group_member` (`id`, `user_id`, `group_member_role_id`, `group_table_id`) VALUES
	(1, 1, 1, 1),
	(2, 3, 1, 2),
	(3, 3, 2, 1);

-- Dumping structure for table zapchat.group_member_role
CREATE TABLE IF NOT EXISTS `group_member_role` (
  `id` int NOT NULL AUTO_INCREMENT,
  `role` varchar(45) NOT NULL COMMENT 'admin,member',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table zapchat.group_member_role: ~2 rows (approximately)
INSERT INTO `group_member_role` (`id`, `role`) VALUES
	(1, 'Admin'),
	(2, 'Member');

-- Dumping structure for table zapchat.group_message
CREATE TABLE IF NOT EXISTS `group_message` (
  `id` int NOT NULL AUTO_INCREMENT,
  `message` text NOT NULL,
  `group_chat_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_group_message_group_chat1_idx` (`group_chat_id`),
  CONSTRAINT `fk_group_message_group_chat1` FOREIGN KEY (`group_chat_id`) REFERENCES `group_chat` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table zapchat.group_message: ~2 rows (approximately)
INSERT INTO `group_message` (`id`, `message`, `group_chat_id`) VALUES
	(1, 'Welcome to Chemistry', 1),
	(2, 'Welcome to Friends Forever', 2),
	(3, 'helo', 3);

-- Dumping structure for table zapchat.group_table
CREATE TABLE IF NOT EXISTS `group_table` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `image_path` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table zapchat.group_table: ~2 rows (approximately)
INSERT INTO `group_table` (`id`, `name`, `image_path`) VALUES
	(1, 'Chemistry', '/group-images/1728966098286.jpeg'),
	(2, 'Friends Forever', '/group-images/1728966795351.jpeg');

-- Dumping structure for table zapchat.message
CREATE TABLE IF NOT EXISTS `message` (
  `id` int NOT NULL AUTO_INCREMENT,
  `message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `single_chat_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_message_single_chat1_idx` (`single_chat_id`),
  CONSTRAINT `fk_message_single_chat1` FOREIGN KEY (`single_chat_id`) REFERENCES `single_chat` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table zapchat.message: ~4 rows (approximately)
INSERT INTO `message` (`id`, `message`, `single_chat_id`) VALUES
	(1, 'hello', 1),
	(2, 'ðhi', 2),
	(3, 'Hello', 3),
	(4, 'How are you ?', 4);

-- Dumping structure for table zapchat.message_content_type
CREATE TABLE IF NOT EXISTS `message_content_type` (
  `id` int NOT NULL AUTO_INCREMENT,
  `type` varchar(45) NOT NULL COMMENT 'text,file',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table zapchat.message_content_type: ~2 rows (approximately)
INSERT INTO `message_content_type` (`id`, `type`) VALUES
	(1, 'Message'),
	(2, 'File');

-- Dumping structure for table zapchat.message_status
CREATE TABLE IF NOT EXISTS `message_status` (
  `id` int NOT NULL AUTO_INCREMENT,
  `status` varchar(10) NOT NULL COMMENT 'read/not read',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table zapchat.message_status: ~2 rows (approximately)
INSERT INTO `message_status` (`id`, `status`) VALUES
	(1, 'Send'),
	(2, 'Received'),
	(3, 'Read');

-- Dumping structure for table zapchat.single_chat
CREATE TABLE IF NOT EXISTS `single_chat` (
  `id` int NOT NULL AUTO_INCREMENT,
  `from_user_id` int NOT NULL,
  `to_user_id` int NOT NULL,
  `datetime` datetime NOT NULL,
  `message_status_id` int NOT NULL,
  `message_content_type_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_chat_user1_idx` (`from_user_id`),
  KEY `fk_chat_user2_idx` (`to_user_id`),
  KEY `fk_single_chat_message_status1_idx` (`message_status_id`),
  KEY `fk_single_chat_message_content_type1_idx` (`message_content_type_id`),
  CONSTRAINT `fk_chat_user1` FOREIGN KEY (`from_user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_chat_user2` FOREIGN KEY (`to_user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_single_chat_message_content_type1` FOREIGN KEY (`message_content_type_id`) REFERENCES `message_content_type` (`id`),
  CONSTRAINT `fk_single_chat_message_status1` FOREIGN KEY (`message_status_id`) REFERENCES `message_status` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table zapchat.single_chat: ~4 rows (approximately)
INSERT INTO `single_chat` (`id`, `from_user_id`, `to_user_id`, `datetime`, `message_status_id`, `message_content_type_id`) VALUES
	(1, 3, 1, '2024-10-15 10:01:42', 3, 1),
	(2, 3, 1, '2024-10-15 10:01:59', 3, 1),
	(3, 1, 3, '2024-10-15 10:02:27', 3, 1),
	(4, 1, 3, '2024-10-15 10:06:20', 3, 1);

-- Dumping structure for table zapchat.status
CREATE TABLE IF NOT EXISTS `status` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_status_user1_idx` (`user_id`),
  CONSTRAINT `fk_status_user1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table zapchat.status: ~1 rows (approximately)
INSERT INTO `status` (`id`, `user_id`) VALUES
	(1, 3);

-- Dumping structure for table zapchat.status_item
CREATE TABLE IF NOT EXISTS `status_item` (
  `id` int NOT NULL AUTO_INCREMENT,
  `text` varchar(150) DEFAULT NULL,
  `file_path` text,
  `status_id` int NOT NULL,
  `datetime` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_status_item_status1_idx` (`status_id`),
  CONSTRAINT `fk_status_item_status1` FOREIGN KEY (`status_id`) REFERENCES `status` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table zapchat.status_item: ~0 rows (approximately)
INSERT INTO `status_item` (`id`, `text`, `file_path`, `status_id`, `datetime`) VALUES
	(1, 'hi ', '/status-images/1728967056882.jpeg', 1, '2024-10-15 10:07:37');

-- Dumping structure for table zapchat.user
CREATE TABLE IF NOT EXISTS `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `mobile` varchar(10) CHARACTER SET utf8mb3 NOT NULL,
  `first_name` varchar(45) CHARACTER SET utf8mb3 NOT NULL,
  `last_name` varchar(45) CHARACTER SET utf8mb3 NOT NULL,
  `password` varchar(20) CHARACTER SET utf8mb3 NOT NULL,
  `registered_datetime` datetime NOT NULL,
  `otp` int DEFAULT NULL,
  `about` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `profile_image` text CHARACTER SET utf8mb3,
  `user_verified_status_id` int NOT NULL,
  `user_online_status_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_user_user_verified_status1_idx` (`user_verified_status_id`),
  KEY `fk_user_user_online_status1_idx` (`user_online_status_id`),
  CONSTRAINT `fk_user_user_online_status1` FOREIGN KEY (`user_online_status_id`) REFERENCES `user_online_status` (`id`),
  CONSTRAINT `fk_user_user_verified_status1` FOREIGN KEY (`user_verified_status_id`) REFERENCES `user_verified_status` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table zapchat.user: ~2 rows (approximately)
INSERT INTO `user` (`id`, `mobile`, `first_name`, `last_name`, `password`, `registered_datetime`, `otp`, `about`, `profile_image`, `user_verified_status_id`, `user_online_status_id`) VALUES
	(1, '0717900130', 'Kamal', 'Ranawaka', 'Pasindu328@', '2024-10-04 20:33:37', 225555, 'ðok thats cool', '/profile-images/2.jpeg', 1, 2),
	(2, '0713309367', 'Wijerathna', 'Jayasundara', 'Pasindu328@', '2024-10-08 09:26:35', 1234567, 'Wijerathna Jayasundara', '/profile-images/1.jpeg', 1, 2),
	(3, '0740211671', 'Pasindu', 'Jayasundara', 'Pasindu328@', '2024-10-15 09:58:51', 65521493, 'Pasindu Jayasundara', '../assets/images/default.svg', 1, 1);

-- Dumping structure for table zapchat.user_online_status
CREATE TABLE IF NOT EXISTS `user_online_status` (
  `id` int NOT NULL AUTO_INCREMENT,
  `status` varchar(20) NOT NULL COMMENT 'online/ofline\n',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table zapchat.user_online_status: ~2 rows (approximately)
INSERT INTO `user_online_status` (`id`, `status`) VALUES
	(1, 'Online'),
	(2, 'Offline');

-- Dumping structure for table zapchat.user_verified_status
CREATE TABLE IF NOT EXISTS `user_verified_status` (
  `id` int NOT NULL AUTO_INCREMENT,
  `status` varchar(15) NOT NULL COMMENT 'verified/not verified',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table zapchat.user_verified_status: ~0 rows (approximately)
INSERT INTO `user_verified_status` (`id`, `status`) VALUES
	(1, 'Verified'),
	(2, 'Not-Verified');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
