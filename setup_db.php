<?php
/**
 * setup_db.php — Run ONCE in a browser or CLI after importing db_schema.sql.
 * Creates the default 'family' user with a securely hashed password.
 * Delete or restrict access to this file after running it.
 */
require __DIR__ . '/db_connect.php';

$defaultUsers = [
    ['username' => 'family', 'password' => 'reunion2026', 'display_name' => 'Family Member', 'role' => 'member'],
];

$pdo  = db_connect();
$stmt = $pdo->prepare(
    'INSERT IGNORE INTO users (username, password_hash, display_name, role)
     VALUES (:username, :hash, :display_name, :role)'
);

foreach ($defaultUsers as $u) {
    $stmt->execute([
        ':username'     => $u['username'],
        ':hash'         => password_hash($u['password'], PASSWORD_BCRYPT),
        ':display_name' => $u['display_name'],
        ':role'         => $u['role'],
    ]);
    $status = $stmt->rowCount() ? 'created' : 'already exists';
    echo "User '{$u['username']}': {$status}\n";
}

echo "\nSetup complete. Delete or restrict access to this file.\n";
