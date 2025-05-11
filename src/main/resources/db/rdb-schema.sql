-- user table
create table users
(
    id         serial primary key,
    email      text        not null unique,
    password   text        not null,
    name       text        not null,
    role       text        not null default 'USER',
    timezone   text        not null default 'Asia/Seoul',
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

-- goal table
create table goals
(
    id          serial primary key,
    user_id     int         not null references users (id),
    title       text        not null,
    description text,
    start_at    timestamptz,
    end_at      timestamptz,
    created_at  timestamptz not null default now(),
    updated_at  timestamptz not null default now()
);

-- kpi table
create table kpis
(
    id          serial primary key,
    user_id     int         not null references users (id),
    goal_id     int         not null references goals (id),
    title       text        not null,
    description text,
    status      text        not null default 'PENDING',
    start_at    timestamptz,
    end_at      timestamptz,
    created_at  timestamptz not null default now(),
    updated_at  timestamptz not null default now()
);

-- task table
create table tasks
(
    id          serial primary key,
    user_id     int         not null references users (id),
    goal_id     int references goals (id),
    kpi_id      int references kpis (id),
    title       text        not null,
    description text,
    status      text        not null default 'PENDING',
    start_at    timestamptz,
    end_at      timestamptz,
    created_at  timestamptz not null default now(),
    updated_at  timestamptz not null default now()
);