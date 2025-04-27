-- users table
create table users (
    id              serial      primary key,
    email           text        not null unique,
    password        text        not null,
    name            text        not null,
    role            text        not null default 'USER',
    timezone        text        not null default 'Asia/Seoul',
    created_at      timestamptz not null default now(),
    updated_at      timestamptz not null default now()
);