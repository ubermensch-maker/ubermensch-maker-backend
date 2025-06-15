-- user table
create table users
(
    id         serial primary key,
    email      text        not null unique,
    password   text        not null,
    name       text        not null,
    role       text        not null check ( role in ('USER', 'ADMIN', 'SYSTEM') ),
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
    status      text        not null,
    start_at    timestamptz,
    end_at      timestamptz,
    created_at  timestamptz not null default now(),
    updated_at  timestamptz not null default now()
);

-- milestone table
create table milestones
(
    id          serial primary key,
    user_id     int         not null references users (id),
    goal_id     int         not null references goals (id),
    title       text        not null,
    description text,
    status      text        not null,
    start_at    timestamptz,
    end_at      timestamptz,
    created_at  timestamptz not null default now(),
    updated_at  timestamptz not null default now()
);

-- quest table
create table quests
(
    id           serial primary key,
    user_id      int         not null references users (id),
    goal_id      int references goals (id),
    milestone_id int references milestones (id),
    title        text        not null,
    description  text,
    type         text        not null,
    status       text        not null,
    start_at     timestamptz,
    end_at       timestamptz,
    created_at   timestamptz not null default now(),
    updated_at   timestamptz not null default now()
);

-- conversation table
create table conversations
(
    id         serial primary key,
    user_id    int         not null references users (id),
    goal_id    int         not null references goals (id),
    title      text        not null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

-- message table
create table messages
(
    id              serial primary key,
    conversation_id int         not null references conversations (id),
    user_id         int references users (id),
    model           text        not null,
    role            text        not null,
    content         jsonb       not null,
    created_at      timestamptz not null default now(),
    updated_at      timestamptz not null default now()
);

-- memory table
create table memories
(
    id         serial primary key,
    user_id    int         not null references users (id),
    content    text        not null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);