<?php
session_start();
require __DIR__ . '/db_connect.php';

if (isset($_SESSION['wf_logged_in']) && $_SESSION['wf_logged_in'] === true) {
    header('Location: home.php');
    exit;
}

$error   = '';
$success = false;

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $username = trim($_POST['username'] ?? '');
    $email    = trim($_POST['email']    ?? '');
    $password = $_POST['password'] ?? '';
    $confirm  = $_POST['confirm']  ?? '';

    if ($username === '' || $email === '' || $password === '' || $confirm === '') {
        $error = 'All fields are required.';
    } elseif (!preg_match('/^[a-zA-Z0-9_]{3,30}$/', $username)) {
        $error = 'Username must be 3-30 characters: letters, numbers, and underscores only.';
    } elseif (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        $error = 'Please enter a valid email address.';
    } elseif (strlen($password) < 8) {
        $error = 'Password must be at least 8 characters.';
    } elseif ($password !== $confirm) {
        $error = 'Passwords do not match.';
    } else {
        $pdo  = db_connect();
        $stmt = $pdo->prepare('SELECT id FROM users WHERE username = ? OR email = ? LIMIT 1');
        $stmt->execute([$username, $email]);

        if ($stmt->fetch()) {
            $error = 'That username or email is already registered.';
        } else {
            $token   = bin2hex(random_bytes(32));
            $expires = date('Y-m-d H:i:s', strtotime('+24 hours'));

            $pdo->prepare(
                'INSERT INTO users
                    (username, password_hash, display_name, email, email_verified, verification_token, token_expires_at, role)
                 VALUES (?, ?, ?, ?, 0, ?, ?, "member")'
            )->execute([
                $username,
                password_hash($password, PASSWORD_BCRYPT),
                $username,
                $email,
                $token,
                $expires,
            ]);

            $scheme    = (!empty($_SERVER['HTTPS']) && $_SERVER['HTTPS'] !== 'off') ? 'https' : 'http';
            $dir       = str_replace('\\', '/', rtrim(dirname($_SERVER['SCRIPT_NAME']), '/\\'));
            $verifyUrl = $scheme . '://' . $_SERVER['HTTP_HOST'] . $dir . '/verify.php?token=' . urlencode($token);

            $subject = 'Verify your WF Reunion account';
            $body    = "Hello {$username},\r\n\r\n"
                     . "Welcome to the Warburton Family Reunion website!\r\n\r\n"
                     . "Please verify your email address by clicking the link below:\r\n\r\n"
                     . $verifyUrl . "\r\n\r\n"
                     . "This link expires in 24 hours. If you did not register, you can ignore this email.\r\n\r\n"
                     . "— The Warburton Family Reunion Team";
            $headers = "From: " . MAIL_FROM . "\r\n"
                     . "Content-Type: text/plain; charset=UTF-8\r\n";

            mail($email, $subject, $body, $headers);
            $success = true;
        }
    }
}
?>
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create Account - WF Reunion</title>
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

        <h1>Create Account</h1>

        <?php if ($success): ?>
            <p class="auth-success">
                Account created! A verification email has been sent to your address.
                Please check your inbox and click the link to activate your account.
            </p>
            <p><a href="login.php" class="btn-secondary">Back to Login</a></p>
        <?php else: ?>
            <?php if ($error !== ''): ?>
                <p class="auth-error"><?php echo htmlspecialchars($error, ENT_QUOTES, 'UTF-8'); ?></p>
            <?php endif; ?>

            <form method="post" action="register.php" id="regForm">
                <div>
                    <label for="reg-username">Username</label><br>
                    <input id="reg-username" name="username" type="text" required
                           pattern="[a-zA-Z0-9_]{3,30}"
                           title="3-30 characters: letters, numbers, underscores"
                           value="<?php echo htmlspecialchars($_POST['username'] ?? '', ENT_QUOTES, 'UTF-8'); ?>">
                </div>
                <div>
                    <label for="reg-email">Email Address</label><br>
                    <input id="reg-email" name="email" type="email" required
                           value="<?php echo htmlspecialchars($_POST['email'] ?? '', ENT_QUOTES, 'UTF-8'); ?>">
                </div>
                <div>
                    <label for="reg-password">Password <small>(min. 8 characters)</small></label><br>
                    <input id="reg-password" name="password" type="password" required minlength="8">
                </div>
                <div>
                    <label for="reg-confirm">Confirm Password</label><br>
                    <input id="reg-confirm" name="confirm" type="password" required minlength="8">
                </div>
                <button type="submit">Create Account</button>
            </form>

            <p class="auth-hint">Already have an account? <a href="login.php">Sign in</a></p>

            <script>
            document.getElementById('regForm').addEventListener('submit', function (e) {
                var p = document.getElementById('reg-password');
                var c = document.getElementById('reg-confirm');
                c.setCustomValidity(p.value !== c.value ? 'Passwords do not match.' : '');
            });
            document.getElementById('reg-confirm').addEventListener('input', function () {
                this.setCustomValidity('');
            });
            </script>
        <?php endif; ?>
    </main>
</body>
</html>
