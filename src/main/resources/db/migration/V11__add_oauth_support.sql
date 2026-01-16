-- Add OAuth support fields to users table
ALTER TABLE auth.users
ADD COLUMN IF NOT EXISTS auth_provider VARCHAR(50) DEFAULT 'LOCAL',
ADD COLUMN IF NOT EXISTS provider_user_id VARCHAR(255),
ADD COLUMN IF NOT EXISTS email_verified BOOLEAN DEFAULT FALSE;

-- Create unique constraint on (auth_provider, provider_user_id)
-- This ensures a Google user can only have one account
CREATE UNIQUE INDEX IF NOT EXISTS idx_users_auth_provider_user_id 
ON auth.users(auth_provider, provider_user_id)
WHERE provider_user_id IS NOT NULL;

-- Update existing users to have LOCAL auth provider
UPDATE auth.users 
SET auth_provider = 'LOCAL' 
WHERE auth_provider IS NULL;

-- Make auth_provider NOT NULL after setting defaults
ALTER TABLE auth.users
ALTER COLUMN auth_provider SET NOT NULL;
