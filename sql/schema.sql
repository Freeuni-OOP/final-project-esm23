DROP TABLE IF EXISTS users;

CREATE TABLE users (
   id            BIGINT AUTO_INCREMENT PRIMARY KEY,
   username      VARCHAR(50)  NOT NULL UNIQUE,
   password_hash VARCHAR(255) NOT NULL,
   salt          VARCHAR(64)  NOT NULL,
   is_admin      BOOLEAN      NOT NULL DEFAULT FALSE,
   created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
);


