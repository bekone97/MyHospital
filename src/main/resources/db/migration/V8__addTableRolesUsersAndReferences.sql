CREATE TABLE roles_users
(
    id    bigint primary key auto_increment,
    role_id BIGINT NOT NULL,
    user_id bigint NOT NULl
);
ALTER TABLE roles_users ADD
    CONSTRAINT users_roles_fk_for_roles
        FOREIGN KEY (role_id) REFERENCES roles(id);
ALTER TABLE roles_users ADD
    CONSTRAINT users_roles_fk_for_users
        FOREIGN KEY (user_id) REFERENCES users(id);
