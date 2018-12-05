<?php
   require_once '../../config/config.php';

   class MGeneral{

      public function __construct(){
      }

        public function Listar_Estados($tipo){
         $sql="CALL `SP_ESTADO_LISTAR`('$tipo');";
         return ejecutarConsulta($sql);
       }
        public function Listar_Persona_Todo(){
         $sql="CALL `SP_PERSONA_LISTAR_TODO`(); ";
         return ejecutarConsulta($sql);
       }

       public function Listar_Personas_Sin_Usuario(){
         $sql="CALL `SP_PERSONAS_LISTAR_SIN_USUARIOS`();";
         return ejecutarConsulta($sql);
       }
		public function Listar_Personas_Todo(){
         $sql="select * from persona";
         return ejecutarConsulta($sql);
       }
        public function Listar_Perfiles(){
         $sql="CALL `SP_PERFIL_LISTAR`();";
         return ejecutarConsulta($sql);
       }

		public function Listar_Materiales(){
         $sql="SELECT * FROM material";
         return ejecutarConsulta($sql);
       }

       public function Listar_Personas_Trabajadores(){
         $sql="select * from persona p INNER JOIN usuario u On u.Persona_idPersona=p.idPersona INNER JOIN perfil per On per.idPerfil=u.Perfil_idPerfil WHERE per.idPerfil=8";
         return ejecutarConsulta($sql);
       }





   }

?>
