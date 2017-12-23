<?php
class Event
{
  public $id;
  public $userid;
  public $username;
  public $timefrom;
  public $timeto;
  public $day;
  public $month;
  public $year;
  public $subject;
  public $calendarEventId;
  public $note;
  public $login;
}

header('Content-Type: application/json');


$db = new PDO('sqlite:../data.s3db');
//$sql = "select * from event where day = 20 and month = 10 and year = 2017";


$login=$_GET["l"];
$password = $_GET["p"];
$sql = "select id from users where login = '$login' and password='$password'";
$result = $db->query($sql);	
$dbUserId = -1;
foreach($result as $row)
{
	$dbUserId = $row["id"];
}
if ($dbUserId == -1)
{
	$r->result = "Neznámý uživatel";
	die(json_encode($r));
}	


$year = date("Y");
$month =  date("m");
$day =  date("d");
$dday = "day = $day and ";

if (!empty($_GET["y"]) && !empty($_GET["m"]) && !empty($_GET["d"]))
{
	$year = $_GET["y"];
	$month = $_GET["m"];
	$day = $_GET["d"];
	$dday = "day = $day and ";
}

if (!empty($_GET["y"]) && !empty($_GET["m"]) && empty($_GET["d"]))
{
	$year = $_GET["y"];
	$month = $_GET["m"];
	$day = $_GET["d"];
	$dday = "";
}

$sql = "select e.id, e.day, strftime(\"%H:%M\", e.timefrom) as timefrom, strftime(\"%H:%M\", e.timeto) as timeto, e.calendarEventId, e.user, e.subject, e.note, u.login as login from event as e left join users as u on u.id = e.user where $dday month=$month and year=$year order by day, timefrom";

$result = $db->query($sql);

$cnt = 0;
$events = array();
foreach($result as $row)
{	
   $ev = new Event();
   $ev->id = $row["id"];
   $ev->username = $row["login"];
   $ev->day = $row["day"];
   $ev->month = $month;
   $ev->year = $year;
   $ev->timefrom = $row["timefrom"];
   $ev->timeto = $row["timeto"];
   $ev->userid = $row["user"];
   $ev->subject = $row["subject"];
   $ev->note = $row["note"];
   $ev->login = $row["login"];
   $ev->calendarEventId = $row["calendarEventId"];
   
   array_push($events, $ev);  
}
$data->events = $events;

echo json_encode($data);

?>