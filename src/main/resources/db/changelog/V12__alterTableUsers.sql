ALTER TABLE users ADD COLUMN email varchar(528) not null;
ALTER TABLE users ADD COLUMN verification_code varchar(528) not null;
ALTER TABLE users ADD COLUMN verification_status TINYINT(1) not null;
-- ALTER TABLE USERS ADD UNIQUE (email,verification_code);