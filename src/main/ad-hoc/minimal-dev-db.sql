use mlds;

-- Inserting data into authority table
INSERT INTO authority (name) VALUES ('ROLE_ADMIN');
INSERT INTO authority (name) VALUES ('ROLE_USER');
INSERT INTO authority (name) VALUES ('ROLE_STAFF');
INSERT INTO authority (name) VALUES ('ROLE_STAFF_SE');
INSERT INTO authority (name) VALUES ('ROLE_STAFF_IHTSDO');


-- Inserting data into user table
INSERT INTO user (login, user_id, password, first_name, last_name, email, activated, lang_key, activation_key, created_by, created_date, last_modified_by, last_modified_date)
VALUES
    ('system', 1, '572d3b834f32347527d749bc1a41042c920682fc430febd380b4b6a0134f314fd381ce11c9a05abe', NULL, 'System', NULL, true, 'en', NULL, 'system', '2014-06-24 12:29:08', NULL, NULL),
    ('anonymousUser', 2, '4f54479f8290dfd503b72a654faf5d70593eab443993d87a79e14e5f7cda3eb7988423aa99090c9b', 'Anonymous', 'User', NULL, true, 'en', NULL, 'system', '2014-06-24 12:29:08', NULL, NULL),
    ('admin', 3, 'b8f57d6d6ec0a60dfe2e20182d4615b12e321cad9e2979e0b9f81e0d6eda78ad9b6dcfe53e4e22d1', NULL, 'Administrator', NULL, true, 'en', NULL, 'system', '2014-06-24 12:29:08', NULL, NULL),
    ('user', 4, '4f54479f8290dfd503b72a654faf5d70593eab443993d87a79e14e5f7cda3eb7988423aa99090c9b', NULL, 'User', NULL, true, 'en', NULL, 'system', '2014-06-24 12:29:08', NULL, NULL),
    ('staff', 5, 'edca6651ee089a1e0b9320988f72e62dc5339f90dcc6dbbfd82a3127d2727610b52d2b0743d9b7ff', NULL, NULL, NULL, true, NULL, NULL, 'system', '2014-07-25 10:27:30', NULL, NULL),
    ('sweden', 6, 'e6b4dc6acaa889021ab202e417245c0880c66b254ad5758a298d310d15d38f1385bc0d82fc6f5e62', NULL, NULL, NULL, true, NULL, NULL, 'system', '2014-07-25 10:27:30', NULL, NULL);


-- Inserting data into user_authority table
INSERT INTO user_authority (user_id, name)
VALUES
    (1, 'ROLE_ADMIN'),
    (1, 'ROLE_USER'),
    (3, 'ROLE_ADMIN'),
    (3, 'ROLE_USER'),
    (4, 'ROLE_USER'),
    (5, 'ROLE_STAFF'),
    (5, 'ROLE_STAFF_IHTSDO'),
    (6, 'ROLE_STAFF'),
    (6, 'ROLE_STAFF_SE');


