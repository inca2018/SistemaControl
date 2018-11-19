<?php
   require_once '../../config/config.php';

   class MEnconado{

      public function __construct(){
      }

   public function RecuperarCorrelativo(){
		$sql="SELECT COUNT(*) as correlativo FROM orden";
		return ejecutarConsultaSimpleFila($sql);
	}

	  public function Listar_Enconado(){
           $sql="CALL `SP_ORDEN_LISTAR`();";
           return ejecutarConsulta($sql);
       }
      public function Eliminar_Enconado($idEnconado,$codigo,$idCreador){
           $sql="CALL `SP_ORDEN_HABILITACION`('$idEnconado','$codigo','$idCreador');";

           return ejecutarConsulta($sql);
       }

      public function RegistroEnconado($idEnconado,$EnconadoNombre,$EnconadoMaterial,$EnconadoLote,$EnconadoKilos,$EnconadoNumero,$login_idLog){
        $sql="";

        if($idEnconado=="" || $idEnconado==null || empty($idEnconado)){
             $sql="CALL `SP_ORDEN_REGISTRO`('$EnconadoNombre','$EnconadoMaterial','$EnconadoLote','$EnconadoKilos','$EnconadoNumero','$login_idLog');";

        }else{

             $sql="CALL `SP_ORDEN_ACTUALIZAR`('$EnconadoNombre','$EnconadoMaterial','$EnconadoLote','$EnconadoKilos','$EnconadoNumero','$login_idLog','$idEnconado');";
        }

         return ejecutarConsulta($sql);
      }

		public function Recuperar_Enconado($idEnconado){
			$sql="CALL `SP_ORDEN_RECUPERAR`('$idEnconado');";
			return ejecutarConsultaSimpleFila($sql);
		}



   }

?>
