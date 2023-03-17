<?php 
require_once("connexion.php");

$ps=$db->query("SELECT * FROM couleurs");

$response = array();
while($d=$ps->fetch(PDO::FETCH_ASSOC))
{
    $response[]=$d;
}

echo json_encode($response);