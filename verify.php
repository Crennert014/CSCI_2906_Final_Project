<?php
require __DIR__ . '/db_connect.php';

$status = 'invalid';
$token  = trim($_GET['token'] ?? '');

if ($token !== '') {
    $pdo  = db_connect();
    $stmt = $pdo->prepare(
        'SELECT id, email_verified, (token_expires_at > NOW()) AS still_valid
         FROM users WHERE verification_token = ? LIMIT 1'
    );
    $stmt->execute([$token]);
    $user = $stmt->fetch();

    if ($user) {
        if ((int) $user['email_verified'] === 1) {
            $status = 'already';
        } elseif (!(int) $user['still_valid']) {
            $status = 'expired';
        } else {
            $pdo->prepare(
                'UPDATE users
                 SET email_verified = 1, verification_token = NULL, token_expires_at = NULL
                 WHERE id = ?'
            )->execute([$user['id']]);
            // Redirect so the token is no longer in the URL on the success screen.
            header('Location: login.php?verified=1');
            exit;
        }
    }
}

$messages = [
    'expired' => ['This verification link has expired. Please register again.', 'auth-error'],
    'already' => ['Your account is already verified. Please sign in.',           'auth-hint'],
    'invalid' => ['Invalid or missing verification link.',                        'auth-error'],
];
[$msg, $cls] = $messages[$status];
?>
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Email Verification - WF Reunion</title>
    <link href="WF_reunion.css" rel="stylesheet">
    <script src="theme-toggle.js" defer></script>
</head>
<body>
    <main class="auth-card">
        <select id="themeToggle" class="theme-toggle auth-toggle" aria-label="Select color theme">
            <option value="light">Light Mode</option>
            <option value="dark">Dark &mdash; Teal</option>
            <option value="dark-forest">Dark &mdash; Slate</option>
            <option value="dark-ember">Dark &mdash; Ember</option>
        </select>

        <h1>Email Verification</h1>
        <p class="<?php echo $cls; ?>"><?php echo htmlspecialchars($msg, ENT_QUOTES, 'UTF-8'); ?></p>

        <?php if ($status === 'expired'): ?>
            <p><a href="register.php" class="btn-secondary">Register Again</a></p>
        <?php endif; ?>
        <p class="auth-hint"><a href="login.php">Go to Login</a></p>
    </main>
</body>
</html>
