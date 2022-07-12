ALTER TABLE persons ADD COLUMN key_for_user VARCHAR(528);
ALTER TABLE users ADD COLUMN
    authentication_status TINYINT(1) DEFAULT 0;
ALTER TABLE users
DROP CONSTRAINT users_roles_fk;
ALTER TABLE users
DROP COLUMN role_id;
ALTER table persons ADD UNIQUE (key_for_user);
