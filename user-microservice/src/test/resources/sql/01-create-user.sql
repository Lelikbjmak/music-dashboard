delete from users_roles;
delete from roles;
delete from users;

insert into roles values
(1, 'ROLE_ADMIN');

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

insert into users values (
    1, -- PK
    'testUser',
    'test@gmail.com',
    NOW(),
    NOW(),
    1, -- account non expired
    1, -- account non locked
    1, -- credentials non expired
    1, -- enabled
    '$argon2id$v=19$m=23552,t=2,p=1$xnr/bs027Dlc/fVMbrapcRU$R5NvPkutHQfnx5fG6FTt3iaUMIu0i/5x8CMpiNekgGs'
);

insert into users_roles values
(1, 1);