<?php
/*
    Author: Chase Rennert
    Date: 12/05/2025
    Filename: WF_aa.php
*/
session_start();
require 'test.php';
require __DIR__ . '/db_connect.php';

// Handle meal signup form submission (PRG pattern)
if ($_SERVER['REQUEST_METHOD'] === 'POST' && ($_POST['form_type'] ?? '') === 'meal_signup') {
    $validDays  = ['Thursday', 'Friday', 'Saturday', 'Sunday'];
    $validMeals = ['Breakfast', 'Lunch', 'Dinner'];
    $day    = $_POST['day']    ?? '';
    $meal   = $_POST['meal']   ?? '';
    $family = trim($_POST['family_name'] ?? '');
    $menu   = trim($_POST['menu']        ?? '');

    if (in_array($day, $validDays, true) && in_array($meal, $validMeals, true)
            && $family !== '' && $menu !== '') {
        $pdo    = db_connect();
        $userId = null;
        if (!empty($_SESSION['wf_username'])) {
            $s = $pdo->prepare('SELECT id FROM users WHERE username = ? LIMIT 1');
            $s->execute([$_SESSION['wf_username']]);
            $row    = $s->fetch();
            $userId = $row ? $row['id'] : null;
        }
        // Only insert if the slot is not already taken
        $taken = $pdo->prepare('SELECT id FROM meal_signups WHERE meal = ? AND day = ? LIMIT 1');
        $taken->execute([$meal, $day]);
        if (!$taken->fetch()) {
            $pdo->prepare(
                'INSERT INTO meal_signups (meal, day, family_name, menu_description, signed_up_by)
                 VALUES (?, ?, ?, ?, ?)'
            )->execute([$meal, $day, $family, $menu, $userId]);
        }
    }
    header('Location: ' . strtok($_SERVER['REQUEST_URI'], '?'));
    exit;
}

// Fetch DB signups grouped by meal + day
$pdo     = db_connect();
$dbSlots = [];
foreach ($pdo->query('SELECT meal, day, family_name, menu_description FROM meal_signups ORDER BY id')->fetchAll() as $r) {
    $dbSlots[$r['meal']][$r['day']][] = ['family' => $r['family_name'], 'menu' => $r['menu_description']];
}

// Baseline static schedule — all slots start empty; families sign up via the form
$schedule = [
    'Breakfast' => ['Thursday' => null, 'Friday' => null, 'Saturday' => null, 'Sunday' => null],
    'Lunch'     => ['Thursday' => null, 'Friday' => null, 'Saturday' => null, 'Sunday' => null],
    'Dinner'    => ['Thursday' => null, 'Friday' => null, 'Saturday' => null, 'Sunday' => null],
];

$days = ['Thursday', 'Friday', 'Saturday', 'Sunday'];

// Build schedule table via output buffering
ob_start(); ?>
<table>
    <thead>
        <tr>
            <th>Meal</th>
            <?php foreach ($days as $day): ?>
            <th><?php echo htmlspecialchars($day, ENT_QUOTES, 'UTF-8'); ?></th>
            <?php endforeach; ?>
        </tr>
    </thead>
    <tbody>
        <?php foreach ($schedule as $meal => $dayData): ?>
        <tr>
            <td><strong><?php echo htmlspecialchars($meal, ENT_QUOTES, 'UTF-8'); ?></strong></td>
            <?php foreach ($days as $day):
                $static  = $dayData[$day] ?? null;
                $dynamic = $dbSlots[$meal][$day] ?? [];
            ?>
            <td>
                <?php if ($static): ?>
                    <span class="meal-entry meal-static">
                        <?php echo htmlspecialchars($static['family'], ENT_QUOTES, 'UTF-8'); ?><br>
                        <em><?php echo htmlspecialchars($static['menu'], ENT_QUOTES, 'UTF-8'); ?></em>
                    </span>
                <?php endif; ?>
                <?php foreach ($dynamic as $entry): ?>
                    <span class="meal-entry meal-signup">
                        <?php echo htmlspecialchars($entry['family'], ENT_QUOTES, 'UTF-8'); ?><br>
                        <em><?php echo htmlspecialchars($entry['menu'], ENT_QUOTES, 'UTF-8'); ?></em>
                    </span>
                <?php endforeach; ?>
                <?php if (!$static && empty($dynamic)): ?>
                    <span class="meal-na">N/A</span>
                <?php endif; ?>
            </td>
            <?php endforeach; ?>
        </tr>
        <?php endforeach; ?>
    </tbody>
</table>
<?php
$tableHtml = ob_get_clean();

