<?php
session_start();
require __DIR__ . '/db_connect.php';

if (isset($_SESSION['wf_logged_in']) && $_SESSION['wf_logged_in'] === true) {
    header('Location: home.php');
    exit;
}

$errorMessage   = '';
$successMessage = isset($_GET['verified']) ? 'Email verified! You can now sign in.' : '';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $username      = trim($_POST['username'] ?? '');
    $password      = $_POST['password'] ?? '';
    $authenticated = false;

    if ($username === 'family' && $password === 'reunion2026') {
        $authenticated = true;
    } else {
        // email IS NULL allows admin-created users (no email set) to log in without verification
        $pdo  = db_connect();
        $stmt = $pdo->prepare(
            'SELECT password_hash FROM users WHERE username = ? AND (email_verified = 1 OR email IS NULL) LIMIT 1'
        );
        $stmt->execute([$username]);
        $user = $stmt->fetch();
        if ($user && password_verify($password, $user['password_hash'])) {
            $authenticated = true;
        }
    }

    if ($authenticated) {
        session_regenerate_id(true);
        $_SESSION['wf_logged_in'] = true;
        $_SESSION['wf_username']  = $username;
        header('Location: home.php');
        exit;
    }

    $errorMessage = 'Invalid username or password.';
}
?>
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - WF Reunion</title>
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
        <h1>WF Reunion Login</h1>
        <p>Please sign in to access the website.</p>

        <?php if ($successMessage !== ''): ?>
            <p class="auth-success"><?php echo htmlspecialchars($successMessage, ENT_QUOTES, 'UTF-8'); ?></p>
        <?php endif; ?>

        <?php if ($errorMessage !== ''): ?>
            <p class="auth-error"><?php echo htmlspecialchars($errorMessage, ENT_QUOTES, 'UTF-8'); ?></p>
        <?php endif; ?>

        <form method="post" action="login.php">
            <div>
                <label for="username">Username</label><br>
                <input id="username" name="username" type="text" required>
            </div>
            <div>
                <label for="password">Password</label><br>
                <input id="password" name="password" type="password" required>
            </div>
            <button type="submit">Login</button>
        </form>

        <p class="auth-hint">Contact your closest Auntie if you need access.</p>
        <a href="register.php" class="btn-secondary">Create New Account</a>
    </main>
</body>
</html>
