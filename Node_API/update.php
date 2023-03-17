<?php 
require_once("connexion.php");

extract($_POST);

$ps = $db->prepare("UPDATE couleurs SET couleurs= ? WHERE id= ?");
$ps ->execute(array($id, $couleurs));

if($ps->rowCount()>0) 
{
    echo "Modification effectuée avec succès !";
}
else 
{
    echo "Erreur de modification.";
}