SELECT 'CREATE DATABASE user_service'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'user_service')\gexec
SELECT 'CREATE DATABASE auth_service'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'auth_service')\gexec
SELECT 'CREATE DATABASE order_service'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'order_service')\gexec
