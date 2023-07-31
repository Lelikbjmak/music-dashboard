create TABLE IF NOT EXISTS users (
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
)
GO
