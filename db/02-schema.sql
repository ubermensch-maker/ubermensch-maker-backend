-- user table
create table users
(
    id                serial primary key,
    email             text        not null unique,
    name              text        not null,
    role              text        not null check ( role in ('USER', 'ADMIN') ),
    picture           text,
    oauth_provider    text        not null check ( oauth_provider in ('GOOGLE') ),
    oauth_provider_id text        not null,
    created_at        timestamptz not null default now(),
    updated_at        timestamptz not null default now(),
    deleted_at        timestamptz
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
    updated_at  timestamptz not null default now(),
    deleted_at  timestamptz
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
    updated_at  timestamptz not null default now(),
    deleted_at  timestamptz
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
    updated_at   timestamptz not null default now(),
    deleted_at   timestamptz
);

-- conversation table
create table chat_conversations
(
    id         uuid default uuid_generate_v7() primary key,
    user_id    int         not null references users (id),
    title      text        not null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    deleted_at timestamptz
);

-- message table
create table chat_messages
(
    id                uuid default uuid_generate_v7() primary key,
    conversation_id   uuid        not null references chat_conversations (id),
    parent_message_id uuid        references chat_messages (id),
    user_id           int         references users (id),
    message_index     int         not null default 0,
    model             text        not null,
    role              text        not null check ( role in ('USER', 'ASSISTANT') ),
    content           jsonb       not null,
    created_at        timestamptz not null default now(),
    updated_at        timestamptz not null default now(),
    deleted_at        timestamptz
);

-- memory table
create table memories
(
    id         serial primary key,
    user_id    int         not null references users (id),
    content    text        not null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    deleted_at timestamptz
);

-- system prompt table
create table system_prompts
(
    id         uuid default uuid_generate_v7() primary key,
    name       text        not null,
    prompt     text        not null,
    version    int         not null default 1,
    metadata   jsonb,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    deleted_at timestamptz
);

-- tool call table
create table tool_calls
(
    id                  uuid default uuid_generate_v7() primary key,
    user_id             int         not null references users (id),
    message_id          uuid        not null references chat_messages (id),
    function_name       text        not null,
    arguments           jsonb       not null,
    result              jsonb,
    status              text        not null check ( status in ('PENDING', 'ACCEPTED', 'REJECTED') ),
    openai_tool_call_id text,
    created_at          timestamptz not null default now(),
    updated_at          timestamptz not null default now(),
    deleted_at          timestamptz
);
