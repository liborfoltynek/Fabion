<?php
header('Content-Type: image/x-png'); //or whatever
$id=0;
if (!empty($_GET["login"]))
	$id = $_GET["login"];

if (file_exists("users/$id.png")) 
	{
		readfile("users/$id.png");
	}
	else {
		if (file_exists("users/$id.jpg")) 
		{
		readfile("users/$id.jpg");
		}
		else
		{
		readfile("users/0.png");
		}
	}
die();

?>