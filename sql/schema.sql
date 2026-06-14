DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS quizes;

CREATE TABLE users (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    salt          VARCHAR(64)  NOT NULL,
    is_admin      BOOLEAN NOT NULL DEFAULT FALSE,
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE quizzes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    creator_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    random_order BOOLEAN NOT NULL DEFAULT FALSE,
    one_page BOOLEAN NOT NULL DEFAULT TRUE,
    immediate_correction BOOLEAN NOT NULL DEFAULT FALSE,
    practice_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (creator_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quiz_id BIGINT NOT NULL,
    question_type ENUM('QUESTION_RESPONSE', 'FILL_BLANK', 'MULTIPLE_CHOICE', 'PICTURE_RESPONSE') NOT NULL,
    question_text TEXT NOT NULL,
    image_url VARCHAR(500),
    position INT NOT NULL DEFAULT 0, -- display order when random_order (in quizzes table) is off
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE
);