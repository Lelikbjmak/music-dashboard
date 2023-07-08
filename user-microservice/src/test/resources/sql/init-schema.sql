CREATE TABLE IF NOT EXISTS users (
    id bigint AUTO_INCREMENT NOT NULL,
    username varchar(25),
    email varchar(45),
    last_time_updated datetime(6),
    registration_date datetime(6),
    account_non_expired bit NOT NULL,
    account_non_locked bit NOT NULL,
    credentials_non_expired bit NOT NULL,
    enabled bit NOT NULL,
    password varchar(255),
    CONSTRAINT users_pk PRIMARY KEY (id),
    CONSTRAINT users_mail_uk UNIQUE (email),
    CONSTRAINT users_username_uk UNIQUE (username)
);

CREATE TABLE IF NOT EXISTS roles (
    id int AUTO_INCREMENT NOT NULL,
    name varchar(25) NOT NULL,
    CONSTRAINT roles_pk PRIMARY KEY (id),
    CONSTRAINT roles_name_uk UNIQUE (name)
);

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
);


