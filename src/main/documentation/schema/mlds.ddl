-- MySQL dump 10.13  Distrib 8.0.31, for Win64 (x86_64)
--
-- Host: localhost    Database: mlds
-- ------------------------------------------------------
-- Server version	8.0.31

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
-- Table structure for table `affiliate`
--

DROP TABLE IF EXISTS `affiliate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `affiliate` (
  `affiliate_id` bigint NOT NULL COMMENT 'A String',
  `created` timestamp NULL DEFAULT NULL,
  `creator` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `application_id` bigint DEFAULT NULL,
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `affiliate_details_id` bigint DEFAULT NULL,
  `home_member_id` bigint NOT NULL,
  `import_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `notes_internal` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,
  `standing_state` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `inactive_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`affiliate_id`),
  UNIQUE KEY `import_key` (`import_key`,`home_member_id`),
  KEY `FK_licensee_application` (`application_id`),
  KEY `FK_affiliate_affiliate_details` (`affiliate_details_id`),
  KEY `affiliate_home_member` (`home_member_id`),
  CONSTRAINT `affiliate_home_member` FOREIGN KEY (`home_member_id`) REFERENCES `member` (`member_id`),
  CONSTRAINT `FK_affiliate_affiliate_details` FOREIGN KEY (`affiliate_details_id`) REFERENCES `affiliate_details` (`affiliate_details_id`),
  CONSTRAINT `FK_licensee_application` FOREIGN KEY (`application_id`) REFERENCES `application` (`application_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `affiliate_details`
--

DROP TABLE IF EXISTS `affiliate_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `affiliate_details` (
  `affiliate_details_id` bigint NOT NULL,
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `other_text` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `subtype` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `first_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `last_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `alternate_email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `third_email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `street` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `city` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `post` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `billing_street` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `billing_city` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `billing_post` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `country_iso_code_2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `billing_country_iso_code_2` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `organization_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `landline_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `landline_extension` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `mobile_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `organization_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `organization_type_other` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `agreement_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `inactive_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`affiliate_details_id`),
  KEY `FK_affiliate_details_country` (`country_iso_code_2`),
  KEY `FK_affiliate_details_billing_country` (`billing_country_iso_code_2`),
  CONSTRAINT `FK_affiliate_details_billing_country` FOREIGN KEY (`billing_country_iso_code_2`) REFERENCES `country` (`iso_code_2`),
  CONSTRAINT `FK_affiliate_details_country` FOREIGN KEY (`country_iso_code_2`) REFERENCES `country` (`iso_code_2`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `application`
--

DROP TABLE IF EXISTS `application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `application` (
  `application_id` bigint NOT NULL,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `street` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `city` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `country` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `landline_extension` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `full_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `landline_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `snomedlicense` bit(1) NOT NULL DEFAULT b'0',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `alternate_email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `third_email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `mobile_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `organization_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `organization_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `billing_street` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `billing_city` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `billing_country` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `billing_post_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `post_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `organization_type_other` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `submitted_at` timestamp NULL DEFAULT NULL,
  `notes_internal` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,
  `completed_at` timestamp NULL DEFAULT NULL,
  `approval_state` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT 'NOT_SUBMITTED',
  `commercial_usage_id` bigint DEFAULT NULL,
  `affiliate_details_id` bigint DEFAULT NULL,
  `member_id` bigint NOT NULL,
  `application_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,
  `affiliate_id` bigint DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `inactive_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`application_id`),
  KEY `FK_application_commercial_usage` (`commercial_usage_id`),
  KEY `application_member` (`member_id`),
  KEY `FK_application_affiliate` (`affiliate_id`),
  KEY `FK_application_affiliate_details` (`affiliate_details_id`),
  CONSTRAINT `application_member` FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`),
  CONSTRAINT `FK_application_affiliate` FOREIGN KEY (`affiliate_id`) REFERENCES `affiliate` (`affiliate_id`),
  CONSTRAINT `FK_application_affiliate_details` FOREIGN KEY (`affiliate_details_id`) REFERENCES `affiliate_details` (`affiliate_details_id`),
  CONSTRAINT `FK_application_commercial_usage` FOREIGN KEY (`commercial_usage_id`) REFERENCES `commercial_usage` (`commercial_usage_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `commercial_usage`
--

