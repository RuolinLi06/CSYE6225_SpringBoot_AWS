CREATE DATABASE IF NOT EXISTS `neu6225`;
USE `neu6225`;
-- ----------------------------
-- Table structure for user_account
-- ----------------------------
-- DROP TABLE IF EXISTS  `user_account`;
CREATE TABLE IF NOT EXISTS`user_account`  (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `first_name` varchar(50) NOT NULL,
    `last_name` varchar(50)  NOT NULL,
    `password` varchar(255) NOT NULL,
    `username` varchar(50) NOT NULL,
    `account_created` timestamp NOT NULL,
    `account_updated` timestamp NOT NULL,
    PRIMARY KEY (`id`) USING BTREE
    );

-- DROP TABLE IF EXISTS `product`;
CREATE TABLE IF NOT EXISTS `product`  (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` varchar(50) NOT NULL,
    `description` varchar(255)  NOT NULL,
    `sku` varchar(255) NOT NULL UNIQUE,
    `manufacturer` varchar(50) NOT NULL,
    `quantity` BIGINT NOT NULL,
    `date_added` timestamp NOT NULL,
    `date_last_updated` timestamp NOT NULL,
    `owner_user_id` BIGINT,
    PRIMARY KEY (`id`) USING BTREE,
    CONSTRAINT `fk_owner_user_id` FOREIGN KEY (`owner_user_id`) REFERENCES `user_account` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS `image_details` (
    `image_id` BIGINT NOT NULL AUTO_INCREMENT,
    `product_id` BIGINT,
    `file_name` varchar(255),
    `date_created` timestamp,
    `s3_bucket_path` varchar(255),
    PRIMARY KEY (`image_id`) USING BTREE
    );
