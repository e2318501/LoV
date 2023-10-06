CREATE TABLE IF NOT EXISTS dungeons
(
    name                varchar(16) NOT NULL PRIMARY KEY,
    player_limit        int(10) NOT NULL DEFAULT 10
);

CREATE TABLE IF NOT EXISTS replicas
(
    id                  int(10) NOT NULL,
    dungeon_name        varchar(16) NOT NULL,
    start_location      text NOT NULL,
    UNIQUE (id, dungeon_name)
);

CREATE TABLE IF NOT EXISTS player_last_locations
(
    player_uuid         char(36) NOT NULL PRIMARY KEY,
    replica_id          int(10) NOT NULL,
    location            text NOT NULL
)
