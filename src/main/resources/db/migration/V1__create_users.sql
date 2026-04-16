create table users (
    id uuid primary key,
    email varchar(255) unique not null,
    password_hash varchar(255) not null,
    role varchar(32) not null,
    deleted boolean not null default false,
    created_at timestamp not null
);
