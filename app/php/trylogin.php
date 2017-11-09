<?php
class FabionUser
{
	public $id;
	public $login;
	public $name; 
	public $email; 
	public $phone;
	public $freehours;
	public $admin;
	public $candelete;
	public $ok;
}
   $db = new PDO('sqlite:../data.s3db');
   
   $login = $_GET["l"];

   $logfile = fopen("log.txt", "a"); 
   fwrite($logfile, date('Y-m-d') . " " . date("H:i:s") . ": [LOGIN] "  . $login . "\n");
   fclose($logfile);     
  
   if ((strpos($login, " ") === true) ||
       (strpos($login, "'") === true) ||
       (strpos($login, "\"") === true) ||
       (strpos($login, "*") === true) ||
       (strpos($login, ";") === true))
   $login = "4dfd564d65fs5f4s564f65sd4d5s";  // asi pokus o sql injection
   
   $q = "select id, login, name, phone, email, freehours, admin, candelete from users where login = '$login' and password = '" . $_GET["p"] . "'";

   $result = $db->query($q);
   $u = new FabionUser(); 
   $u->id = -1;
   $u->ok = 0;
   $u->freehours = 0;
   foreach($result as $row) 
   {
     $u->id = $row["id"];
     $u->login = $row["login"];
     $u->name = $row["name"];
	 $u->email = $row["email"];
	 $u->phone = $row["phone"];
     $u->freehours = $row["freehours"];
     $u->admin = $row["admin"];
     $u->candelete = $row["candelete"];
	 $u->ok = 1;
	 break;
   }
   
   
header('Content-Type: application/json');  
echo json_encode($u);
   
?>