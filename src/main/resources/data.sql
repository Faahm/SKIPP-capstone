INSERT INTO users (username, password_hash, role) VALUES ('admin', '{noop}password', 'FACULTY');

INSERT INTO students (student_number, first_name, last_name) VALUES (2001, 'Emily', 'Johnson');
INSERT INTO students (student_number, first_name, last_name) VALUES (2002, 'David', 'Smith');
INSERT INTO students (student_number, first_name, last_name) VALUES (2003, 'Sarah', 'Lee');

INSERT INTO faculty (faculty_number, first_name, last_name) VALUES (1001, 'John', 'Anderson');
INSERT INTO faculty (faculty_number, first_name, last_name) VALUES (1002, 'Samantha', 'Williams');
INSERT INTO faculty (faculty_number, first_name, last_name) VALUES (1003, 'Michael', 'Roberts');

INSERT INTO users (username, password_hash, role) VALUES ('ST-2001', '{noop}EmilyJohnson', 'STUDENT');
INSERT INTO users (username, password_hash, role) VALUES ('ST-2002', '{noop}DavidSmith', 'STUDENT');
INSERT INTO users (username, password_hash, role) VALUES ('ST-2003', '{noop}Sarah Lee', 'STUDENT');
INSERT INTO users (username, password_hash, role) VALUES ('FC-1001', '{noop}JohnAnderson', 'FACULTY');
INSERT INTO users (username, password_hash, role) VALUES ('FC-1002', '{noop}SamanthaWilliams', 'FACULTY');
INSERT INTO users (username, password_hash, role) VALUES ('FC-1003', '{noop}MichaelRoberts', 'FACULTY');

INSERT INTO subjects VALUES ('ThesisWriting1');
INSERT INTO subjects VALUES ('SKIPP');

INSERT INTO rooms (name, capacity) VALUES ('F612', 40);
INSERT INTO rooms (name, capacity) VALUES ('F601', 30);
INSERT INTO rooms (name, capacity) VALUES ('F600', 20);

INSERT INTO sections (section_id, subject_id, schedule, room_name, faculty_number) VALUES ('CS4A', 'SKIPP', 'TF 10:30-18:00', 'F601', 1001);