DROP TABLE IF EXISTS `commercial_usage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `commercial_usage` (
  `commercial_usage_id` bigint NOT NULL,
  `created` timestamp NULL DEFAULT NULL,
  `key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `note` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,
  `state` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT 'NOT_SUBMITTED',
  `submitted` timestamp NULL DEFAULT NULL,
  `affiliate_id` bigint DEFAULT NULL COMMENT 'A String',
  `current_usage` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,
  `planned_usage` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,
  `purpose` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `implementation_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `effective_to` timestamp NULL DEFAULT NULL,
  `other_activities` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,
  `inactive_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`commercial_usage_id`),
  KEY `FK_commercial_usage_licensee` (`affiliate_id`),
  CONSTRAINT `FK_commercial_usage_licensee` FOREIGN KEY (`affiliate_id`) REFERENCES `affiliate` (`affiliate_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `commercial_usage_count`
--

DROP TABLE IF EXISTS `commercial_usage_count`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `commercial_usage_count` (
  `commercial_usage_count_id` bigint NOT NULL,
  `commercial_usage_id` bigint DEFAULT NULL,
  `created` timestamp NULL DEFAULT NULL,
  `country_iso_code_2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,
  `snomed_practices` int DEFAULT NULL,
  `hospitals_staffing_practices` int DEFAULT NULL,
  `data_creation_practices_not_part_of_hospital` int DEFAULT NULL,
  `non_practice_data_creation_systems` int DEFAULT NULL,
  `deployed_data_analysis_systems` int DEFAULT NULL,
  `databases_per_deployment` int DEFAULT NULL,
  `inactive_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`commercial_usage_count_id`),
  KEY `FK_commercial_usage_count_commercial_usage` (`commercial_usage_id`),
  KEY `FK_commercial_usage_count_country` (`country_iso_code_2`),
  CONSTRAINT `FK_commercial_usage_count_commercial_usage` FOREIGN KEY (`commercial_usage_id`) REFERENCES `commercial_usage` (`commercial_usage_id`),
  CONSTRAINT `FK_commercial_usage_count_country` FOREIGN KEY (`country_iso_code_2`) REFERENCES `country` (`iso_code_2`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `commercial_usage_entry`
--

DROP TABLE IF EXISTS `commercial_usage_entry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `commercial_usage_entry` (
  `commercial_usage_entry_id` bigint NOT NULL,
  `created` timestamp NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `country_iso_code_2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `commercial_usage_id` bigint DEFAULT NULL,
  `note` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,
  `inactive_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`commercial_usage_entry_id`),
  KEY `FK_commercial_usage_entry_country` (`country_iso_code_2`),
  KEY `FK_commercial_usage_entry_commercial_usage` (`commercial_usage_id`),
  CONSTRAINT `FK_commercial_usage_entry_commercial_usage` FOREIGN KEY (`commercial_usage_id`) REFERENCES `commercial_usage` (`commercial_usage_id`),
  CONSTRAINT `FK_commercial_usage_entry_country` FOREIGN KEY (`country_iso_code_2`) REFERENCES `country` (`iso_code_2`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `country`
--

DROP TABLE IF EXISTS `country`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `country` (
  `iso_code_2` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `iso_code_3` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `common_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `exclude_usage` bit(1) DEFAULT b'0',
  `alternate_registration_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `member_id` bigint DEFAULT NULL,
  PRIMARY KEY (`iso_code_2`),
  KEY `country_member` (`member_id`),
  CONSTRAINT `country_member` FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `databasechangelog`
--

DROP TABLE IF EXISTS `databasechangelog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `databasechangelog` (
  `ID` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `AUTHOR` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `FILENAME` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `DATEEXECUTED` datetime NOT NULL,
  `ORDEREXECUTED` int NOT NULL,
  `EXECTYPE` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `MD5SUM` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `DESCRIPTION` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `COMMENTS` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `TAG` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `LIQUIBASE` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `CONTEXTS` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `LABELS` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `DEPLOYMENT_ID` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `databasechangeloglock`
--

DROP TABLE IF EXISTS `databasechangeloglock`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `databasechangeloglock` (
  `ID` int NOT NULL,
  `LOCKED` bit(1) NOT NULL,
  `LOCKGRANTED` datetime DEFAULT NULL,
  `LOCKEDBY` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `email_domain_blacklist`
--

DROP TABLE IF EXISTS `email_domain_blacklist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `email_domain_blacklist` (
  `domain_id` bigint NOT NULL AUTO_INCREMENT,
  `domainname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  PRIMARY KEY (`domain_id`)
) ENGINE=InnoDB AUTO_INCREMENT=386731 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `event`
--

DROP TABLE IF EXISTS `event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `event` (
  `event_id` bigint NOT NULL,
  `type` varchar(31) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `description` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `timestamp` timestamp NOT NULL,
  `event_sub_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `principal` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `browser_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `browser_version` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `ip_address` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `locale` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `session_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `user_agent` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  PRIMARY KEY (`event_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `file`
--

DROP TABLE IF EXISTS `file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `file` (
  `file_id` bigint NOT NULL,
  `filename` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  `creator` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `mimetype` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `content` longblob,
  PRIMARY KEY (`file_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hibernate_sequence`
--

DROP TABLE IF EXISTS `hibernate_sequence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hibernate_sequence` (
  `sequence_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `next_val` bigint DEFAULT '1',
  PRIMARY KEY (`sequence_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `member`
--

DROP TABLE IF EXISTS `member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `member` (
  `member_id` bigint NOT NULL,
  `key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `license_file` bigint DEFAULT NULL,
  `license_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `license_version` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `logo_file` bigint DEFAULT NULL,
  `staff_notification_email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `promote_packages` bit(1) NOT NULL DEFAULT b'0',
  `contactEmail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `memberOrgName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `memberOrgURL` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  PRIMARY KEY (`member_id`),
  UNIQUE KEY `member_name_unique` (`key`),
  KEY `member_file` (`license_file`),
  KEY `logo_file` (`logo_file`),
  CONSTRAINT `logo_file` FOREIGN KEY (`logo_file`) REFERENCES `file` (`file_id`),
  CONSTRAINT `member_file` FOREIGN KEY (`license_file`) REFERENCES `file` (`file_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `password_reset_token`
--

DROP TABLE IF EXISTS `password_reset_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `password_reset_token` (
  `password_reset_token_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `created` timestamp NULL DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`password_reset_token_id`),
  KEY `fk_password_reset_token_user_id` (`user_id`),
  CONSTRAINT `fk_password_reset_token_user_id` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `release_file`
--

DROP TABLE IF EXISTS `release_file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `release_file` (
  `release_file_id` bigint NOT NULL,
  `release_version_id` bigint DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `label` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `download_url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,
  `primary_file` bit(1) NOT NULL DEFAULT b'0',
  `md5_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `file_size` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  PRIMARY KEY (`release_file_id`),
  KEY `FK_release_file_release_version` (`release_version_id`),
  CONSTRAINT `FK_release_file_release_version` FOREIGN KEY (`release_version_id`) REFERENCES `release_version` (`release_version_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `release_package`
--

DROP TABLE IF EXISTS `release_package`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `release_package` (
  `release_package_id` bigint NOT NULL,
  `created_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,
  `inactive_at` timestamp NULL DEFAULT NULL,
  `member_id` bigint NOT NULL,
  `licence_file` bigint DEFAULT NULL,
  `priority` int DEFAULT '-1',
  `copyrights` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,
  `releasePackageURI` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`release_package_id`),
  KEY `FK_package_t_user` (`created_by`),
  KEY `release_package_member` (`member_id`),
  KEY `licence_file` (`licence_file`),
  CONSTRAINT `licence_file` FOREIGN KEY (`licence_file`) REFERENCES `file` (`file_id`),
  CONSTRAINT `release_package_member` FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `release_version`
--

DROP TABLE IF EXISTS `release_version`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `release_version` (
  `release_version_id` bigint NOT NULL,
  `release_package_id` bigint DEFAULT NULL,
  `created_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,
  `published_at` date DEFAULT NULL,
  `online` bit(1) NOT NULL DEFAULT b'0',
  `inactive_at` timestamp NULL DEFAULT NULL,
  `release_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `summary` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,
  `versionDependentDerivativeURI` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `versionDependentURI` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `versionURI` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  `package_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `archive` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`release_version_id`),
  KEY `FK_release_version_release_package` (`release_package_id`),
  KEY `FK_release_version_t_user` (`created_by`),
  CONSTRAINT `FK_release_version_release_package` FOREIGN KEY (`release_package_id`) REFERENCES `release_package` (`release_package_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_authority`
--

DROP TABLE IF EXISTS `t_authority`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_authority` (
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_persistent_audit_event`
--

DROP TABLE IF EXISTS `t_persistent_audit_event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_persistent_audit_event` (
  `event_id` bigint NOT NULL,
  `principal` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `event_date` timestamp NULL DEFAULT NULL,
  `event_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `application_id` bigint DEFAULT NULL,
  `affiliate_id` bigint DEFAULT NULL,
  `release_package_id` bigint DEFAULT NULL,
  `release_version_id` bigint DEFAULT NULL,
  `release_file_id` bigint DEFAULT NULL,
  `commercial_usage_id` bigint DEFAULT NULL,
  PRIMARY KEY (`event_id`),
  KEY `idx_persistent_audit_event` (`principal`,`event_date`),
  KEY `FK_t_persistent_audit_event_application` (`application_id`),
  KEY `FK_t_persistent_audit_event_affiliate` (`affiliate_id`),
  KEY `FK_t_persistent_audit_event_release_package` (`release_package_id`),
  KEY `FK_t_persistent_audit_event_release_version` (`release_version_id`),
  KEY `FK_t_persistent_audit_event_release_file` (`release_file_id`),
  KEY `FK_t_persistent_audit_event_commercial_usage` (`commercial_usage_id`),
  CONSTRAINT `FK_t_persistent_audit_event_commercial_usage` FOREIGN KEY (`commercial_usage_id`) REFERENCES `commercial_usage` (`commercial_usage_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_persistent_audit_event_data`
--

DROP TABLE IF EXISTS `t_persistent_audit_event_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_persistent_audit_event_data` (
  `event_id` bigint NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  PRIMARY KEY (`event_id`,`name`),
  KEY `idx_persistent_audit_event_data` (`event_id`),
  KEY `idx_persistent_audit_event_data_name` (`name`),
  CONSTRAINT `FK_event_persistent_audit_event_data` FOREIGN KEY (`event_id`) REFERENCES `t_persistent_audit_event` (`event_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_persistent_token`
--

DROP TABLE IF EXISTS `t_persistent_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_persistent_token` (
  `series` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `token_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `token_date` date DEFAULT NULL,
  `ip_address` varchar(39) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `user_agent` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`series`),
  KEY `fk_persistent_token_user_id` (`user_id`),
  CONSTRAINT `fk_persistent_token_user_id` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_user`
--

DROP TABLE IF EXISTS `t_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user` (
  `login` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `first_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `last_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `activated` bit(1) NOT NULL DEFAULT b'1',
  `lang_key` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `activation_key` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `created_by` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT 'system',
  `created_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_modified_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `last_modified_date` timestamp NULL DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `inactive_at` timestamp NULL DEFAULT NULL,
  `accept_notifications` bit(1) NOT NULL DEFAULT b'1',
  `country_notifications_only` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `idx_unique_active_login` ((lower(`login`)),((case when ((`inactive_at` is null) and (`inactive_at` < _utf8mb4'2000-01-01')) then coalesce(`inactive_at`,_utf8mb4'2000-01-01') end)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_user_authority`
--

DROP TABLE IF EXISTS `t_user_authority`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_authority` (
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`user_id`,`name`),
  KEY `fk_authority_name` (`name`),
  CONSTRAINT `fk_authority_name` FOREIGN KEY (`name`) REFERENCES `t_authority` (`name`),
  CONSTRAINT `fk_user_authority_user_id` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_registration`
--

DROP TABLE IF EXISTS `user_registration`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_registration` (
  `user_registration_id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  PRIMARY KEY (`user_registration_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary view structure for view `vw_affiliate_approved_application_countries`
--

DROP TABLE IF EXISTS `vw_affiliate_approved_application_countries`;
/*!50001 DROP VIEW IF EXISTS `vw_affiliate_approved_application_countries`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `vw_affiliate_approved_application_countries` AS SELECT
 1 AS `affiliate_id`,
 1 AS `countries`*/;
SET character_set_client = @saved_cs_client;

--
-- Final view structure for view `vw_affiliate_approved_application_countries`
--

/*!50001 DROP VIEW IF EXISTS `vw_affiliate_approved_application_countries`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`mlds`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `vw_affiliate_approved_application_countries` AS select `a`.`affiliate_id` AS `affiliate_id`,group_concat(`c`.`common_name` separator ' ') AS `countries` from ((`application` `a` join `member` `m` on((`m`.`member_id` = `a`.`member_id`))) join `country` `c` on((`c`.`iso_code_2` = `m`.`key`))) where (`a`.`approval_state` = 'APPROVED') group by `a`.`affiliate_id` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed
