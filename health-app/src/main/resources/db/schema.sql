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
        FOREIGN KEY (trainerID) REFERENCES users (user_id) ON DELETE SET NULL
    );
CREATE TABLE
    IF NOT EXISTS activities (
        user_id INTEGER,
        activity VARCHAR(20) NOT NULL, --"water", "food", "exercise"
        calorie INTEGER, -- amount of calorie
        waterintake INTEGER, -- grams of waterintake
        trainee_weight DECIMAL(5, 2), --weight taken after activity, will also need to update in users
        duration INTEGER, -- exercise duration (minutes)
        input_datetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        PRIMARY KEY (user_id, input_datetime),
        FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE SET NULL
    );



