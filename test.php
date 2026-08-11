<?php

session_start();

$currentScript = basename($_SERVER['PHP_SELF'] ?? '');
if (!isset($_SESSION['wf_logged_in']) && $currentScript !== 'login.php') {
  header('Location: login.php');
  exit;
}

    /*

    Author: Chase Rennert
    Date: 12/05/2025

    Filename: WF_reunion1.php

     */
class Page {
    // class properties
    public $content;

    public $title = "WF Reunion";

    public $keywords = "family reunion, Warburton family, reunion activities, 
                        family photos, contact us";

    

    public $buttons = array (
        "Home" => "home.php",
        "Activities" => "WF_aa.php",
        "Photos" => "WF_photoGallery.php",
      "Contact Us" => "contactus.php",
      "Logout" => "logout.php"
        );

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
            echo "\n</body>\n</html>";
        }

    public function DisplayTitle()
    {
        echo "<title>" .$this->title."</title>\n";
    }

public function DisplayKeywords()
    {
        ?>
        <meta name="keywords" content="<?php echo $this->keywords; ?>">
        <?php
    }

    public function DisplayStyles()
    {
        ?>
        <meta charset="UTF-8">
      <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="WF_countdown.css" rel="stylesheet">
        <link href="WF_reunion.css" rel="stylesheet">
        <link href="WF_aa.css" rel="stylesheet">
        <link href="WF_reunion_contact.css" rel="stylesheet">
      <script src="theme-toggle.js" defer></script>

        <?php
    }

    public function DisplayHeader()
    {
    ?>
        <!-- Header -->
        <header>
            <img src="test2.png" alt="Warburton Family" height="200" width="500">
            <h1>Warburton Family Reunion</h1>    
        </header>
       
    <?php
    }

     public function DisplayMenu($buttons)
  {
    echo "<!-- menu -->
    <nav>";
        // navigation buttons

    foreach ($buttons as $name => $url) {
      $this->DisplayButton($name, $url);
    }
    // while (list($name, $url) = each($buttons)) {
    //   $this->DisplayButton($name, $url, 
    //            !$this->IsURLCurrentPage($url));
    // }
    echo '<select id="themeToggle" class="theme-toggle" aria-label="Select color theme">
      <option value="light">Light Mode</option>
      <option value="dark">Dark — Teal</option>
      <option value="dark-forest">Dark — Slate</option>
      <option value="dark-ember">Dark — Ember</option>
    </select>';
    echo "</nav>\n";
  }

  public function DisplayButton($name,$url,$active = true)
  {
    if ($active) { ?>
      <div class="menuitem">
        <a href="<?=$url?>">
        <img src="family.jpg" alt="" height="20" width="20" />
        <span class="menutext"><?=$name?></span>
        </a>
      </div>
      <?php
    } else { ?>
      <div class="menuitem">
      <img src="family.jpg" alt="" height="20" width="20" />
      <span class="menutext"><?=$name?></span>
      </div>
      <?php
    }  
  }

    public function DisplayFooter()
    {
        ?>
        <div id="footer">
        <!-- footer -->
        <footer>
            <p>&copy; 2025 Warburton Family Reunion. All rights reserved.</p>
        </footer></div>
        <?php
    }

}
?>
