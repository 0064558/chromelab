CREATE TABLE rule_set (
    id VARCHAR(80) PRIMARY KEY,
    version VARCHAR(40) NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('draft', 'verified', 'retired')),
    source_url TEXT NOT NULL
);
INSERT INTO rule_set(id, version, status, source_url) VALUES
('attributes-2.0-v1', '1', 'draft', 'https://www.cyberpunk.net/en/build-planner');

CREATE TABLE saved_build (
    id UUID PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    ruleset_id VARCHAR(80) NOT NULL REFERENCES rule_set(id),
    character_state JSONB NOT NULL CHECK (jsonb_typeof(character_state) = 'object'),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE TABLE plan_snapshot (
    id UUID PRIMARY KEY,
    build_id UUID REFERENCES saved_build(id),
    ruleset_id VARCHAR(80) NOT NULL REFERENCES rule_set(id),
    request JSONB NOT NULL CHECK (jsonb_typeof(request) = 'object'),
    response JSONB NOT NULL CHECK (jsonb_typeof(response) = 'object'),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX plan_snapshot_build_idx ON plan_snapshot(build_id);
