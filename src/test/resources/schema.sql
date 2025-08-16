CREATE TABLE IF NOT EXISTS member (
    member_id SERIAL PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email_address VARCHAR(255) NOT NULL,
    is_active BOOL NOT NULL
);

CREATE TABLE IF NOT EXISTS survey (
    survey_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    expected_completes VARCHAR(255) NOT NULL,
    completion_points VARCHAR(255) NOT NULL,
    filtered_points VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS status (
    status_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS participation (
    id SERIAL PRIMARY KEY,
    member_id BIGINT NOT NULL,
    survey_id BIGINT NOT NULL,
    status_id BIGINT NOT NULL,
    length INT,
    CONSTRAINT unique_member_id_survey_id_status_id UNIQUE (member_id, survey_id, status_id)
);