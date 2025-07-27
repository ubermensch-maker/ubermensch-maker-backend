FROM postgres:17

# install dev tools
RUN apt-get update && apt-get install -y \
    postgresql-server-dev-17 \
    build-essential \
    git \
    && rm -rf /var/lib/apt/lists/*

# install pg_uuidv7 extension
RUN cd /tmp \
    && git clone https://github.com/fboulnois/pg_uuidv7.git \
    && cd pg_uuidv7 \
    && make \
    && make install \
    && rm -rf /tmp/pg_uuidv7

# copy sql files
COPY *.sql /docker-entrypoint-initdb.d/