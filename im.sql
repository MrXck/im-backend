/*
Navicat MySQL Data Transfer

Source Server         : 127.0.0.1
Source Server Version : 50644
Source Host           : localhost:3306
Source Database       : im

Target Server Type    : MYSQL
Target Server Version : 50644
File Encoding         : 65001

Date: 2022-07-24 11:56:03
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for address_book
-- ----------------------------
DROP TABLE IF EXISTS `address_book`;
CREATE TABLE `address_book` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `applicant` int(11) NOT NULL,
  `respondent` int(11) NOT NULL,
  `is_agree` int(11) NOT NULL,
  `reason` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `address_book_user_id` (`applicant`),
  KEY `address_book_user_id1` (`respondent`),
  CONSTRAINT `address_book_user_id` FOREIGN KEY (`applicant`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `address_book_user_id1` FOREIGN KEY (`respondent`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for historical_chat
-- ----------------------------
DROP TABLE IF EXISTS `historical_chat`;
CREATE TABLE `historical_chat` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `with_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `historical_chat_user_id` (`user_id`),
  KEY `historical_chat_user_id1` (`with_id`),
  CONSTRAINT `historical_chat_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `historical_chat_user_id1` FOREIGN KEY (`with_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `from_id` int(11) NOT NULL,
  `to_id` int(11) NOT NULL,
  `message` varchar(256) NOT NULL,
  `time` datetime NOT NULL,
  `type` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `message_user_id` (`from_id`),
  KEY `message_user_id1` (`to_id`),
  CONSTRAINT `message_user_id` FOREIGN KEY (`from_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `message_user_id1` FOREIGN KEY (`to_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(32) NOT NULL,
  `password` varchar(32) NOT NULL,
  `avatar` varchar(256) DEFAULT NULL,
  `phone` varchar(11) NOT NULL,
  `synopsis` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;
