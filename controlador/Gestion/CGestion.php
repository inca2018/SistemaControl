<?php
   session_start();
   require_once "../../modelo/Gestion/MGestion.php";
   require_once "../../modelo/General/MGeneral.php";
   require_once "../../config/conexion.php";
   require_once "../../../php/PasswordHash.php";

    $gestion = new MGestion();
    $general = new MGeneral();
    $recursos = new Conexion();


	 $login_idLog=$_SESSION['idUsuario'];

    $fechaInicio=isset($_POST["fechaInicio"])?limpiarCadena($_POST["fechaInicio"]):"";
    $fechaFin=isset($_POST["fechaFin"])?limpiarCadena($_POST["fechaFin"]):"";

    // GESTION DE PERFIL
    $UsuarioCorreo=isset($_POST["UsuarioCorreo"])?limpiarCadena($_POST["UsuarioCorreo"]):"";
    $UsuarioContacto=isset($_POST["UsuarioContacto"])?limpiarCadena($_POST["UsuarioContacto"]):"";
    $idUsuario=isset($_POST["idUsuario"])?limpiarCadena($_POST["idUsuario"]):"";

    $UsuarioPassVerificar=isset($_POST["UsuarioPassVerificar"])?limpiarCadena($_POST["UsuarioPassVerificar"]):"";
    $UsuarioPassNuevo=isset($_POST["UsuarioPassNuevo"])?limpiarCadena($_POST["UsuarioPassNuevo"]):"";


    $Inicio=isset($_POST["Inicio"])?limpiarCadena($_POST["Inicio"]):"";
    $Fin=isset($_POST["Fin"])?limpiarCadena($_POST["Fin"]):"";


    $date = str_replace('/', '-', $fechaInicio);
    $fechaInicio = date("Y-m-d", strtotime($date));
 	 $date = str_replace('/', '-', $fechaFin);
    $fechaFin = date("Y-m-d", strtotime($date));


    $date = str_replace('/', '-', $Inicio);
    $Inicio = date("Y-m-d", strtotime($date));

    $date = str_replace('/', '-', $Fin);
    $Fin = date("Y-m-d", strtotime($date));

function Verificar($reg){
    if($reg->TotalEntregado==0){
        return number_format(0,2);
    }else{
        return  number_format(round(($reg->TotalEntregado/$reg->TotalPedido)*100,2),1);
    }

}
function Verificar2($reg){
    if($reg->Promedio1==0){
        return number_format(0,2);
    }else{
        return  number_format(round($reg->Promedio1,2),1);
    }

}


function VerificarPendiente($reg,$cont){
    if($cont==1){
        return 0;
    }else{
        return number_format($reg->TotalAnterior-$reg->ResultadoAlcanzadoAnterior,2);
    }
}

function VerificarProduccion($reg,$cont){
        return number_format($reg->ProduccionDia,2);
}

function VerificarTotal($reg,$cont){

        return number_format($reg->ProduccionDia-$reg->ResultadoAlcanzado+($reg->ProAnterior-$reg->ResultadoAlcanzadoAnterior),2);

}

function VerificarTotal2($reg,$cont){

        return number_format($reg->ProduccionDia+($reg->ProduccionDia-$reg->ResultadoAlcanzado+($reg->ProAnterior-$reg->ResultadoAlcanzadoAnterior)),2);

}

function VerificarTotal3($reg,$cont){
     if($reg->ResultadoAlcanzado==0){
        return 0;
    }else{
        $resuTotal=$reg->ProduccionDia+($reg->ProduccionDia-$reg->ResultadoAlcanzado+($reg->ProAnterior-$reg->ResultadoAlcanzadoAnterior));
        return number_format(($reg->ResultadoAlcanzado/$resuTotal)*100,2);
    }
}




   switch($_GET['op']){
       case 'RecuperarDatosPerfil':
         $rspta=$gestion->RecuperarDatosPerfil($login_idLog);
         echo json_encode($rspta);
      break;

     case 'ActualizarPerfil':
           $rspta = array("Mensaje"=>"","Registro"=>false,"Error"=>false);

           $hasher=new PasswordHash(8,false);
           $hash=$gestion->password($idUsuario);
           $hash=$hash['pass'];

           if($UsuarioPassVerificar=='' || $UsuarioPassVerificar==null){
                $rspta["Registro"]=$gestion->actualizar_datos_perfil($idUsuario,$UsuarioCorreo,$UsuarioContacto,$UsuarioPassNuevo,1);
               $rspta["Mensaje"]="Datos del Perfil Actualizado Correctamente.";
           }else{

                if($hasher->CheckPassword($UsuarioPassVerificar,$hash)==1){

                  $UsuarioPassword = $hasher->HashPassword($UsuarioPassNuevo);
                  $rspta["Registro"]=$gestion->actualizar_datos_perfil($idUsuario,$UsuarioCorreo,$UsuarioContacto,$UsuarioPassword,2);
                  $rspta["Mensaje"]="Datos del Perfil Actualizado Correctamente.";

               }else{
                 $rspta["Registro"]=false;
                 $rspta["Mensaje"]="Contraseña anterior incorrecta";
               }
           }


         echo json_encode($rspta);
      break;



     case 'ListarReportes1':
         $rspta=$gestion->BuscarReporteIndicadores1($Inicio,$Fin);
         $data= array();
         $count=1;
         while ($reg=$rspta->fetch_object()){
         $data[]=array(
               "0"=>"",
               "1"=>$reg->Fecha,
               "2"=>$reg->Material,
               "3"=>number_format($reg->TotalEntregado,2),
               "4"=>number_format($reg->TotalPedido,2),
               "5"=>Verificar($reg),
               "6"=>Verificar2($reg)
            );
         }
         $results = array(
            "sEcho"=>1, //Información para el datatables
            "iTotalRecords"=>count($data), //enviamos el total registros al datatable
            "iTotalDisplayRecords"=>count($data), //enviamos el total registros a visualizar
            "aaData"=>$data);
         echo json_encode($results);
      break;

     case 'ListarReportes2':
         $rspta=$gestion->BuscarReporteIndicadores2($Inicio,$Fin);
         $data= array();
         $count=1;
         while ($reg=$rspta->fetch_object()){
         $data[]=array(
               "0"=>"",
               "1"=>$reg->Fecha,
               "2"=>$reg->Persona,
               "3"=>number_format($reg->ResultadoAlcanzado,2),
               "4"=>number_format($reg->ProduccionDia,2),
               "5"=>VerificarPendiente($reg,$count),
               "6"=>VerificarProduccion($reg,$count),
                "7"=>VerificarTotal($reg,$count),
                "8"=>VerificarTotal2($reg,$count),
                "9"=>VerificarTotal3($reg,$count)
            );
            $count++;
         }
         $results = array(
            "sEcho"=>1, //Información para el datatables
            "iTotalRecords"=>count($data), //enviamos el total registros al datatable
            "iTotalDisplayRecords"=>count($data), //enviamos el total registros a visualizar
            "aaData"=>$data);
         echo json_encode($results);
      break;


   }


?>
