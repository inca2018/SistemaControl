<?php
   require_once '../../config/config.php';

   class MCalidad{

      public function __construct(){
      }


	  public function Listar_calidad(){
           $sql="CALL `SP_OVILLADO_LISTAR`();";
           return ejecutarConsulta($sql);
       }
  public function Listar_GestionOvillado($idOrden){
           $sql="CALL `SP_OVILLADO_LISTAR_GESTION`('$idOrden');";
           return ejecutarConsulta($sql);
       }

   }

?>
