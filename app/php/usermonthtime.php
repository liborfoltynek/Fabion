<?php

   $db = new PDO('sqlite:../data.s3db');  
   $login = $_GET["l"];   
   $m = $_GET["m"];   
   $y = $_GET["y"];
  
   if ((strpos($login, " ") === true) ||
       (strpos($login, "'") === true) ||
       (strpos($login, "\"") === true) ||
       (strpos($login, "*") === true) ||
       (strpos($login, ";") === true))
   $login = "4dfd564d65fs5f4s564f65sd4d5s";  // asi pokus o sql injection
   
   $q = "select sum((strftime('%s',event.timeto) - strftime('%s',event.timefrom))) / 3600.0 as total from event inner join users on users.id=event.user where event.month=$m and event.year = $y and users.login='$login' and users.password = '" . $_GET["p"] . "' group by user having user=users.id";

   $result = $db->query($q);

   foreach($result as $row) 
   {
     $u->sum = $row[0];
	 break;
   }
      
header('Content-Type: application/json');  
echo json_encode($u);
   
?>