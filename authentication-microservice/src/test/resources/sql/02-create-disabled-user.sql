insert into roles values
(1, 'ROLE_ADMIN');

insert into users values (
    1, -- PK
    'test',
    'test@gmail.com',
    NOW(),
    NOW(),
    1, -- account non expired
    1, -- account non locked
    1, -- credentials non expired
    0, -- enabled
    '$argon2id$v=19$m=23552,t=2,p=1$xnr/bs027Dlc/fVMbrapcRU$R5NvPkutHQfnx5fG6FTt3iaUMIu0i/5x8CMpiNekgGs'
);

insert into users_roles values
(1, 1);


