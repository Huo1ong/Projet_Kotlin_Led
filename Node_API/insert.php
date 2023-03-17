<?php 
require_once("connexion.php");

extract($_POST);

$ps = $db->prepare("INSERT INTO couleurs(id, couleurs) VALUES (?, '?')");
$ps ->execute(array($id ,$couleurs));

if($ps->rowCount()>0) 
{
    echo "Insertion effectuée avec succès !";
}
else 
{
    echo "Erreur d'insertion.";
}