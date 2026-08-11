
<!-- Author: Chase Rennert
   Date: 12/05/2025
   Filename: emailprocess.php
  -->

<?php
require("test.php");
require("file_exceptions.php");
require(__DIR__ . '/db_connect.php');

//create short variable names
$name = trim($_POST['name']);
$email = trim($_POST['email']);
$message = trim($_POST['message']);
$subject = trim($_POST['subject']);
$document_root = $_SERVER['DOCUMENT_ROOT'];
$date = date('H:i, jS F Y');

//set up some static information
$toaddress = ADMIN_EMAIL;

// Save submission to database
$pdo = db_connect();
$pdo->prepare(
    'INSERT INTO contact_messages (name, email, subject, message) VALUES (?, ?, ?, ?)'
)->execute([$name, $email, $subject, $message]);

$mailcontent = "name: ".str_replace("\r\n", "", $name)."<br />".
    "email: ".str_replace("\r\n", "", $email)."<br />".
    "message:<br />".str_replace("\r\n", "", $message);

    $outputstring = $date."\tname: ".$name."\temail: ".$email.
                    "\tsubject: ".$subject."\tmessage: ".$message."\n";
                 
    // Append to log file as backup
    $fp = fopen("emails.txt", 'ab');
    if ($fp) {
        flock($fp, LOCK_EX);
        fwrite($fp, $outputstring);
        flock($fp, LOCK_UN);
        fclose($fp);
    }

// build headers for HTML email
$headers = "From: " . MAIL_FROM . "\r\n" .
           "Reply-To: " . $email . "\r\n" .
           "MIME-Version: 1.0\r\n" .
           "Content-Type: text/html; charset=UTF-8\r\n";

//invoke mail() function to send mail
mail($toaddress, $subject, $mailcontent, $headers);

?>

<?php
/* Author: Chase Rennert
   Date: 12/05/2025
    Filename: emailprocess.php
*/
class EmailProcessPage extends Page {
    public $title = "Email Processed - WF Reunion";
    public $keywords = "email processed, contact us, Warburton family reunion";

    public function Display()
    {
      echo "<html>\n<head>\n";
      $this->DisplayTitle();
      $this->DisplayKeywords();
      $this->DisplayStyles();
      echo "</head>\n<body>\n";
      $this->DisplayHeader();
      $this->DisplayMenu($this->buttons);
      echo $this->content;
      $this->DisplayFooter();
      echo "</body>\n</html>\n";
    }
}
$EmailProcessPage = new EmailProcessPage();

$EmailProcessPage -> content = <<<'HTML'
    <!-- Thank you page -->

    <div id="thankyou">

        <h1>Feedback submitted</h1>
        <h2>Thank you for contacting us!</h2>
    <p>We appreciate you reaching out to the Warburton Family Reunion team. Your message has been received, and we will get back to you as soon as possible.
        If you have any urgent inquiries, please feel free to get a hold of your Auntie.
        We look forward to connecting with you and making this reunion a memorable experience for all family members.
    </p> 
   
    </div>
        <div id="mailcontent">
            <p> <?php echo $mailcontent;  ?> </p>
        </div>

    
    HTML;
$EmailProcessPage -> Display();
?>