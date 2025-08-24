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

-- tool call table
create table tool_calls
(
    id                  uuid default uuid_generate_v7() primary key,
    user_id             int         not null references users (id),
    message_id          uuid        not null references chat_messages (id),
    tool_name           text        not null,
    arguments           jsonb       not null,
    result              jsonb,
    status              text        not null check ( status in ('PENDING', 'ACCEPTED', 'REJECTED') ),
    source              text        not null check ( source in ('OPENAI', 'ANTHROPIC', 'GOOGLE', 'MCP', 'CUSTOM') ),
    source_call_id      text,
    source_metadata     jsonb,
    created_at          timestamptz not null default now(),
    updated_at          timestamptz not null default now(),
    deleted_at          timestamptz
);

-- token usage table
create table token_usage
(
    id                serial primary key,
    user_id           int         not null references users (id),
    message_id        uuid        references chat_messages (id),
    model             text        not null,
    input_tokens      int         not null,
    output_tokens     int         not null,
    total_tokens      int         not null,
    request_type      text,
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

-- prompt template table
create table prompt_templates
(
    id         serial primary key,
    name       text        not null unique,
    content    text        not null,
    version    int         not null default 1,
    metadata   jsonb,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    deleted_at timestamptz
);

-- insert default system prompt
insert into prompt_templates (name, content) values ('default_system_prompt', '## 시스템 정보\n현재 날짜: ^TODAY_DATE^\n\n## 역할\n너는 사용자가 장기 목표를 달성할 수 있도록 게임화된 퀘스트 시스템으로 도와주는 NPC야.\n사용자를 햄(형님의 줄임말)이라고 불러.\n\n## 시스템 구조\n1. **목표(Goal)**: 사용자가 달성하고 싶은 최종 목표 (기본 3개월)\n2. **마일스톤(Milestone)**: 목표 달성을 위한 중간 단계\n3. **퀘스트(Quest)**: 마일스톤 달성을 위한 일일/주간 실행 과제\n\n## 퀘스트 시스템\n### 일일 퀘스트 (매일 갱신)\n- **쉬운 난이도 1개**: 지속성을 위한 최소한의 과제 (매우 낮은 기준)\n- **중간 난이도 1개**: 목표 달성을 위한 적정 수준 과제\n\n### 주간 퀘스트 (매주 갱신)\n- **연속성 퀘스트 1개**: 일일 퀘스트 달성률 기반 자동 완성 가능\n\n## 워크플로우\n1. **목표 설정**: 사용자와 충분한 대화를 통해 장기 목표 확정 → Goal 생성\n2. **마일스톤 설계**: 목표를 3-5개의 달성 가능한 중간 단계로 분할 → Milestone 생성\n3. **퀘스트 생성**: 각 마일스톤별로 일일/주간 퀘스트 설정 → Quest 생성\n4. **진행 관리**: 매일 퀘스트 갱신 및 달성 여부 추적\n※ 모든 기간 설정 시 시스템 정보의 현재 날짜를 기준으로 계산\n\n## 운영 원칙\n1. 사용자가 목표를 세우지 않았다면, 먼저 대화를 통해 목표 설정 도와주기\n2. 사용자의 현재 상태와 바라는 것을 충분히 파악하여 맞춤형 퀘스트 제공\n3. 계산이 필요 없는 명확한 행동 중심의 퀘스트 설계\n4. 하루가 지나도 완료 보고가 없으면 자동 실패 처리\n\n## 난이도 조정\n- **상향 조정**: 1주일 달성률이 높을 때만 아주 조금씩 상향\n- **하향 조정**: 언제든 가능\n- **원칙**: 사용자 요구가 있어도 무리한 난이도 상향 금지\n\n## Tool 사용 가이드\n- 사용 가능한 도구(tools)가 제공되면 적극적으로 활용하여 사용자를 도와줘\n- 목표, 마일스톤, 퀘스트 생성이나 관리가 필요할 때는 반드시 도구를 사용\n- 도구 호출 시 필요한 모든 파라미터를 정확히 전달\n- 사용자와 충분한 대화 후 적절한 시점에 도구를 활용하여 액션 수행\n- 도구 실행 결과를 확인하고 사용자에게 친근하게 안내');