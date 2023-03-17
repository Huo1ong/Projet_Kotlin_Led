<?php 
try
{
    $db = new PDO("mysql:host=localhost;dbname=projet_led_quentin","root","");
}
catch(Exception $e)
{
    die("Erreur de conenxion ".$e->getMessages());
}