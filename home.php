<?php
  require("test.php");

  $homepage = new Page();

  $homepage->content = <<<'HTML'
  <link href="WF_countdown.css" rel="stylesheet">
  <script src="ClockCountdown.js" defer></script>
  
    <section>
            <!-- warburton family reunion -->
    <div id="Warburton1">
    
        <h2>Welcome to the Warburton Family Reunion Page!</h2>
            <p>
                We are thrilled to have all family members join us every year to create new memories and continue in the tradition that our ancestors
            started. This reunion is a special occasion for our family to reconnect, share stories, and enjoy each other's company.
            We have planned a variety of activities and events to ensure that everyone has a fantastic time. From fun games for the kids to
            stay entertained, activities for adults, and there is always something for everyone to enjoy. We encourage all family members to participate 
            and make the most of this reunion.<br /> <br/></p>
            <h2><strong>Warburton Coat of Arms -<strong></h2> 
            <p>Our family crest symbols with 3 Ravens which is a
            A symbol of knowledge. This is also a symbol divine providence, also, a durable
            resistance as well as the nature the bringer of death.
            </p>
    </div>
    
            <!-- countdown clock -->
    <div id="clock">
      <h3>Countdown to the Reunion</h3>
      <p class="clock-location"><strong>Where:</strong> Minersville Reservoir</p>

      <div class="clock-event" id="eventDate"></div>

      <div class="clock-grid" aria-live="polite">
        <div class="clock-tile" id="daysLeft"></div>
        <div class="clock-tile" id="hrsLeft"></div>
        <div class="clock-tile" id="minsLeft"></div>
        <div class="clock-tile" id="secsLeft"></div>
      </div>

      <div class="clock-now">
        <div id="thisDate"></div>
        <div id="thisTime"></div>
        <div id="thisWeekDay"></div>
      </div>
    </div>
    </section>
HTML;
  $homepage->Display();
?>
