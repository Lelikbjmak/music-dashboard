CREATE TABLE IF NOT EXISTS roles (
    id int AUTO_INCREMENT NOT NULL,
    name varchar(25) NOT NULL,
    CONSTRAINT roles_pk PRIMARY KEY (id),
    CONSTRAINT roles_name_uk UNIQUE (name)
)
GO