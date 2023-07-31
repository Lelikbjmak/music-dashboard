CREATE TABLE IF NOT EXISTS users_roles (
    user_id bigint,
    role_id int,
    CONSTRAINT users_roles_pkey PRIMARY KEY (user_id, role_id),
    CONSTRAINT users_roles_user_id_fk FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON UPDATE NO ACTION
        ON DELETE CASCADE,
    CONSTRAINT users_roles_role_id_fk FOREIGN KEY (role_id)
        REFERENCES roles (id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
GO