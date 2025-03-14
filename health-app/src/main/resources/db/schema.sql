-- users table
CREATE TABLE
    IF NOT EXISTS users (
        user_id INTEGER PRIMARY KEY AUTOINCREMENT,
        password VARCHAR(255) NOT NULL,
        username VARCHAR(100) NOT NULL,
        email VARCHAR(100) UNIQUE NOT NULL,
        weight DECIMAL(5, 2),
        height DECIMAL(5, 2),
        gender VARCHAR(6) NOT NULL, -- 'Male', 'Female'
        trainerID INTEGER,
        role VARCHAR(10) NOT NULL, -- 'admin', 'trainer', 'trainee'
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (trainerID) REFERENCES users (userID) ON DELETE SET NULL
    );