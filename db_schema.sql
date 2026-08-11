-- WF Reunion Database Schema
-- Run once in phpMyAdmin or MySQL CLI: source /path/to/db_schema.sql
-- Then run setup_db.php once to create the default user.

CREATE DATABASE IF NOT EXISTS wf_reunion
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE wf_reunion;

-- Users (login credentials)
CREATE TABLE IF NOT EXISTS users (
    id                 INT          AUTO_INCREMENT PRIMARY KEY,
    username           VARCHAR(50)  NOT NULL UNIQUE,
    password_hash      VARCHAR(255) NOT NULL,
    display_name       VARCHAR(100),
    email              VARCHAR(255) UNIQUE,
    email_verified     TINYINT(1)   NOT NULL DEFAULT 0,
    verification_token VARCHAR(64)  DEFAULT NULL,
    token_expires_at   DATETIME     DEFAULT NULL,
    role               ENUM('admin','member') NOT NULL DEFAULT 'member',
    created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Run these if upgrading an existing database (skip for fresh installs):
 --ALTER TABLE users ADD COLUMN email              VARCHAR(255) UNIQUE    AFTER display_name;
 --ALTER TABLE users ADD COLUMN email_verified     TINYINT(1) NOT NULL DEFAULT 0 AFTER email;
  --ALTER TABLE users ADD COLUMN verification_token VARCHAR(64) DEFAULT NULL       AFTER email_verified;
 --ALTER TABLE users ADD COLUMN token_expires_at   DATETIME   DEFAULT NULL        AFTER verification_token;

-- Uploaded photos
CREATE TABLE IF NOT EXISTS photos (
    id            INT          AUTO_INCREMENT PRIMARY KEY,
    filename      VARCHAR(255) NOT NULL,
    original_name VARCHAR(255),
    uploaded_by   INT,
    uploaded_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (uploaded_by) REFERENCES users(id) ON DELETE SET NULL
);

-- Contact form submissions
CREATE TABLE IF NOT EXISTS contact_messages (
    id           INT          AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    email        VARCHAR(255) NOT NULL,
    subject      VARCHAR(255),
    message      TEXT         NOT NULL,
    submitted_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- Meal assignment signups
CREATE TABLE IF NOT EXISTS meal_signups (
    id               INT          AUTO_INCREMENT PRIMARY KEY,
    meal             ENUM('Breakfast','Lunch','Dinner') NOT NULL,
    day              ENUM('Thursday','Friday','Saturday','Sunday') NOT NULL,
    family_name      VARCHAR(100) NOT NULL,
    menu_description VARCHAR(255) NOT NULL,
    signed_up_by     INT          DEFAULT NULL,
    created_at       TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (signed_up_by) REFERENCES users(id) ON DELETE SET NULL
);

-- API tokens for the Android app
CREATE TABLE IF NOT EXISTS api_tokens (
    id         INT         AUTO_INCREMENT PRIMARY KEY,
    user_id    INT         NOT NULL,
    token      VARCHAR(64) NOT NULL UNIQUE,
    expires_at DATETIME    NOT NULL,
    created_at TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