// Build signup form (prefill family name from session username)
$prefill = htmlspecialchars($_SESSION['wf_username'] ?? '', ENT_QUOTES, 'UTF-8');
$formHtml = <<<HTML
<details class="meal-signup-panel">
    <summary class="meal-signup-btn">+ Sign Up for a Meal</summary>
    <form class="meal-signup-form" method="post" action="">
        <input type="hidden" name="form_type" value="meal_signup">
        <div class="meal-form-grid">
            <div>
                <label for="ms-family">Family Name</label>
                <input id="ms-family" name="family_name" type="text" required
                       value="{$prefill}" placeholder="e.g. Smith Family">
            </div>
            <div>
                <label for="ms-day">Day</label>
                <select id="ms-day" name="day" required>
                    <option value="">&#8212; Select Day &#8212;</option>
                    <option value="Thursday">Thursday</option>
                    <option value="Friday">Friday</option>
                    <option value="Saturday">Saturday</option>
                    <option value="Sunday">Sunday</option>
                </select>
            </div>
            <div>
                <label for="ms-meal">Meal</label>
                <select id="ms-meal" name="meal" required>
                    <option value="">&#8212; Select Meal &#8212;</option>
                    <option value="Breakfast">Breakfast</option>
                    <option value="Lunch">Lunch</option>
                    <option value="Dinner">Dinner</option>
                </select>
            </div>
            <div class="meal-form-full">
                <label for="ms-menu">What you&#39;re bringing</label>
                <input id="ms-menu" name="menu" type="text" required
                       placeholder="e.g. Potato salad and lemonade">
            </div>
        </div>
        <button type="submit" class="meal-signup-submit">Add to Schedule</button>
    </form>
</details>
HTML;

// Assemble page content
$activitiesPage          = new Page();
$activitiesPage->title    = 'Activities - WF Reunion';
$activitiesPage->keywords = 'family reunion activities, auction, games, food assignments, Warburton family reunion';

$activitiesPage->content = <<<'HTML'
<div id="activities">
    <img src="WF1.png" alt="temp pic">
    <h1><strong>What we have planned for this coming Campout</strong></h1>
    <ul>We have a variety of activities and assignments planned for the Warburton
         Family Reunion. <br>Here is what we have for this years campout!!</ul>
</div>
<div id="Auction">
    <img src="WF2.png" alt="temp pic">
    <h1><strong>Auction Fundraiser</strong></h1>
    <p><strong>Auctioneer this year is Heidi Hansen</strong></p>
    <p>As always, We will be holding an auction to raise funds for the reunion.
        For the Auction part of the campout, please consider bringing quality
        items for the auction. Popular items include handmade crafts, collectibles,
        gift baskets, and useful items that can be enjoyed by all. Please ensure
        that the items you bring are in good condition and are suitable for an auction.
        If the item is trash to you, it probably won't fetch a good price at auction!
        Your contributions will help make this event a success and support future
        reunions. Take this opportunity to declutter and contribute to the success
        of our reunion.
    </p>
</div>
<div id="Games">
    <img src="WF3.png" alt="temp pic">
    <h1><strong>Games and Activities</strong></h1>
    <p><strong>Games and Activities Coordinator this year is <br>Randy Dussler</br></strong></p>
    <p>We have planned several games and activities to keep everyone entertained
        during the reunion. These activities are designed to foster camaraderie and
        create lasting memories among family members. We have a variety of games and
        activities planned for all age groups. The main activities include horse shoes
        tournament, Corn hole tournament, and a rock shape contest (design of rocks
        found around the campout area). We encourage everyone to participate and have fun!
    </p>
</div>
<div id="FoodAssignments">
    <img src="img.png" alt="temp pic">
    <h1><strong>Food Assignments</strong></h1>
    <p>It is encouraged to create a delicious and diverse menu. We have assigned meal
        times and day(s) to each family to prepare. It is the responsibility of each
        family to provide necessary items for their assigned meals. We appreciate your
        cooperation in making sure we have a variety of delicious meals and snacks for
        everyone to enjoy. Please refer to the schedule below to see what your family
        is responsible for bringing:</p>
</div>
HTML;

$activitiesPage->content .= '<div id="calendar">'
    . '<h1><strong>Meal Assignment Schedule</strong></h1>'
    . $tableHtml
    . $formHtml
    . '</div>';

$activitiesPage->content .= <<<'HTML'
<h2><strong>Thank you for all who have contributed to making this reunion a success!
    We are grateful for all the support and continued enthusiasm. See you all at the
    reunion! It is important to remember that this reunion is about spending quality
    time together and creating lasting memories. Let's make the most of this special
    occasion and time that we have together as a family. Safe travels to everyone,
    and we look forward to seeing you all soon!</strong></h2>
HTML;

$activitiesPage->Display();
exit;
// ?>

//     $activitiesPage -> title = "Activities - WF Reunion";
//     $activitiesPage -> keywords = "family reunion activities, auction, games, food assignments, Warburton family reunion";

//     $activitiesPage -> content = <<<'HTML'

