db.auth('root', 'root');
db.createUser({
    user: 'admin',
    pwd: 'admin',
    roles: [
        {
            role: 'dbOwner',
            db: 'payment_service',
        },
    ],
});
db = new Mongo().getDB("payment_service");