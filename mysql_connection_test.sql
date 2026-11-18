CREATE DATABASE IF NOT EXISTS connection_test
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE connection_test;

CREATE TABLE IF NOT EXISTS test_users (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_test_users_username (username),
    UNIQUE KEY uq_test_users_email (email)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS connection_checks (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id INT UNSIGNED NOT NULL,
    check_message VARCHAR(255) NOT NULL,
    checked_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_connection_checks_user
        FOREIGN KEY (user_id) REFERENCES test_users (id)
) ENGINE = InnoDB;

INSERT INTO test_users (username, email)
VALUES ('connection_test_user', 'connection-test@example.com')
ON DUPLICATE KEY UPDATE id = LAST_INSERT_ID(id);

INSERT INTO connection_checks (user_id, check_message)
VALUES (LAST_INSERT_ID(), 'MySQL connection and table write succeeded');

SELECT
    u.username,
    u.email,
    c.check_message,
    c.checked_at
FROM test_users AS u
JOIN connection_checks AS c ON c.user_id = u.id
WHERE u.username = 'connection_test_user'
ORDER BY c.checked_at DESC
LIMIT 1;
