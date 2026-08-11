"use strict"

/*
  
   Clock
   Author:Chase Rennert
   Date:11/11/2025
    Filename: ClockCountdown.js
*/
/* Execute and Run clock function */
runClock();

setInterval(runClock, 1000);

/* Creates and runs the Clock */

function runClock() {

   /* Get the current date and time */
   var currentDay = new Date();

   var thisDateStr = currentDay.toLocaleDateString();
   var thisTime = currentDay.toLocaleTimeString();
   var thisDayNum = currentDay.getDate();
   var thisWeekDayStr = getWeekDay(currentDay.getDay());
   


   
   
   document.getElementById("thisDate").textContent = thisDateStr;
   document.getElementById("thisWeekDay").textContent = thisWeekDayStr;
   document.getElementById("thisTime").textContent = thisTime;
   //document.getElementById("displayEventDate").textContent = ;
    displayEventDate(currentDay);   
   }

   function displayEventDate(currentDay) {
   /* Get the event date and time */
   var eventDate = new Date("June 10, 2027 12:00:00");
   var dateStr = eventDate.toLocaleDateString();
   var timeStr = eventDate.toLocaleTimeString();

 /* Calculate precise time remaining */
    var diffMs = eventDate.getTime() - currentDay.getTime();
    if (diffMs < 0) diffMs = 0; // clamp if event has started/passed

    var dayMs = 24 * 60 * 60 * 1000;
    var hourMs = 60 * 60 * 1000;
    var minMs = 60 * 1000;
    var secMs = 1000;

    var days = Math.floor(diffMs / dayMs);
    var remainderAfterDays = diffMs % dayMs;
    var hours = Math.floor(remainderAfterDays / hourMs);
    var remainderAfterHours = remainderAfterDays % hourMs;
    var minutes = Math.floor(remainderAfterHours / minMs);
    var seconds = Math.floor((remainderAfterHours % minMs) / secMs);

    function pad(n) { return n.toString().padStart(2, '0'); }

   // Display the event date with optional status
    var statusMsg = (diffMs === 0) ? "<br/><span>It's Reunion time!</span>" : "";
    document.getElementById("eventDate").innerHTML = dateStr + "<br />" + timeStr + statusMsg;
   
   /* Display time left until event */
    document.getElementById("daysLeft").innerHTML = days + " <br/> Days";
    document.getElementById("hrsLeft").innerHTML = pad(hours) + " <br/>Hours";
    document.getElementById("minsLeft").innerHTML = pad(minutes) + " <br/> Minutes";
    document.getElementById("secsLeft").innerHTML = pad(seconds) + " <br/> Seconds";

   //displayEventDate();
   
}

function getWeekDay(thisDayNum) {
   var wDays = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
   return wDays[thisDayNum];
}

// sample of taking an element off a page after load
// setTimeout(
//    document.getElementById("eventDate").remove()
// ), 8000;