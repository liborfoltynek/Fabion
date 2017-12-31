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
  public $note;
  public $login;
  public $calendarEventId;
}

header('Content-Type: application/json');
$db = new PDO('sqlite:../data.s3db');

if (empty($_GET["action"]))
{
	$r->result = "unknown action";
	die (json_encode($r));	
}
$action = $_GET["action"];

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

if ($action == "n")
{	
	$timeFrom=$_GET["tf"];
	$timeTo=$_GET["tt"];
	$subject=$_GET["s"];
	$note=$_GET["n"];
	$y=$_GET["y"];
	$m=$_GET["m"];
	$d=$_GET["d"];
	if (!empty($_GET["cid"]))
		$calendarEventId=$_GET["cid"];
	else
		$calendarEventId = -1;
	
	$created = date('Y-m-d H:i:s');
	
 $q = "select count(*) from event where day=$d and month=$m and year=$y and (( (timefrom < time('$timeFrom')) and (timeto > time('$timeFrom'))) or ((timefrom < time('$timeTo')) and (timeto > time('$timeTo')) ) or ( (timefrom > time('$timeFrom')) and (timeto < time('$timeTo')) ) or ( (timefrom = time('$from')) and (timeto = time('$to')) ))";
 
 $result = $db->query($q);
  $cnt = 0;
  foreach ($result as $row)
  {
   $cnt = $row[0];
  }
  settype($cnt, "integer");
  
   $logfile = fopen("log.txt", "a"); 
   fwrite($logfile, date('Y-m-d') . " " . date("H:i:s") . ": [EVENT] NEW "  . $login . "\n");
   fclose($logfile);     
  
  if ($cnt == 0)
	{
		$q = "insert into event (day, month, year,timefrom, timeto, user, subject, note, created, info, calendarEventId) values ($d, $m, $y, time('$timeFrom'), time('$timeTo'), $dbUserId, '$subject', '$note', '$created', '$note', $calendarEventId)";
		$result = $db->exec($q);

		$r->result = "ok";
		$q = "select max(id) as eid from event where day=$d and month=$m and year=$y and subject='$subject' and timefrom=time('$timeFrom') and timeto=time('$timeTo')"; 
		 
		 $logfile = fopen("log.txt", "a"); 
		fwrite($logfile, date('Y-m-d') . " " . date("H:i:s") . ": [EVENT] NEW ID QUESY="  . $q . "\n");
		fclose($logfile);     
		
		$result = $db->query($q);  
		foreach ($result as $row)
		{
		  $r->eventId = $row[0];
		}				
				
		die(json_encode($r));
	}
	else	  
	{
		$r->result = "Kolize s jinou rezervací";
		$r->eventId = -1;
		die(json_encode($r));
	}
} 

$eventId = $_GET["id"];	
$sql = "select user from event where id=$eventId";
$eventDbUserId = -1;
$result = $db->query($sql);	
foreach($result as $row)
{
	$eventDbUserId = $row["user"];
}
if ($eventDbUserId == -1)
{
	$r->result= "wrong event id";
	die(json_encode($r));
}
if ($eventDbUserId != $dbUserId)
{
	$r->result= "access denied";
	die(json_encode($r));
}

if ($action == "d")
{	
	$sql = "delete from event where id=$eventId";
	$result = $db->query($sql);	
	
	$r->result = "ok";
	
	$logfile = fopen("log.txt", "a"); 
    fwrite($logfile, date('Y-m-d') . " " . date("H:i:s") . ": [EVENT] DELETE "  . $login . "\n");
    fclose($logfile);     
   
	die (json_encode($r));
}

if ($action == "u")
{	
	$timeFrom=$_GET["tf"];
	$timeTo=$_GET["tt"];
	$subject=$_GET["s"];
	$note=$_GET["n"];
	$y=$_GET["y"];
	$m=$_GET["m"];
	$d=$_GET["d"];
	$created = date('Y-m-d H:i:s');
	
 $q = "select count(*) from event where  id <> $eventId and day=$d and month=$m and year=$y and (( (timefrom < time('$timeFrom')) and (timeto > time('$timeFrom'))) or ((timefrom < time('$timeTo')) and (timeto > time('$timeTo')) ) or ( (timefrom > time('$timeFrom')) and (timeto < time('$timeTo')) ) or ( (timefrom = time('$timeFrom')) and (timeto = time('$timeTo')) ))";
 
 $result = $db->query($q);
  $cnt = 0;
  foreach ($result as $row)
  {
   $cnt = $row[0];
  }
  settype($cnt, "integer");
  
  if ($cnt == 0)
	{
		$q = "update event set day=$d, month=$m, year=$y, timefrom=time('$timeFrom'), timeto=time('$timeTo'), user=$dbUserId, subject='$subject', note='$note' where id=$eventId";
		$result = $db->exec($q);
  
		$r->result = "ok";
		$r->eventId = $eventId;
		$logfile = fopen("log.txt", "a"); 
		fwrite($logfile, date('Y-m-d') . " " . date("H:i:s") . ": [EVENT] UPDATE "  . $login . "\n");
		fclose($logfile);     
   
		die(json_encode($r));
	}
	else	  
	{
		$r->result = "Kolize s jinou rezervací";
		$r->eventId = $eventId;
		die(json_encode($r));
	}	
}

$r->result = "unknown action";
die (json_encode($r));	
?>