-- Seed data for demo
-- VULNERABILITY: Passwords stored in plain text (no hashing)
INSERT INTO users (name, email, password, role) VALUES ('Alice Admin', 'alice@example.com', 'admin123', 'ADMIN');
INSERT INTO users (name, email, password, role) VALUES ('Bob User', 'bob@example.com', 'password', 'USER');
INSERT INTO users (name, email, password, role) VALUES ('Charlie Dev', 'charlie@example.com', 'charlie99', 'USER');
INSERT INTO users (name, email, password, role) VALUES ('Diana Ops', 'diana@example.com', 'diana2024', 'USER');
INSERT INTO users (name, email, password, role) VALUES ('Eve Hacker', 'eve@example.com', 'letmein', 'USER');
