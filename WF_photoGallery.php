<?php
    /*
  <!--
    Title: Warburton Family Reunion Photo Gallery
    Author: Chase Rennert
    Date: 2023-10-22

    Filename: WF_photoGallery.php
    Description: This is the photo gallery page for the Warburton Family
     Reunion website.
     -->*/
    require("test.php");
    require(__DIR__ . '/db_connect.php');

    $uploadMessage = "";
    if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_FILES['photoFile'])) {
      if ($_FILES['photoFile']['error'] !== UPLOAD_ERR_OK) {
        $uploadMessage = "<p>Photo upload failed. Please try again.</p>";
      } else {
        $allowedExtensions = array('jpg', 'jpeg', 'png', 'gif', 'webp');
        $originalName = $_FILES['photoFile']['name'];
        $extension = strtolower(pathinfo($originalName, PATHINFO_EXTENSION));

        if (!in_array($extension, $allowedExtensions, true)) {
          $uploadMessage = "<p>Please upload a valid image file (JPG, PNG, GIF, or WEBP).</p>";
        } elseif ($_FILES['photoFile']['size'] > 5 * 1024 * 1024) {
          $uploadMessage = "<p>Please upload an image smaller than 5MB.</p>";
        } else {
          $uploadDirectory = __DIR__ . '/images/';
          if (!is_dir($uploadDirectory)) {
            mkdir($uploadDirectory, 0775, true);
          }

          $safeFilename = 'wf_' . time() . '_' . bin2hex(random_bytes(4)) . '.' . $extension;
          $destinationPath = $uploadDirectory . $safeFilename;

          if (move_uploaded_file($_FILES['photoFile']['tmp_name'], $destinationPath)) {
            $pdo = db_connect();
            $userId = null;
            if (isset($_SESSION['wf_username'])) {
                $stmt = $pdo->prepare('SELECT id FROM users WHERE username = ?');
                $stmt->execute([$_SESSION['wf_username']]);
                $u = $stmt->fetch();
                $userId = $u ? (int) $u['id'] : null;
            }
            $pdo->prepare('INSERT INTO photos (filename, original_name, uploaded_by) VALUES (?, ?, ?)')
                ->execute([$safeFilename, $originalName, $userId]);
            $uploadMessage = "<p>Photo uploaded successfully.</p>";
          } else {
            $uploadMessage = "<p>Unable to save uploaded photo.</p>";
          }
        }
      }
    }

    class PhotoGalleryPage extends Page {
        public $title = "Photo Gallery - WF Reunion";
        public $keywords = "family reunion photos, Warburton family reunion,
                        photo gallery, family memories, reunion pictures";
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

        $pictures = array('WF1.png', 'WF2.png', 'WF3.png', 'WF4.png');
        $pdo = db_connect();
        $stmt = $pdo->query('SELECT filename FROM photos ORDER BY uploaded_at DESC');
        foreach ($stmt->fetchAll() as $row) {
            $path = 'images/' . $row['filename'];
            if (!in_array($path, $pictures, true)) {
                $pictures[] = $path;
            }
        }

    foreach ($pictures as $pic) {
        echo '<img src="' . htmlspecialchars($pic, ENT_QUOTES, 'UTF-8') . '" alt="Warburton Family Photo">';
    }

      $this->DisplayFooter();
      echo "</body>\n</html>\n";
    }
  }

    $photoGalleryPage = new PhotoGalleryPage();

    
    $photoGalleryPage -> content = <<<'HTML'
    
    <div id="photoGallery">
        <h1><strong>Warburton Family Reunion Photo Gallery</strong></h1>
        <p>Welcome to the Warburton Family Reunion Photo Gallery! Here, 
            you can relive the wonderful moments we've shared during our 
            reunions over the years. Browse through the collection of 
            photos capturing the joy, laughter, and togetherness of our
            family gatherings. From candid shots to group photos, each
            image tells a story of love and connection that defines our
            family. Feel free to download and share these memories
            with your loved ones. We look forward to adding more
            photos from future reunions, so stay tuned!</p>

          <form method="post" enctype="multipart/form-data">
            <label for="photoFile">Choose a photo:</label>
            <input type="file" id="photoFile" name="photoFile" accept="image/*" required>
            <button type="submit">Upload Photo</button>
          </form>
    </div>

    HTML;

        if ($uploadMessage !== "") {
          $photoGalleryPage->content .= $uploadMessage;
        }
    
       $photoGalleryPage -> Display();
       
    
        
    
    ?>