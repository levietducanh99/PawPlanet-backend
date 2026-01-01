-- Add InvalidatedToken table for JWT token blacklisting
CREATE TABLE IF NOT EXISTS auth.invalidated_tokens (
    id VARCHAR(255) PRIMARY KEY,
    expired_at TIMESTAMP NOT NULL
);

-- Create index on expired_at for efficient cleanup queries
CREATE INDEX IF NOT EXISTS idx_invalidated_token_expired_at ON auth.invalidated_tokens(expired_at);
