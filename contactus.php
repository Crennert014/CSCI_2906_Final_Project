<?php
    /*
    Author: Chase Rennert
    Date: 12/05/2025
    Filename: contactus.php
    */ 
    require("test.php");

    class ContactUsPage extends Page {
        public $title = "Contact Us - WF Reunion";
        public $keywords = "contact, email, questions, suggestions, Warburton family reunion";

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
   $ContactUs = new ContactUsPage();

    $ContactUs -> content = <<<'HTML'
    <div id="contactInfo">
        <h1>Contact Us</h1>
            <p class="sendEmail"></p>
                <form class="contact-form" action="emailprocess.php" method="post">
                    <div class="formRow"> 
                        <label for="name">Name *</label>
                        <input id="name" name="name" type="text" placeholder="Full Name" required>
                    </div> 
                    <div class="formRow">
                        <label for="email">Your Email *</label>
                        <input  id="email" name="email" type="email" placeholder="Email Address" required>
                    </div>
                    <div class="formRow">
                        <label for="subject">Subject</label>
                        <input id="subject" type="text"name="subject" placeholder="Subject">
                    </div>
                    <div class="formRow">
                        <label for="message">Message*</label>
                        <textarea id="message" type="text" name="message" placeholder="Concerns, Comments, Questions, Ideas" required style="width: 50%; height: 150px;"></textarea>
                    </div>
                    <div class="submitButton">      
                        <label for="Submit" type="button" >Submit</label>
                        <input type="submit"/>
                    </div>
                </form>  
            
                <p>If you have any questions, suggestions, or need further information about the Warburton Family Reunion, 
                    please feel free to reach out to your closest Auntie. 
                 We are here to assist you and ensure that you have a wonderful experience at the reunion.</p>
    </div>
HTML;
    $ContactUs -> Display();
?>