// <div id="activities">
//     <img src="WF1.png" alt="temp pic">
//     <h1><strong>What we have planned for this coming Campout</strong></h1>
//         <ul>We have a variety of activities and assignments planned for the Warburton
//              Family Reunion. <br>Here is what we have for this years campout!!</ul>
// </div>  
//     <div id="Auction">
//         <img src="WF2.png" alt="temp pic">
//              <h1><strong>Auction Fundraiser</strong></h1>
//             <p> <strong>Auctioneer this year is Heidi Hansen</strong></p>
//                 <p>As always, We will be holding an auction to raise funds for the reunion.
//                      For the Auction part of the campout, please consider bringing quality
//                       items for the auction.
//                     Popular items include handmade crafts, collectibles, gift baskets, and 
//                     useful items that can be enjoyed by all. Please ensure that the items 
//                     you bring are in good condition
//                     and are suitable for an auction. If the item is trash to you, it 
//                     probably won't fetch a good price at auction!
//                     Your contributions will help make this event a success and support 
//                     future reunions. Take this opportunity to declutter and contribute to 
//                     the success of our reunion.
//                 </p>
//             </div>
//             <div id="Games">
//                 <img src="WF3.png" alt="temp pic">
//                 <h1><strong>Games and Activities</strong></h1>
//                 <p> <strong>Games and Activities Coordinator this year is 
//                     <br>Randy Dussler</br></strong></p>
//                <p>We have planned several games and activities to keep everyone entertained
//                  during the reunion. These activities are designed to foster camaraderie
//                   and create 
//                     lasting memories among family members.

//                     We have a variety of games and activities planned for all age groups. 
//                     The main activities include horse shoes tournement, Corn hole tournement,
//                      and a rock shape contest (design of rocks found around the campout
//                       area). We encourage everyone to participate and have fun!
//                 </p>
//             </div>
//             <div id="FoodAssignments">
//                 <img src="img.png" alt="temp pic">
//                 <h1><strong>Food Assignments</strong></h1>
//                 <p>It is encouraged to create a delicious and diverse menu. We have assigned meal times and day(s) to each family to prepare. It is the responsibility of each family to provide necessary "items" for their assigned meals. We appreciate your cooperation in making sure we have a variety of delicious
//                      meals and snacks for everyone to enjoy. Please refer to the assignment list below to see
//                     what your Family is responsible for bringing: </p>
//             </div>
               
//             <div id="calendar" >
//                  <h1><strong>Meal Assignment Schedule</strong></h1>
//                 <table>
//                      <tr>
//                           <th>Meal</th>
//                           <th>Thursday</th>
//                           <th>Friday</th>
//                           <th>Saturday</th>
//                           <th>Sunday</th>
//                      </tr>
//                      <tr>
//                           <td>Breakfast</td>
//                           <td>N/A</td>
//                           <td>Sorenson Family eggs, bacon, sulders</td>
//                           <td>Dussler Family Pancakes, sausages, and fruit</td>
//                           <td>Individual Family Meal Continental breakfast — pastries, fruit, beverages</td>
//                      </tr>
//                      <tr>
//                           <td>Lunch</td>
//                           <td>N/A</td>
//                           <td>Andre Family Steaks and sides</td>
//                           <td>Rennert Family Sandwiches, salads, and chips</td>
//                           <td>N/A</td>
//                      </tr>
//                      <tr>
//                           <td>Dinner</td>
//                           <td>Hansen Family grilled meats, casseroles</td>
//                           <td>Randal Family Pasta and garlic bread</td>
//                           <td>Jayme Family BBQ ribs, corn on the cob, and baked beans</td>
//                           <td>N/A</td>
//                      </tr>
//                 </table>
//             </div>

//                <h2><strong>Thank you for all who have contributed to making this reunion 
//                 a success! We are grateful for all the suh2port and continued enthusiasm.
//                  See you all at the reunion! It is
//                  important to remember that this reunion is about spending quality time 
//                  together and creating lasting memories. Let's make the most of this
//                   special occasion and time that we have together as a family. Safe 
//                   travels to everyone, and we look forward to seeing you all soon!
//                </strong></h2>
            
// HTML;

//  $schedule = [
//                     'Breakfast' => [
//                         'Thursday' => null,
//                         'Friday'   => ['family' => 'Sorenson Family', 'menu' => 'eggs, bacon, sulders'],
//                         'Saturday' => ['family' => 'Dussler Family',  'menu' => 'Pancakes, sausages, and fruit'],
//                         'Sunday'   => ['family' => 'Individual Family Meal',    'menu' => 'Continental breakfast — pastries, fruit, beverages'],
//                                     ],
//                     'Lunch' => [
//                         'Thursday' => null,
//                         'Friday'   => ['family' => 'Andre Family',    'menu' => 'Steaks and sides'],
//                         'Saturday' => ['family' => 'Rennert Family',  'menu' => 'Sandwiches, salads, and chips'],
//                         'Sunday'   => null,
//                                 ],
//                     'Dinner' => [
//                         'Thursday' => ['family' => 'Hansen Family',   'menu' => 'grilled meats, casseroles'],
//                         'Friday'   => ['family' => 'Randal Family',   'menu' => 'Pasta and garlic bread'],
//                         'Saturday' => ['family' => 'Jayme Family',    'menu' => 'BBQ ribs, corn on the cob, and baked beans'],
//                         'Sunday'   => null,
//                                 ],
//                             ];

//                     $days = ['Friday', 'Saturday', 'Sunday'];

//     $activitiesPage -> Display();
// ?>
