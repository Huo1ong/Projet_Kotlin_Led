<?php 
require_once("connexion.php");

$id=$_GET['id'];

$ps = $db->query("DELETE FROM couleurs WHERE id= $id");

if($ps->rowCount()>0) 
{
    echo "Suppression effectuée avec succès !";
}
else 
{
    echo "Erreur de suppression.";
}