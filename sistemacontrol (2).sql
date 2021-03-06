-- phpMyAdmin SQL Dump
-- version 4.7.9
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1:3306
-- Tiempo de generación: 21-11-2018 a las 09:16:06
-- Versión del servidor: 5.7.21
-- Versión de PHP: 5.6.35

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `sistemacontrol`
--

DELIMITER $$
--
-- Procedimientos
--
DROP PROCEDURE IF EXISTS `SP_CALIDAD_LISTAR`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_CALIDAD_LISTAR` ()  NO SQL
BEGIN 

SELECT o.idOrden,o.NumOrden,ma.Descripcion,o.Lote,o.Kilos,o.NumConos,o.Estado_idEstado,e.nombreEstado,DATE_FORMAT(o.fechaRegistro,"%d/%m/%Y") as fechaRegistro FROM orden o INNER JOIN estado e ON e.idEstado=o.Estado_idEstado INNER JOIN material ma On ma.idMaterial=o.Material_idMaterial WHERE o.Estado_idEstado=9 or o.Estado_idEstado=8 or o.Estado_idEstado=7 or o.Estado_idEstado=6 ORDER BY o.idOrden DESC;

END$$

DROP PROCEDURE IF EXISTS `SP_ESTADO_LISTAR`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_ESTADO_LISTAR` (IN `Tipo` INT(11))  NO SQL
BEGIN 

Select * FROM estado e WHERE e.tipoEstado=Tipo;

END$$

DROP PROCEDURE IF EXISTS `SP_GESTION_ACTUALIZAR`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_GESTION_ACTUALIZAR` (IN `idTrabajo` INT(11), IN `peso` DECIMAL(10,2), IN `lote` VARCHAR(100), IN `ordenTra` VARCHAR(100), IN `cant` INT(11), IN `idpersona` INT(11), IN `Obs` TEXT)  NO SQL
BEGIN 

if(Obs="-1")then 
set Obs=null;
end if;

UPDATE `ovillado` SET `PesoOvillo`=peso,`LoteOvillo`=lote,`NumOrden`=ordenTra,`Cantidadovillos`=cant,`Persona_idPersona`=idpersona,`Observaciones`=Obs WHERE `idOvillado`=idTrabajo;  
 
END$$

DROP PROCEDURE IF EXISTS `SP_GESTION_RECUPERAR`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_GESTION_RECUPERAR` (IN `idOrDENe` INT(11))  NO SQL
BEGIN 

SELECT o.idOvillado,o.PesoOvillo,o.LoteOvillo,o.NumOrden as CodigoTrabajo,o.Cantidadovillos,o.Persona_idPersona,ma.Descripcion as NombreMaterial,o.Observaciones FROM ovillado o INNER JOIN material ma On ma.idMaterial=o.Material_idMaterial WHERE O.idOvillado=idOrDENe; 
END$$

DROP PROCEDURE IF EXISTS `SP_GESTION_REGISTRO`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_GESTION_REGISTRO` (IN `idOrdenR` INT(11), IN `pesoO` DECIMAL(10,2), IN `loteOv` VARCHAR(100), IN `NumTrabajo` VARCHAR(100), IN `idMaterialR` INT(11), IN `cantidadO` INT(11), IN `idPersonaR` INT(11), IN `Obs` TEXT)  NO SQL
BEGIN 

if(Obs="-1")THEN
set Obs=null;
end if;

INSERT INTO `ovillado`(`idOvillado`, `Orden_idOrden`, `PesoOvillo`, `LoteOvillo`, `NumOrden`, `Material_idMaterial`, `Cantidadovillos`, `Persona_idPersona`, `fechaRegistro`, `Estado_idEstado`,`Observaciones`) VALUES (NULL,idOrdenR,pesoO,loteOv,NumTrabajo,idMaterialR,cantidadO,idPersonaR,NOW(),1,Obs);
 
END$$

DROP PROCEDURE IF EXISTS `SP_LOGIN_REGISTRO`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_LOGIN_REGISTRO` (IN `idUsuario` INT(11), IN `usuario` VARCHAR(100), IN `passwordLog` TEXT, IN `perfil` VARCHAR(100))  NO SQL
BEGIN


INSERT INTO `login`(`idLogin`, `Usuario_idUsuario`, `usuarioLog`, `passwordLog`, `perfilLog`, `fechaLog`) VALUES (null,idUsuario,usuario,passwordLog,perfil,NOW());


END$$

DROP PROCEDURE IF EXISTS `SP_MATERIAL_ACTUALIZAR`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_MATERIAL_ACTUALIZAR` (IN `Descri` VARCHAR(100), IN `estado` INT(11), IN `idMaterialE` INT(11), IN `creador` INT(11))  NO SQL
BEGIN 

UPDATE `material` SET  `Descripcion`=UPPER(Descri),`Estado_idEstado`=estado WHERE `idMaterial`=idMaterialE;  

/* ------ REGISTRO DE BITACORA ------ */
SET @NombreUsuario=(SELECT u.usuario FROM usuario u WHERE u.idUsuario=creador);

INSERT INTO `bitacora`(`idBitacora`, `usuarioAccion`, `Accion`, `tablaAccion`,`Detalle`, `fechaRegistro`) VALUES (null,@NombreUsuario,'ACTUALIZACION','Material',CONCAt("SE ACTUALIZO MATERIAL:",Descri),NOW());    
END$$

DROP PROCEDURE IF EXISTS `SP_MATERIAL_HABILITACION`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_MATERIAL_HABILITACION` (IN `idMaterialE` INT(11), IN `codigo` INT(11), IN `creador` INT(11))  NO SQL
BEGIN 

if (codigo=1) then 
  
    UPDATE `material` SET `Estado_idEstado`=4  WHERE `idMaterial`=idMaterialE;
  SET @Mensaje=("MATERIAL DESHABILITADO");
else 
   UPDATE `material` SET `Estado_idEstado`=1  WHERE `idMaterial`=idMaterialE;    
 SET  @Mensaje=("MATERIAL HABILITADO");   
end if;

 /* ------ REGISTRO DE BITACORA ------ */

set @usuario=(SELECT u.usuario FROM usuario u  WHERE u.idUsuario=creador);

 

INSERT INTO `bitacora`(`usuarioAccion`, `Accion`, `tablaAccion`,`Detalle`, `fechaRegistro`) VALUES (@usuario,@Mensaje,'MATERIAL',"MATERIAL ACTUALIZAR",NOW());     
 
END$$

DROP PROCEDURE IF EXISTS `SP_MATERIAL_LISTAR`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_MATERIAL_LISTAR` ()  NO SQL
BEGIN 

SELECT * FROM material n INNER JOIN estado e ON e.idEstado=n.Estado_idEstado;

END$$

DROP PROCEDURE IF EXISTS `SP_MATERIAL_RECUPERAR`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_MATERIAL_RECUPERAR` (IN `idMaterialE` INT(11))  NO SQL
BEGIN 

SELECT * FROM material ni WHERE ni.idMaterial=idMaterialE; 
END$$

DROP PROCEDURE IF EXISTS `SP_MATERIAL_REGISTRO`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_MATERIAL_REGISTRO` (IN `nom` VARCHAR(100), IN `estado` INT(11), IN `creador` INT(11))  NO SQL
BEGIN 

-- REGISTRAR TIPO DE TARJETA --
INSERT INTO `material`(`idMaterial`, `Descripcion`, `fechaRegistro`, `Estado_idEstado`) VALUES (NULL,UPPER(nom),NOW(),estado);
 
 
 
SET @NombreUsuario=(SELECT concat(p.nombrePersona,' ',p.apellidoPaterno,' ',p.apellidoMaterno) as NombresPersona FROM usuario u inner join persona p ON p.idPersona=u.Persona_idPersona WHERE u.idUsuario=creador);


INSERT INTO `bitacora`(`idBitacora`, `usuarioAccion`, `Accion`, `tablaAccion`,`Detalle`, `fechaRegistro`) VALUES (null,@NombreUsuario,'INSERTAR','SE REGISTRO MATERIAL','MATERIAL',NOW()); 

END$$

DROP PROCEDURE IF EXISTS `SP_ORDEN_ACTUALIZAR`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_ORDEN_ACTUALIZAR` (IN `material` INT(11), IN `loteE` VARCHAR(100), IN `kilosE` DECIMAL(10,2), IN `numConoE` DECIMAL(10,2), IN `creador` INT(11), IN `idOrdenE` INT(11), IN `Obs` TEXT)  NO SQL
BEGIN 

IF(Obs="-1")then 
SET Obs=null;
end if;
UPDATE `orden` SET `Material_idMaterial`=material,`Lote`=loteE,`Kilos`=kilosE,`NumConos`=numConoE,`Observaciones`=Obs WHERE `idOrden`=idOrdenE;
  
END$$

DROP PROCEDURE IF EXISTS `SP_ORDEN_LISTAR`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_ORDEN_LISTAR` ()  NO SQL
BEGIN 

SELECT o.idOrden,o.NumOrden,ma.Descripcion,o.Lote,o.Kilos,o.NumConos,o.Estado_idEstado,e.nombreEstado,DATE_FORMAT(o.fechaRegistro,"%d/%m/%Y") as fechaRegistro,o.RechazoEnconado,DATE_FORMAT(o.fechaRechazoEnconado,"%d/%m/%Y") as fechaRechazo FROM orden o INNER JOIN estado e ON e.idEstado=o.Estado_idEstado INNER JOIN material ma On ma.idMaterial=o.Material_idMaterial WHERE o.Estado_idEstado=10 or o.Estado_idEstado=9 or o.Estado_idEstado=8 or o.Estado_idEstado=7 or o.Estado_idEstado=6 or o.Estado_idEstado=5 ORDER BY o.idOrden DESC;

END$$

DROP PROCEDURE IF EXISTS `SP_ORDEN_RECUPERAR`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_ORDEN_RECUPERAR` (IN `idOrDENe` INT(11))  NO SQL
BEGIN 

SELECT * FROM orden o WHERE O.idOrden=idOrDENe; 
END$$

DROP PROCEDURE IF EXISTS `SP_ORDEN_REGISTRO`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_ORDEN_REGISTRO` (IN `numOrdenR` VARCHAR(100), IN `idMaterialR` INT(11), IN `LoteR` VARCHAR(100), IN `KilosR` DECIMAL(10,2), IN `NumConos` DECIMAL(10,2), IN `creador` INT(11), IN `Obs` TEXT)  NO SQL
BEGIN 

if(Obs="-1")then 
SET Obs=null;
end if;

INSERT INTO `orden`(`idOrden`, `NumOrden`, `Material_idMaterial`, `Lote`, `Kilos`, `NumConos`, `fechaRegistro`, `Estado_idEstado`,`Observaciones`) VALUES (NULL,numOrdenR,idMaterialR,LoteR,KilosR,NumConos,NOW(),10,Obs);

 
END$$

DROP PROCEDURE IF EXISTS `SP_OVILLADO_LISTAR`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_OVILLADO_LISTAR` ()  NO SQL
BEGIN 

SELECT o.idOrden,o.NumOrden,ma.Descripcion,o.Lote,o.Kilos,o.NumConos,o.Estado_idEstado,e.nombreEstado,DATE_FORMAT(o.fechaRegistro,"%d/%m/%Y") as fechaRegistro,o.RechazoOvillado FROM orden o INNER JOIN estado e ON e.idEstado=o.Estado_idEstado INNER JOIN material ma On ma.idMaterial=o.Material_idMaterial WHERE o.Estado_idEstado=9 or o.Estado_idEstado=8 or o.Estado_idEstado=7 or o.Estado_idEstado=6 or o.Estado_idEstado=5 ORDER BY o.idOrden DESC;

END$$

DROP PROCEDURE IF EXISTS `SP_OVILLADO_LISTAR_GESTION`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_OVILLADO_LISTAR_GESTION` (IN `idOrdenE` INT(11))  NO SQL
BEGIN 

SELECT 
o.idOrden,
ov.idOvillado,
o.NumOrden as cod_orden,
ma.Descripcion as NombreMaterial,
CONCAT(p.nombrePersona,' ',p.apellidoPaterno,' ',p.apellidoMaterno) as Trabajador,
ov.PesoOvillo,
ov.LoteOvillo,
ov.NumOrden as cod_trabajo,
ov.Cantidadovillos,
ov.Estado_idEstado,
e.nombreEstado,
DATE_FORMAT(ov.fechaRegistro,"%d/%m/%Y") as fechaRegistro FROM orden o INNER JOIN ovillado ov On ov.Orden_idOrden=o.idOrden INNER JOIN persona p On p.idPersona=ov.Persona_idPersona INNER JOIN material ma ON ma.idMaterial=ov.Material_idMaterial INNER JOIN estado e ON e.idEstado=ov.Estado_idEstado WHERE o.idOrden=idOrdenE ORDER BY o.idOrden DESC;

END$$

DROP PROCEDURE IF EXISTS `SP_PERFIL_ACTUALIZAR`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_PERFIL_ACTUALIZAR` (IN `nombre` VARCHAR(50), IN `descripcion` TEXT, IN `estado` INT(11), IN `idperfilE` INT(11), IN `creador` INT(11))  NO SQL
BEGIN 

if(descripcion=-1)then 
SET descripcion=null;
end if;
 
UPDATE `perfil` SET `nombrePerfil`=nombre,`descripcionPerfil`=descripcion,`Estado_idEstado`=estado WHERE `idPerfil`=idperfilE; 

/* ------ REGISTRO DE BITACORA ------ */
SET @NombreUsuario=(SELECT u.user FROM usuario u WHERE u.idUsuario=creador);

INSERT INTO `bitacora`(`idBitacora`, `usuarioAccion`, `Accion`, `tablaAccion`,`Detalle`, `fechaRegistro`) VALUES (null,@NombreUsuario,'ACTUALIZACION','Perfil',CONCAt("SE ACTUALIZO PERFIL:",nombre),NOW());
END$$

DROP PROCEDURE IF EXISTS `SP_PERFIL_ELIMINAR`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_PERFIL_ELIMINAR` (IN `idPerfilEnviado` INT(11), IN `idUsuario` INT(11), OUT `Mensaje` TEXT)  NO SQL
BEGIN 
DECLARE CantidadPerfil INT(11);

SET CantidadPerfil=(SELECT COUNT(*) FROM usuario u WHERE u.Perfil_idPerfil=idPerfilEnviado);

SELECT CantidadPerfil;

if(CantidadPerfil>0) then  
    SET Mensaje="No se Puede Eliminar,Existen Usuarios usando el Perfil Seleccionado.";
else 
 	DELETE FROM `perfil`  WHERE `idPerfil`=idPerfilEnviado;
    SET Mensaje="Perfil Elimino Correctamente.";
end if;

/* ------ REGISTRO DE BITACORA ------ */
SET @NombreUsuario=(SELECT concat(p.nombrePersona,' ',p.apellidoPaterno,' ',p.apellidoMaterno) as NombresPersona FROM usuario u inner join persona p ON p.idPersona=u.Persona_idPersona WHERE u.idUsuario=idUsuario);

INSERT INTO `bitacora`(`idBitacora`, `usuarioAccion`, `Accion`, `tablaAccion`, `fechaRegistro`) VALUES (null,@NombreUsuario,'ELIMINAR','Perfil',NOW());


END$$

DROP PROCEDURE IF EXISTS `SP_PERFIL_HABILITACION`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_PERFIL_HABILITACION` (IN `idPerfilE` INT(11), IN `codigo` INT(11), IN `idUsuarioE` INT(11))  NO SQL
BEGIN 

SET @NombrePerfil=(SELECT pe.nombrePerfil FROM perfil pe WHERE pe.idPerfil=idPerfilE);


if (codigo=1) then 
 	UPDATE `perfil` SET  `Estado_idEstado`=4 WHERE `idPerfil`=idPerfilE;
  SET @Mensaje=("PERFIL DESHBILITADO");
else 
    UPDATE `perfil` SET  `Estado_idEstado`=1  WHERE `idPerfil`=idPerfilE;
 SET  @Mensaje=("PERFIL HABILITADO");
end if;

 /* ------ REGISTRO DE BITACORA ------ */

set @usuario=(SELECT u.user FROM usuario u  WHERE u.idUsuario=idUsuarioE);

INSERT INTO `bitacora`(`usuarioAccion`, `Accion`, `tablaAccion`,`Detalle`, `fechaRegistro`) VALUES (@usuario,@Mensaje,'PERFIL',CONCAT("SE",@Mensaje," :", @NombrePerfil),NOW());     
 
END$$

DROP PROCEDURE IF EXISTS `SP_PERFIL_LISTAR`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_PERFIL_LISTAR` ()  NO SQL
BEGIN 

SELECT * FROM perfil;

END$$

DROP PROCEDURE IF EXISTS `SP_PERFIL_LISTAR_TODOS`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_PERFIL_LISTAR_TODOS` ()  NO SQL
BEGIN 

SELECT * FROM perfil p INNER JOIN estado e on e.idEstado=p.Estado_idEstado;
END$$

DROP PROCEDURE IF EXISTS `SP_PERFIL_RECUPERAR`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_PERFIL_RECUPERAR` (IN `idPerfilE` INT(11))  NO SQL
BEGIN

SELECT * FROM perfil p WHERE p.idPerfil=idPerfilE;
END$$

DROP PROCEDURE IF EXISTS `SP_PERFIL_REGISTRO`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_PERFIL_REGISTRO` (IN `nombrePerfil` VARCHAR(50), IN `descripcion` TEXT, IN `estado` INT(11), IN `idUsuario` INT(11))  NO SQL
BEGIN 

DECLARE idPerfil INT(11);

-- REGISTRAR PERFIL --
INSERT INTO `perfil`(`idPerfil`, `nombrePerfil`, `descripcionPerfil`, `Estado_idEstado`, `fechaRegistro`) VALUES (null,nombrePerfil,descripcion,estado,NOW());
-- RECUPERAR ID DE PERFIL REGISTRADO --
SET idPerfil=(SELECT LAST_INSERT_ID());
-- REGISTRAR PERMISOS ASIGNADOS A PERFIL --
INSERT INTO `permisos`(`idPermisos`, `perfil_idPerfil`, `permiso1`, `permiso2`, `permiso3`, `permiso4`, `permiso5`, `permiso6`) VALUES (null,idPerfil,1,1,1,1,1,1);




SET @NombreUsuario=(SELECT concat(p.nombrePersona,' ',p.apellidoPaterno,' ',p.apellidoMaterno) as NombresPersona FROM usuario u inner join persona p ON p.idPersona=u.Persona_idPersona WHERE u.idUsuario=idUsuario);


INSERT INTO `bitacora`(`idBitacora`, `usuarioAccion`, `Accion`, `tablaAccion`,`Detalle`, `fechaRegistro`) VALUES (null,@NombreUsuario,'INSERTAR','SE REGISTRO PERFIL','Perfil',NOW());

INSERT INTO `bitacora`(`idBitacora`, `usuarioAccion`, `Accion`, `tablaAccion`,`Detalle`, `fechaRegistro`) VALUES (null,@NombreUsuario,'INSERTAR','SE REGISTRO PERMISOS DE PERFIL','Permisos',NOW()); 

END$$

DROP PROCEDURE IF EXISTS `SP_PERMISOS_ACTUALIZAR`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_PERMISOS_ACTUALIZAR` (IN `idPermisosE` INT(11), IN `perm1` INT(11), IN `perm2` INT(11), IN `perm3` INT(11), IN `perm4` INT(11), IN `perm5` INT(11), IN `perm6` INT(11), IN `idPerfilE` INT(11), IN `idUsuarioE` INT(11))  NO SQL
BEGIN 

UPDATE `permisos` SET `Permiso1`=perm1,`Permiso2`=perm2,`Permiso3`=perm3,`Permiso4`=perm4,`Permiso5`=perm5,`Permiso6`=perm6 WHERE `idPermisos`=idPermisosE;

set @perfil=(SELECT perfil.nombrePerfil FROM perfil WHERE perfil.idPerfil=idPerfilE);

/* ------ REGISTRO DE BITACORA ------ */

set @usuario=(SELECT u.usuario FROM usuario u  WHERE u.idUsuario=idUsuarioE);

INSERT INTO `bitacora`(`idBitacora`, `usuarioAccion`, `Accion`, `tablaAccion`,`Detalle`, `fechaRegistro`) VALUES (null,@usuario,'SE ACTUALIZO PERMISOS','PERMISOS',CONCAT("SE ACTUALIZO PERMISOS DE PERFIL:",@perfil),NOW()); 

END$$

DROP PROCEDURE IF EXISTS `SP_PERMISOS_RECUPERAR`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_PERMISOS_RECUPERAR` (IN `idPerfilE` INT(11))  NO SQL
BEGIN 

SELECT per.idPermisos,per.Permiso1,per.Permiso2,per.Permiso3,per.Permiso4,per.Permiso5,per.Permiso6,perf.nombrePerfil FROM permisos per INNER JOIN perfil perf ON perf.idPerfil=per.Perfil_idPerfil WHERE perf.idPerfil=idPerfilE;

END$$

DROP PROCEDURE IF EXISTS `SP_PERSONAS_LISTAR_SIN_USUARIOS`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_PERSONAS_LISTAR_SIN_USUARIOS` ()  NO SQL
BEGIN 

SELECT * FROM persona p WHERE NOT EXISTS (SELECT * FROM usuario u WHERE u.Persona_idPersona=p.idPersona);


END$$

DROP PROCEDURE IF EXISTS `SP_PERSONA_ACTUALIZAR`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_PERSONA_ACTUALIZAR` (IN `nombre` VARCHAR(50), IN `apellidoP` VARCHAR(50), IN `apellidoM` VARCHAR(50), IN `DNI` CHAR(10), IN `fechaNacimiento` DATE, IN `correo` VARCHAR(100), IN `telefono` CHAR(10), IN `Direccion` TEXT, IN `estado` INT(11), IN `idPersonaU` INT(11), IN `idUsuario` INT(11))  NO SQL
BEGIN 

if(fechaNacimiento=-1)then 
SET fechaNacimiento=null;
end if;
if(correo=-1)then 
SET correo=null;
end if;
if(telefono=-1)then 
SET telefono=null;
end if;
if(Direccion=-1)then 
SET Direccion=null;
end if;
   
UPDATE `persona` SET `nombrePersona`=nombre,`apellidoPaterno`=apellidoP,`apellidoMaterno`=apellidoM,`DNI`=DNI,`fechaNacimiento`=fechaNacimiento,`correo`=correo,`telefono`=telefono,`direccion`=Direccion,`Estado_idEstado`=estado WHERE `idPersona`=idPersonaU;

/* ------ REGISTRO DE BITACORA ------ */
SET @NombreUsuario=(SELECT concat(p.nombrePersona,' ',p.apellidoPaterno,' ',p.apellidoMaterno) as NombresPersona FROM usuario u inner join persona p ON p.idPersona=u.Persona_idPersona WHERE u.idUsuario=idUsuario);

INSERT INTO `bitacora`(`idBitacora`, `usuarioAccion`, `Accion`, `tablaAccion`,`Detalle`, `fechaRegistro`) VALUES (null,@NombreUsuario,'ACTUALIZACION','Persona',CONCAT('SE ACTUALIZO PERSONA:',nombre,' ',apellidoP,' ',apellidoM),NOW());
END$$

DROP PROCEDURE IF EXISTS `SP_PERSONA_HABILITACION`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_PERSONA_HABILITACION` (IN `idPersonaE` INT(11), IN `codigo` INT(11), IN `idUsuarioE` INT(11))  NO SQL
BEGIN 

SET @NombrePersona=(SELECT concat(p.nombrePersona,' ',p.apellidoPaterno,' ',p.apellidoMaterno) as NombresPersona FROM persona p WHERE p.idPersona=idPersonaE);


if (codigo=1) then 
 	UPDATE `persona` SET  `Estado_idEstado`=4 WHERE `idPersona`=idPersonaE;
  SET @Mensaje=("PERSONA DESHBILITADO");
else 
    UPDATE `persona` SET  `Estado_idEstado`=1  WHERE `idPersona`=idPersonaE;
 SET  @Mensaje=("PERSONA HABILITADO");
end if;

 /* ------ REGISTRO DE BITACORA ------ */

set @usuario=(SELECT u.user FROM usuario u  WHERE u.idUsuario=idUsuarioE); 

INSERT INTO `bitacora`(`idBitacora`, `usuarioAccion`, `Accion`, `tablaAccion`,`Detalle`, `fechaRegistro`) VALUES (null,@usuario,@Mensaje,'USUARIO',CONCAT("SE",@Mensaje," :", @NombrePersona),NOW());    

END$$

DROP PROCEDURE IF EXISTS `SP_PERSONA_LISTAR`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_PERSONA_LISTAR` ()  NO SQL
BEGIN 


SELECT * FROM persona p INNER JOIN estado e ON e.idEstado=p.Estado_idEstado;


END$$

DROP PROCEDURE IF EXISTS `SP_PERSONA_LISTAR_TODO`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_PERSONA_LISTAR_TODO` ()  NO SQL
BEGIN 

SELECT * FROM persona;

END$$

DROP PROCEDURE IF EXISTS `SP_PERSONA_RECUPERAR`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_PERSONA_RECUPERAR` (IN `idPersonaU` INT)  NO SQL
begin 

SELECT * FROM persona p WHERE p.idPersona=idPersonaU;

end$$

DROP PROCEDURE IF EXISTS `SP_PERSONA_REGISTRO`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_PERSONA_REGISTRO` (IN `nombre` VARCHAR(50), IN `apellidoP` VARCHAR(50), IN `apellidoM` VARCHAR(50), IN `DNI` CHAR(10), IN `fechaNacimiento` DATE, IN `correo` VARCHAR(100), IN `telefono` CHAR(10), IN `Direccion` TEXT, IN `estado` INT(11), IN `idUsuario` INT(11))  NO SQL
BEGIN 

if(correo='0')THEN
SET correo=null;
end if;
if(telefono='0')THEN
SET telefono=null;
end if;
if(Direccion='0')THEN
SET Direccion=null;
end if;


INSERT INTO `persona`(`idPersona`, `nombrePersona`, `apellidoPaterno`, `apellidoMaterno`, `DNI`, `fechaNacimiento`, `correo`, `telefono`, `direccion`, `Estado_idEstado`, `fechaRegistro`) VALUES (NULL,nombre,apellidoP,apellidoM,DNI,fechaNacimiento,correo,telefono,Direccion,estado,NOW());  


/* ------ REGISTRO DE BITACORA ------ */
SET @NombreUsuario=(SELECT concat(p.nombrePersona,' ',p.apellidoPaterno,' ',p.apellidoMaterno) as NombresPersona FROM usuario u inner join persona p ON p.idPersona=u.Persona_idPersona WHERE u.idUsuario=idUsuario);

INSERT INTO `bitacora`(`idBitacora`, `usuarioAccion`, `Accion`, `tablaAccion`,`Detalle`, `fechaRegistro`) VALUES (null,@NombreUsuario,'REGISTRO','Persona',CONCAT('SE REGISTRO PERSONA:',nombre,' ',apellidoP,' ',apellidoM),NOW());


END$$

DROP PROCEDURE IF EXISTS `SP_USUARIO_ACTUALIZAR`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_USUARIO_ACTUALIZAR` (IN `usuario` VARCHAR(50), IN `pass` TEXT, IN `idPerfil` INT(11), IN `idEstado` INT(11), IN `idUsuarioU` INT(11), IN `idCreador` INT(11))  NO SQL
BEGIN
 
DECLARE Mensaje VARCHAR(100);

-- ACTUALIZAR USUARIO
if(pass='0')then  

UPDATE `usuario` SET 		`user`=usuario,`Perfil_idPerfil`=idPerfil,`Estado_idEstado`=idEstado WHERE  `idUsuario`= idUsuarioU;
set Mensaje="SE ACTUALIZO EL USUARIO:";
        
else 

UPDATE `usuario` SET 		`user`=usuario,`password`=pass,`Perfil_idPerfil`=idPerfil,`Estado_idEstado`=idEstado WHERE  `idUsuario`= idUsuarioU;
set Mensaje="SE ACTUALIZO EL USUARIO:";
end if;



-- REGISTRAR BITACORA
SET @NombreUsuario=(SELECT concat(p.nombrePersona,' ',p.apellidoPaterno,' ',p.apellidoMaterno) as NombresPersona FROM usuario u inner join persona p ON p.idPersona=u.Persona_idPersona WHERE u.idUsuario=idCreador);
 
INSERT INTO `bitacora`(`idBitacora`, `usuarioAccion`, `Accion`, `tablaAccion`,`Detalle`, `fechaRegistro`) VALUES (null,@NombreUsuario,'ACTUALIZACION','USUARIO',CONCAT(Mensaje,usuario),NOW());  
 
 

END$$

DROP PROCEDURE IF EXISTS `SP_USUARIO_HABILITACION`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_USUARIO_HABILITACION` (IN `idUsuarioE` INT(11), IN `codigo` INT(11), IN `idUsuarioM` INT(11))  NO SQL
BEGIN 

SET @NombreUsuario=(SELECT concat(p.nombrePersona,' ',p.apellidoPaterno,' ',p.apellidoMaterno) as NombresPersona FROM usuario u inner join persona p ON p.idPersona=u.Persona_idPersona WHERE u.idUsuario=idUsuarioM);


if (codigo=1) then 
 	UPDATE `usuario` SET  `Estado_idEstado`=4 WHERE `idUsuario`=idUsuarioE;
  SET @Mensaje=("USUARIO DESHBILITADO");
else 
    UPDATE `usuario` SET  `Estado_idEstado`=1  WHERE `idUsuario`=idUsuarioE;
 SET  @Mensaje=("USAURIO HABILITADO");
end if;

 /* ------ REGISTRO DE BITACORA ------ */

set @usuario=(SELECT u.user FROM usuario u  WHERE u.idUsuario=idUsuarioE);

INSERT INTO `bitacora`(`idBitacora`, `usuarioAccion`, `Accion`, `tablaAccion`,`Detalle`, `fechaRegistro`) VALUES (null,@usuario,@Mensaje,'USUARIO',CONCAT("SE",@Mensaje," :", @NombreUsuario),NOW());    

END$$

DROP PROCEDURE IF EXISTS `SP_USUARIO_LISTAR_TODO`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_USUARIO_LISTAR_TODO` ()  NO SQL
BEGIN 

SELECT 
u.idUsuario,
u.usuario,
DATE_FORMAT(u.fechaRegistro,'%d/%m/%Y') as fechaRegistro,
CONCAT(pes.nombrePersona,' ',pes.apellidoPaterno,' ',pes.apellidoMaterno) as NombrePersona,
e.nombreEstado,  
e.idEstado as Estado_idEstado, 
per.nombrePerfil 
FROM usuario u INNER JOIN estado e ON e.idEstado=u.Estado_idEstado INNER JOIN perfil per ON per.idPerfil=u.Perfil_idPerfil INNER JOIN persona pes ON pes.idPersona=u.Persona_idPersona;

END$$

DROP PROCEDURE IF EXISTS `SP_USUARIO_RECUPERAR`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_USUARIO_RECUPERAR` (IN `idUsuarioE` INT)  NO SQL
BEGIN


SELECT * FROM usuario u WHERE u.idUsuario=idUsuarioE; 


END$$

DROP PROCEDURE IF EXISTS `SP_USUARIO_REGISTRO`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_USUARIO_REGISTRO` (IN `usuario` VARCHAR(50), IN `pass` TEXT, IN `idPerfil` INT(11), IN `idPersona` INT(11), IN `idEstado` INT(11), IN `idCreador` INT(11))  NO SQL
BEGIN 

DECLARE Mensaje VARCHAR(100);

-- REGISTRO USUARIO --
INSERT INTO `usuario`(`usuario`, `pass`, `Perfil_idPerfil`, `Persona_idPersona`, `Estado_idEstado`, `fechaRegistro`) VALUES (usuario,pass,idPerfil,idPersona,idEstado,NOW());
 
set Mensaje="SE REGISTRO EL USUARIO:";
 
 
-- REGISTRAR BITACORA
SET @NombreUsuario=(SELECT concat(p.nombrePersona,' ',p.apellidoPaterno,' ',p.apellidoMaterno) as NombresPersona FROM usuario u inner join persona p ON p.idPersona=u.Persona_idPersona WHERE u.idUsuario=idCreador);
 
INSERT INTO `bitacora`(`idBitacora`, `usuarioAccion`, `Accion`, `tablaAccion`,`Detalle`, `fechaRegistro`) VALUES (null,@NombreUsuario,'REGISTRO','USUARIO',CONCAT(Mensaje,usuario),NOW());  
 
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `bitacora`
--

DROP TABLE IF EXISTS `bitacora`;
CREATE TABLE IF NOT EXISTS `bitacora` (
  `idBitacora` int(11) NOT NULL AUTO_INCREMENT,
  `usuarioAccion` varchar(100) NOT NULL,
  `Accion` varchar(100) NOT NULL,
  `tablaAccion` varchar(100) NOT NULL,
  `Detalle` text NOT NULL,
  `fechaRegistro` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`idBitacora`)
) ENGINE=InnoDB AUTO_INCREMENT=158 DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `bitacora`
--

INSERT INTO `bitacora` (`idBitacora`, `usuarioAccion`, `Accion`, `tablaAccion`, `Detalle`, `fechaRegistro`) VALUES
(1, 'JESUS INCA CARDENAS', 'INSERTAR', 'USUARIO', 'SE REGISTRO EL USUARIO:admin3', '2018-09-29 19:53:29'),
(2, 'JESUS INCA CARDENAS', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:usuaricambia', '2018-09-29 19:56:41'),
(3, 'JESUS INCA CARDENAS', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:prueba', '2018-09-29 20:00:24'),
(4, 'JESUS INCA CARDENAS', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:nuevo', '2018-09-29 20:01:47'),
(5, 'JESUS INCA CARDENAS', 'USUARIO DESHBILITADO', 'USUARIO', 'SEUSUARIO DESHBILITADO :admin', '2018-10-03 00:56:59'),
(6, 'JESUS INCA CARDENAS', 'USUARIO DESHBILITADO', 'USUARIO', 'SEUSUARIO DESHBILITADO :admin', '2018-10-03 01:04:58'),
(7, 'JESUS INCA CARDENAS', 'USAURIO HABILITADO', 'USUARIO', 'SEUSAURIO HABILITADO :admin', '2018-10-03 01:09:40'),
(8, 'JESUS INCA CARDENAS', 'USUARIO DESHBILITADO', 'USUARIO', 'SEUSUARIO DESHBILITADO :admin', '2018-10-03 01:09:44'),
(9, 'JESUS INCA CARDENAS', 'USAURIO HABILITADO', 'USUARIO', 'SEUSAURIO HABILITADO :admin', '2018-10-03 01:09:47'),
(10, 'JESUS INCA CARDENAS', 'ACTUALIZO', 'USUARIO', 'SE REGISTRO EL USUARIO:wdqw', '2018-10-03 02:00:09'),
(11, 'MAJE SILVA SILVA', 'ACTUALIZO', 'USUARIO', 'SE REGISTRO EL USUARIO:msilva', '2018-10-04 13:29:08'),
(12, 'MAJE SILVA SILVA', 'ACTUALIZO', 'USUARIO', 'SE REGISTRO EL USUARIO:JLOPEZ', '2018-10-04 13:35:15'),
(13, 'MAJE SILVA SILVA', 'USUARIO DESHBILITADO', 'USUARIO', 'SEUSUARIO DESHBILITADO :JLOPEZ', '2018-10-04 13:35:27'),
(14, 'MAJE SILVA SILVA', 'USAURIO HABILITADO', 'USUARIO', 'SEUSAURIO HABILITADO :JLOPEZ', '2018-10-04 13:35:36'),
(15, 'MAJE SILVA SILVA', 'USUARIO DESHBILITADO', 'USUARIO', 'SEUSUARIO DESHBILITADO :JLOPEZ', '2018-10-04 13:35:40'),
(16, 'MAJE SILVA SILVA', 'USAURIO HABILITADO', 'USUARIO', 'SEUSAURIO HABILITADO :JLOPEZ', '2018-10-04 13:35:42'),
(17, 'MAJE SILVA SILVA', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:prueba', '2018-10-04 16:38:33'),
(18, 'MAJE SILVA SILVA', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:msilva2', '2018-10-04 16:39:27'),
(19, 'MAJE SILVA SILVA', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:admin', '2018-10-04 16:45:08'),
(20, 'MAJE SILVA SILVA', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:prueba2', '2018-10-04 16:45:17'),
(21, 'MAJE SILVA SILVA', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:prueba2', '2018-10-04 16:45:47'),
(22, 'MAJE SILVA SILVA', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:prueba2', '2018-10-04 16:46:08'),
(23, 'MAJE SILVA SILVA', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:prueba3', '2018-10-04 16:47:16'),
(24, 'MAJE SILVA SILVA', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:prueba1', '2018-10-04 16:47:56'),
(25, 'LUCIA TABOADA GUZMAN', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:prueba1', '2018-10-04 16:48:23'),
(26, 'JESUS INCA CARDENAS', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:prueba3', '2018-10-04 16:50:18'),
(27, 'JESUS INCA CARDENAS', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:prueba3', '2018-10-04 16:51:20'),
(28, 'LUCIA TABOADA GUZMAN', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:admin2', '2018-10-04 16:52:32'),
(29, 'LUCIA TABOADA GUZMAN', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:prueba3', '2018-10-04 16:55:17'),
(30, 'LUCIA TABOADA GUZMAN', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:prueba3', '2018-10-04 16:55:33'),
(31, 'LUCIA TABOADA GUZMAN', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:prueba3', '2018-10-04 16:55:53'),
(32, 'LUCIA TABOADA GUZMAN', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:prueba2', '2018-10-04 16:56:02'),
(33, 'LUCIA TABOADA GUZMAN', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:prueba2', '2018-10-04 16:56:09'),
(34, 'LUCIA TABOADA GUZMAN', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:prueba2', '2018-10-04 16:56:32'),
(35, 'MAJE SILVA SILVA', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:prueba2', '2018-10-04 16:58:27'),
(36, 'LUCIA TABOADA GUZMAN', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:prueba2', '2018-10-04 16:59:13'),
(37, 'LUCIA TABOADA GUZMAN', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:prueba2', '2018-10-04 17:01:09'),
(38, 'LUCIA TABOADA GUZMAN', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:prueba2', '2018-10-04 17:03:37'),
(39, 'LUCIA TABOADA GUZMAN', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:prueba2', '2018-10-04 17:04:28'),
(40, 'LUCIA TABOADA GUZMAN', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:prueba2', '2018-10-04 17:04:43'),
(41, 'LUCIA TABOADA GUZMAN', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:prueba2', '2018-10-04 17:05:07'),
(42, 'LUCIA TABOADA GUZMAN', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:prueba2', '2018-10-04 17:05:52'),
(43, 'LUCIA TABOADA GUZMAN', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:prueba2', '2018-10-04 17:06:14'),
(44, 'LUCIA TABOADA GUZMAN', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:prueba', '2018-10-04 17:07:12'),
(45, 'LUCIA TABOADA GUZMAN', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:prueba', '2018-10-04 17:08:37'),
(46, 'LUCIA TABOADA GUZMAN', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:prueba', '2018-10-04 17:09:29'),
(47, 'LUCIA TABOADA GUZMAN', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:prueba', '2018-10-04 17:10:33'),
(48, 'LUCIA TABOADA GUZMAN', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:prueba', '2018-10-04 17:11:14'),
(49, 'LUCIA TABOADA GUZMAN', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:prueba', '2018-10-04 17:12:14'),
(50, 'LUCIA TABOADA GUZMAN', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:prueba', '2018-10-04 17:12:22'),
(51, 'LUCIA TABOADA GUZMAN', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:prueba', '2018-10-04 17:13:03'),
(52, 'LUCIA TABOADA GUZMAN', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:prueba', '2018-10-04 17:13:14'),
(53, 'LUCIA TABOADA GUZMAN', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:prueba', '2018-10-04 17:14:09'),
(54, 'LUCIA TABOADA GUZMAN', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:prueba', '2018-10-04 17:14:50'),
(55, 'LUCIA TABOADA GUZMAN', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:prueba', '2018-10-04 17:14:50'),
(56, 'LUCIA TABOADA GUZMAN', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:prueba', '2018-10-04 17:15:39'),
(57, 'LUCIA TABOADA GUZMAN', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:prueba', '2018-10-04 17:15:47'),
(58, 'JESUS INCA CARDENAS', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:prueba', '2018-10-04 17:17:05'),
(59, 'LUCIA TABOADA GUZMAN', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:admin2', '2018-10-04 17:19:14'),
(60, 'LUCIA TABOADA GUZMAN', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:admin2', '2018-10-04 17:19:43'),
(61, 'LUCIA TABOADA GUZMAN', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:admin2', '2018-10-04 17:22:44'),
(62, 'LUCIA TABOADA GUZMAN', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:admin2', '2018-10-04 17:23:08'),
(63, 'LUCIA TABOADA GUZMAN', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:admin2', '2018-10-04 17:24:49'),
(64, 'MAJE SILVA SILVA', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:admin', '2018-10-04 17:29:52'),
(65, 'JESUS INCA CARDENAS', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:admin', '2018-10-04 17:31:53'),
(66, 'MAJE SILVA SILVA', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:admin', '2018-10-04 17:32:14'),
(67, 'MAJE SILVA SILVA', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:admin', '2018-10-04 17:48:50'),
(68, 'MAJE SILVA SILVA', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:admin', '2018-10-04 17:49:27'),
(69, 'MAJE SILVA SILVA', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:admin', '2018-10-04 17:53:16'),
(70, 'MAJE SILVA SILVA', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:admin', '2018-10-04 17:54:56'),
(71, 'MAJE SILVA SILVA', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:admin', '2018-10-04 17:55:24'),
(72, 'MAJE SILVA SILVA', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:admin', '2018-10-04 17:56:52'),
(73, 'MAJE SILVA SILVA', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:admin', '2018-10-04 18:00:31'),
(74, 'JESUS INCA CARDENAS', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:admin', '2018-10-04 18:03:17'),
(75, 'JESUS INCA CARDENAS', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:admin', '2018-10-04 18:04:41'),
(76, 'JESUS INCA CARDENAS', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:admin', '2018-10-04 18:06:15'),
(77, 'JESUS INCA CARDENAS', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:prueba2', '2018-10-05 10:02:27'),
(78, 'JESUS INCA CARDENAS', 'ACTUALIZO', 'USUARIO', 'SE REGISTRO EL USUARIO:persona', '2018-10-05 10:14:26'),
(79, 'JESUS INCA CARDENAS', 'ACTUALIZO', 'USUARIO', 'SE REGISTRO EL USUARIO:persona2', '2018-10-05 10:16:34'),
(80, 'JESUS INCA CARDENAS', 'ACTUALIZO', 'USUARIO', 'SE REGISTRO EL USUARIO:prueba', '2018-10-05 10:17:48'),
(81, 'JESUS INCA CARDENAS', 'ACTUALIZO', 'USUARIO', 'SE REGISTRO EL USUARIO:prueba1', '2018-10-05 10:18:17'),
(82, 'JESUS INCA CARDENAS', 'ACTUALIZO', 'USUARIO', 'SE ACTUALIZO EL USUARIO:prueba2', '2018-10-05 10:39:30'),
(83, 'JESUS INCA CARDENAS', 'INSERTAR', 'Persona', 'SE REGISTRO PERSONA:jesu werfwe fefwe', '2018-10-05 10:51:36'),
(84, 'JESUS INCA CARDENAS', 'ACTUALIZACION', 'Persona', 'SE ACTUALIZO PERSONA:jesus inca cardenas', '2018-10-05 11:11:11'),
(85, 'JESUS INCA CARDENAS', 'REGISTRO', 'Persona', 'SE REGISTRO PERSONA:wfwefwe werfwef frefwef', '2018-10-05 12:48:34'),
(86, 'JESUS INCA CARDENAS', 'USUARIO DESHBILITADO', 'USUARIO', 'SEUSUARIO DESHBILITADO :admin', '2018-10-05 13:36:40'),
(87, 'JESUS INCA CARDENAS', 'USUARIO DESHBILITADO', 'USUARIO', 'SEUSUARIO DESHBILITADO :admin', '2018-10-05 13:36:46'),
(88, 'admin', 'PERSONA DESHBILITADO', 'USUARIO', 'SEPERSONA DESHBILITADO :JESUS INCA CARDENAS', '2018-10-05 13:38:27'),
(89, 'admin', 'PERSONA HABILITADO', 'USUARIO', 'SEPERSONA HABILITADO :JESUS INCA CARDENAS', '2018-10-05 13:38:30'),
(90, 'admin', 'PERSONA DESHBILITADO', 'USUARIO', 'SEPERSONA DESHBILITADO :JESUS INCA CARDENAS', '2018-10-05 13:38:32'),
(91, 'admin', 'PERSONA DESHBILITADO', 'USUARIO', 'SEPERSONA DESHBILITADO :LUCIA TABOADA GUZMAN', '2018-10-05 13:59:32'),
(92, 'admin', 'PERSONA HABILITADO', 'USUARIO', 'SEPERSONA HABILITADO :JESUS INCA CARDENAS', '2018-10-05 14:00:13'),
(93, 'JESUS23 INCA23 CARDENAS', 'ACTUALIZACION', 'Persona', 'SE ACTUALIZO PERSONA:JESUS23 INCA23 CARDENAS', '2018-10-05 14:42:15'),
(94, 'JESUS233 INCA233 CARDENAS', 'ACTUALIZACION', 'Persona', 'SE ACTUALIZO PERSONA:JESUS233 INCA233 CARDENAS', '2018-10-05 14:43:49'),
(95, 'JESUS23331 INCA23314 CARDENAS', 'ACTUALIZACION', 'Persona', 'SE ACTUALIZO PERSONA:JESUS23331 INCA23314 CARDENAS', '2018-10-05 14:45:17'),
(96, 'JESUS23331 INCA23314 CARDENAS', 'ACTUALIZACION', 'Persona', 'SE ACTUALIZO PERSONA:JESUS23331 INCA23314 CARDENAS', '2018-10-05 14:46:03'),
(97, 'JESUS INCA CARDENAS', 'ACTUALIZACION', 'Persona', 'SE ACTUALIZO PERSONA:JESUS INCA CARDENAS', '2018-10-05 14:48:34'),
(98, 'JESUS2 INCA2 CARDENAS', 'ACTUALIZACION', 'Persona', 'SE ACTUALIZO PERSONA:JESUS2 INCA2 CARDENAS', '2018-10-05 14:49:29'),
(99, 'admin', 'PERSONA DESHBILITADO', 'USUARIO', 'SEPERSONA DESHBILITADO :JESUS2 INCA2 CARDENAS', '2018-10-05 14:49:35'),
(100, 'JESUS2 INCA2 CARDENAS', 'INSERTAR', 'SE REGISTRO PERFIL', 'Perfil', '2018-10-05 15:42:18'),
(101, 'JESUS2 INCA2 CARDENAS', 'INSERTAR', 'SE REGISTRO PERMISOS DE PERFIL', 'Permisos', '2018-10-05 15:42:18'),
(102, 'JESUS2 INCA2 CARDENAS', 'INSERTAR', 'SE REGISTRO PERFIL', 'Perfil', '2018-10-05 15:42:43'),
(103, 'JESUS2 INCA2 CARDENAS', 'INSERTAR', 'SE REGISTRO PERMISOS DE PERFIL', 'Permisos', '2018-10-05 15:42:43'),
(104, 'admin', 'PERSONA DESHBILITADO', 'USUARIO', 'SEPERSONA DESHBILITADO :MAJE SILVA SILVA', '2018-10-05 15:42:51'),
(105, 'admin', 'ACTUALIZACION', 'Perfil', 'SE ACTUALIZO PERFIL:fweefw', '2018-10-05 15:53:04'),
(106, 'admin', 'ACTUALIZACION', 'Perfil', 'SE ACTUALIZO PERFIL:ADMINISTRADOR', '2018-10-05 15:53:22'),
(107, 'admin', 'ACTUALIZACION', 'Perfil', 'SE ACTUALIZO PERFIL:wefwefew', '2018-10-05 15:54:03'),
(108, 'admin', 'ACTUALIZACION', 'Perfil', 'SE ACTUALIZO PERFIL:wfgwf', '2018-10-05 15:54:11'),
(109, 'admin', 'PERSONA DESHBILITADO', 'USUARIO', 'SEPERSONA DESHBILITADO :JESUS2 INCA2 CARDENAS', '2018-10-05 15:56:36'),
(110, 'admin', 'PERSONA DESHBILITADO', 'USUARIO', 'SEPERSONA DESHBILITADO :JESUS2 INCA2 CARDENAS', '2018-10-05 15:56:44'),
(111, 'admin', 'PERFIL DESHBILITADO', 'PERFIL', 'SEPERFIL DESHBILITADO :ADMINISTRADOR', '2018-10-05 16:02:49'),
(112, 'admin', 'PERFIL HABILITADO', 'PERFIL', 'SEPERFIL HABILITADO :ADMINISTRADOR', '2018-10-05 16:03:01'),
(113, 'admin', 'PERFIL HABILITADO', 'PERFIL', 'SEPERFIL HABILITADO :wefwefew', '2018-10-05 16:03:02'),
(114, 'admin', 'PERFIL HABILITADO', 'PERFIL', 'SEPERFIL HABILITADO :wfgwf', '2018-10-05 16:03:05'),
(115, 'JESUS2 INCA2 CARDENAS', 'INSERTAR', 'SE REGISTRO PERFIL', 'Perfil', '2018-11-08 13:25:46'),
(116, 'JESUS2 INCA2 CARDENAS', 'INSERTAR', 'SE REGISTRO PERMISOS DE PERFIL', 'Permisos', '2018-11-08 13:25:46'),
(117, 'admin', 'ACTUALIZACION', 'Perfil', 'SE ACTUALIZO PERFIL:ADMINISTRADOR', '2018-11-08 13:26:10'),
(118, 'JESUS2 INCA2 CARDENAS', 'INSERTAR', 'SE REGISTRO PERFIL', 'Perfil', '2018-11-08 13:26:34'),
(119, 'JESUS2 INCA2 CARDENAS', 'INSERTAR', 'SE REGISTRO PERMISOS DE PERFIL', 'Permisos', '2018-11-08 13:26:34'),
(120, 'JESUS2 INCA2 CARDENAS', 'INSERTAR', 'SE REGISTRO PERFIL', 'Perfil', '2018-11-08 13:27:09'),
(121, 'JESUS2 INCA2 CARDENAS', 'INSERTAR', 'SE REGISTRO PERMISOS DE PERFIL', 'Permisos', '2018-11-08 13:27:09'),
(122, 'JESUS2 INCA2 CARDENAS', 'INSERTAR', 'SE REGISTRO PERFIL', 'Perfil', '2018-11-08 13:27:48'),
(123, 'JESUS2 INCA2 CARDENAS', 'INSERTAR', 'SE REGISTRO PERMISOS DE PERFIL', 'Permisos', '2018-11-08 13:27:48'),
(124, 'JESUS2 INCA2 CARDENAS', 'INSERTAR', 'SE REGISTRO PERFIL', 'Perfil', '2018-11-08 13:28:06'),
(125, 'JESUS2 INCA2 CARDENAS', 'INSERTAR', 'SE REGISTRO PERMISOS DE PERFIL', 'Permisos', '2018-11-08 13:28:06'),
(126, 'admin', 'USAURIO HABILITADO', 'USUARIO', 'SEUSAURIO HABILITADO :JESUS2 INCA2 CARDENAS', '2018-11-08 13:28:18'),
(127, 'admin', 'PERSONA HABILITADO', 'USUARIO', 'SEPERSONA HABILITADO :JESUS2 INCA2 CARDENAS', '2018-11-08 13:29:09'),
(128, 'ADMINISTRADOR GENERAL DEL SISTEMA', 'ACTUALIZACION', 'Persona', 'SE ACTUALIZO PERSONA:ADMINISTRADOR GENERAL DEL SISTEMA', '2018-11-08 13:29:28'),
(129, 'ADMINISTRADOR GENERAL DEL SISTEMA', 'REGISTRO', 'Persona', 'SE REGISTRO PERSONA:JOSE RODRIGO SULCA', '2018-11-08 13:30:27'),
(130, 'ADMINISTRADOR GENERAL DEL SISTEMA', 'REGISTRO', 'Persona', 'SE REGISTRO PERSONA:JORGE ROMAN SULCA', '2018-11-08 13:31:02'),
(131, 'ADMINISTRADOR GENERAL DEL SISTEMA', 'REGISTRO', 'Persona', 'SE REGISTRO PERSONA:feww wef wef', '2018-11-08 14:29:45'),
(132, 'ADMINISTRADOR GENERAL DEL SISTEMA', 'REGISTRO', 'Persona', 'SE REGISTRO PERSONA:wef wef wef', '2018-11-08 14:32:35'),
(133, 'ADMINISTRADOR GENERAL DEL SISTEMA', 'ACTUALIZACION', 'Persona', 'SE ACTUALIZO PERSONA:wef wef wef', '2018-11-08 14:34:19'),
(134, 'ADMINISTRADOR GENERAL DEL SISTEMA', 'REGISTRO', 'Persona', 'SE REGISTRO PERSONA:rger gwefwe gwegw', '2018-11-08 14:34:33'),
(135, 'ADMINISTRADOR GENERAL DEL SISTEMA', 'REGISTRO', 'Persona', 'SE REGISTRO PERSONA:rf f fr', '2018-11-08 14:35:17'),
(136, 'admin', 'MATERIAL DESHABILITADO', 'MATERIAL', 'MATERIAL ACTUALIZAR', '2018-11-08 15:42:18'),
(137, 'admin', 'MATERIAL HABILITADO', 'MATERIAL', 'MATERIAL ACTUALIZAR', '2018-11-08 15:42:21'),
(138, 'admin', 'ACTUALIZACION', 'Material', 'SE ACTUALIZO MATERIAL:PRIMA WOOL - 000236', '2018-11-08 15:42:32'),
(139, 'admin', 'ACTUALIZACION', 'Material', 'SE ACTUALIZO MATERIAL:PRIMA ACRYLIC - 000235', '2018-11-08 15:43:16'),
(140, 'admin', 'ACTUALIZACION', 'Material', 'SE ACTUALIZO MATERIAL:PRIMA COTTON - 000234', '2018-11-08 15:43:23'),
(141, 'ADMINISTRADOR GENERAL DEL SISTEMA', 'REGISTRO', 'USUARIO', 'SE REGISTRO EL USUARIO:agarciaa', '2018-11-08 15:52:43'),
(142, 'ADMINISTRADOR GENERAL DEL SISTEMA', 'REGISTRO', 'USUARIO', 'SE REGISTRO EL USUARIO:jgonzalezc', '2018-11-08 15:54:41'),
(143, 'ADMINISTRADOR GENERAL DEL SISTEMA', 'REGISTRO', 'USUARIO', 'SE REGISTRO EL USUARIO:mrodriguezc', '2018-11-08 15:55:08'),
(144, 'ADMINISTRADOR GENERAL DEL SISTEMA', 'REGISTRO', 'USUARIO', 'SE REGISTRO EL USUARIO:dlopezp', '2018-11-08 15:55:42'),
(145, 'ADMINISTRADOR GENERAL DEL SISTEMA', 'REGISTRO', 'USUARIO', 'SE REGISTRO EL USUARIO:jmartinezs', '2018-11-08 15:56:05'),
(146, 'admin', 'SE ACTUALIZO PERMISOS', 'PERMISOS', 'SE ACTUALIZO PERMISOS DE PERFIL:SUPERVISOR DE ENCONADO', '2018-11-08 16:02:04'),
(147, 'admin', 'SE ACTUALIZO PERMISOS', 'PERMISOS', 'SE ACTUALIZO PERMISOS DE PERFIL:SUPERVISOR DE OVILLADO', '2018-11-08 16:03:13'),
(148, 'admin', 'SE ACTUALIZO PERMISOS', 'PERMISOS', 'SE ACTUALIZO PERMISOS DE PERFIL:SUPERVISOR DE CALIDAD', '2018-11-08 16:03:28'),
(149, 'admin', 'SE ACTUALIZO PERMISOS', 'PERMISOS', 'SE ACTUALIZO PERMISOS DE PERFIL:SUPERVISOR DE TINTORERIA', '2018-11-08 16:03:43'),
(150, 'admin', 'SE ACTUALIZO PERMISOS', 'PERMISOS', 'SE ACTUALIZO PERMISOS DE PERFIL:TRABAJADOR', '2018-11-08 16:03:59'),
(151, 'admin', 'MATERIAL DESHABILITADO', 'MATERIAL', 'MATERIAL ACTUALIZAR', '2018-11-21 01:02:36'),
(152, 'admin', 'MATERIAL DESHABILITADO', 'MATERIAL', 'MATERIAL ACTUALIZAR', '2018-11-21 01:02:38'),
(153, 'admin', 'MATERIAL HABILITADO', 'MATERIAL', 'MATERIAL ACTUALIZAR', '2018-11-21 01:02:40'),
(154, 'admin', 'MATERIAL HABILITADO', 'MATERIAL', 'MATERIAL ACTUALIZAR', '2018-11-21 01:02:42'),
(155, 'admin', 'ACTUALIZACION', 'Material', 'SE ACTUALIZO MATERIAL:ALPACA - 000234', '2018-11-21 01:02:57'),
(156, 'admin', 'ACTUALIZACION', 'Material', 'SE ACTUALIZO MATERIAL:BABY MICROFIBRA - 000235', '2018-11-21 01:03:11'),
(157, 'admin', 'ACTUALIZACION', 'Material', 'SE ACTUALIZO MATERIAL:COTTON WOOL - 000236', '2018-11-21 01:03:23');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `estado`
--

DROP TABLE IF EXISTS `estado`;
CREATE TABLE IF NOT EXISTS `estado` (
  `idEstado` int(11) NOT NULL AUTO_INCREMENT,
  `tipoEstado` tinyint(4) NOT NULL,
  `nombreEstado` varchar(50) NOT NULL,
  PRIMARY KEY (`idEstado`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `estado`
--

INSERT INTO `estado` (`idEstado`, `tipoEstado`, `nombreEstado`) VALUES
(1, 1, 'ACTIVO'),
(2, 1, 'INACTIVO'),
(3, 2, 'HABILITADO'),
(4, 2, 'DESHABILITADO'),
(5, 3, 'EN PROCESO - OVILLADO'),
(6, 3, 'EN PROCESO - CALIDAD'),
(7, 3, 'FINALIZADO'),
(8, 3, 'ENVIADO A ENCONADO'),
(9, 3, 'ENVIADO A CALIDAD'),
(10, 3, 'EN PROCESO - ENCONADO');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `login`
--

DROP TABLE IF EXISTS `login`;
CREATE TABLE IF NOT EXISTS `login` (
  `idLogin` int(11) NOT NULL AUTO_INCREMENT,
  `Usuario_idUsuario` int(11) NOT NULL,
  `usuarioLog` varchar(50) NOT NULL,
  `passwordLog` varchar(100) NOT NULL,
  `perfilLog` varchar(150) NOT NULL,
  `fechaLog` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ip` varchar(50) DEFAULT NULL,
  `fechaLogout` datetime DEFAULT NULL,
  PRIMARY KEY (`idLogin`),
  KEY `Usuario_idUsuario` (`Usuario_idUsuario`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `login`
--

INSERT INTO `login` (`idLogin`, `Usuario_idUsuario`, `usuarioLog`, `passwordLog`, `perfilLog`, `fechaLog`, `ip`, `fechaLogout`) VALUES
(1, 1, 'admin', '$2a$08$RCuzW/8g2Lg4QMNCfmsa/uKp33rvDmdWrC.P40DOECJlMtPu16NMW', 'Administrador', '2018-09-29 14:03:44', '::1', '2018-11-19 22:14:09');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `material`
--

DROP TABLE IF EXISTS `material`;
CREATE TABLE IF NOT EXISTS `material` (
  `idMaterial` int(11) NOT NULL AUTO_INCREMENT,
  `Descripcion` varchar(150) NOT NULL,
  `fechaRegistro` datetime NOT NULL,
  `Estado_idEstado` int(11) NOT NULL,
  PRIMARY KEY (`idMaterial`),
  KEY `FK_EstadoMaterial` (`Estado_idEstado`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `material`
--

INSERT INTO `material` (`idMaterial`, `Descripcion`, `fechaRegistro`, `Estado_idEstado`) VALUES
(3, 'ALPACA - 000234', '2018-11-08 00:00:00', 1),
(4, 'BABY MICROFIBRA - 000235', '2018-11-08 00:00:00', 1),
(5, 'COTTON WOOL - 000236', '2018-11-08 00:00:00', 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `orden`
--

DROP TABLE IF EXISTS `orden`;
CREATE TABLE IF NOT EXISTS `orden` (
  `idOrden` int(11) NOT NULL AUTO_INCREMENT,
  `NumOrden` varchar(100) NOT NULL,
  `Material_idMaterial` int(11) NOT NULL,
  `Lote` varchar(100) NOT NULL,
  `Kilos` decimal(10,2) NOT NULL,
  `NumConos` decimal(10,2) NOT NULL,
  `fechaRegistro` datetime NOT NULL,
  `Estado_idEstado` int(11) NOT NULL,
  `Observaciones` text,
  `RechazoEnconado` text,
  `fechaRechazoEnconado` datetime DEFAULT NULL,
  `RechazoOvillado` text,
  `fechaRechazoOvillado` date DEFAULT NULL,
  PRIMARY KEY (`idOrden`),
  KEY `FK_ESTADO_ORDEN` (`Estado_idEstado`),
  KEY `FK_MATERIAL_ORDEN` (`Material_idMaterial`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `orden`
--

INSERT INTO `orden` (`idOrden`, `NumOrden`, `Material_idMaterial`, `Lote`, `Kilos`, `NumConos`, `fechaRegistro`, `Estado_idEstado`, `Observaciones`, `RechazoEnconado`, `fechaRechazoEnconado`, `RechazoOvillado`, `fechaRechazoOvillado`) VALUES
(4, 'Nº 0001', 3, 'AN1', '3.00', '60.00', '2018-11-21 01:07:49', 7, 'vhnfgng', 'RECHAZO ENCONADO! PRUEBA', '2018-11-21 03:33:01', NULL, NULL),
(5, 'Nº 0002', 3, 'AN1', '4.00', '100.00', '2018-11-21 01:10:47', 5, NULL, NULL, NULL, 'asfasf', '2018-11-21'),
(6, 'Nº 0003', 4, 'AN2', '50.00', '15.00', '2018-11-21 01:13:47', 6, NULL, NULL, NULL, NULL, NULL),
(7, 'Nº 0004', 4, 'AN2', '60.00', '56.00', '2018-11-21 01:14:06', 6, NULL, NULL, NULL, NULL, NULL),
(8, 'Nº 0005', 5, 'AN3', '120.00', '80.00', '2018-11-21 01:14:22', 6, NULL, NULL, NULL, NULL, NULL),
(9, 'Nº 0006', 5, 'LT1', '90.00', '12.00', '2018-11-21 01:50:25', 6, 'OBSERVACIÓN DE LA ORDEN DE TRABAJO', NULL, NULL, NULL, NULL);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `ovillado`
--

DROP TABLE IF EXISTS `ovillado`;
CREATE TABLE IF NOT EXISTS `ovillado` (
  `idOvillado` int(11) NOT NULL AUTO_INCREMENT,
  `Orden_idOrden` int(11) NOT NULL,
  `PesoOvillo` decimal(10,2) NOT NULL,
  `LoteOvillo` varchar(100) NOT NULL,
  `NumOrden` varchar(100) NOT NULL,
  `Material_idMaterial` int(11) NOT NULL,
  `Cantidadovillos` int(11) NOT NULL,
  `Persona_idPersona` int(11) NOT NULL,
  `fechaRegistro` datetime NOT NULL,
  `Estado_idEstado` int(11) NOT NULL,
  `Observaciones` text,
  PRIMARY KEY (`idOvillado`),
  KEY `FK_ESTADOL_OVILLADO` (`Estado_idEstado`),
  KEY `FK_ORDEN_OVILLADO` (`Orden_idOrden`),
  KEY `FK_MATERIAL_OVILLADO` (`Material_idMaterial`),
  KEY `FK_PERSONA_OVILLADO` (`Persona_idPersona`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `ovillado`
--

INSERT INTO `ovillado` (`idOvillado`, `Orden_idOrden`, `PesoOvillo`, `LoteOvillo`, `NumOrden`, `Material_idMaterial`, `Cantidadovillos`, `Persona_idPersona`, `fechaRegistro`, `Estado_idEstado`, `Observaciones`) VALUES
(1, 4, '20.00', 'OV23', 'OV - 0001', 3, 130, 47, '2018-11-21 01:34:11', 1, NULL),
(2, 4, '25.00', 'AFG2', 'OV - 0002', 3, 300, 49, '2018-11-21 01:34:30', 1, NULL),
(3, 8, '120.00', 'LT12', 'OV - 0003', 5, 200, 50, '2018-11-21 01:38:25', 1, NULL),
(4, 9, '12.00', 'LT2', 'OV - 0004', 5, 150, 51, '2018-11-21 02:01:35', 1, 'Observaciones del trabajador'),
(5, 7, '20.00', 'LT3', 'OV - 0005', 4, 100, 51, '2018-11-21 02:29:46', 1, 'QEFQEFQWFQWFQW'),
(6, 6, '30.00', 'LT312', 'OV - 0006', 4, 120, 57, '2018-11-21 02:31:02', 1, 'EWFQFQEFQF'),
(7, 5, '10.00', 'LT42', 'OV - 0007', 3, 60, 62, '2018-11-21 02:33:07', 1, 'AFQFQWFWQ');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `perfil`
--

DROP TABLE IF EXISTS `perfil`;
CREATE TABLE IF NOT EXISTS `perfil` (
  `idPerfil` int(11) NOT NULL AUTO_INCREMENT,
  `nombrePerfil` varchar(50) NOT NULL,
  `descripcionPerfil` text NOT NULL,
  `Estado_idEstado` int(11) NOT NULL,
  `fechaRegistro` datetime NOT NULL,
  PRIMARY KEY (`idPerfil`),
  KEY `FK_Estado` (`Estado_idEstado`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `perfil`
--

INSERT INTO `perfil` (`idPerfil`, `nombrePerfil`, `descripcionPerfil`, `Estado_idEstado`, `fechaRegistro`) VALUES
(1, 'ADMINISTRADOR', 'ADMINISTRADOR GENERAL.', 1, '2018-09-29 13:29:55'),
(4, 'SUPERVISOR DE ENCONADO', 'USUARIO SUPERVISOR DEL PROCESO DE ENCONADO.', 1, '2018-11-08 13:25:46'),
(5, 'SUPERVISOR DE OVILLADO', 'USUARIO SUPERVISOR DEL PROCESO DE OVILLADO', 1, '2018-11-08 13:26:34'),
(6, 'SUPERVISOR DE CALIDAD', 'USUARIO SUPERVISOR DEL PROCESO DE CALIDAD.', 1, '2018-11-08 13:27:09'),
(7, 'SUPERVISOR DE TINTORERIA', 'USUARIO SUPERVISOR DEL PROCESO DE TINTORERIA.', 1, '2018-11-08 13:27:48'),
(8, 'TRABAJADOR', 'USUARIO COLABORADOR.', 1, '2018-11-08 13:28:06');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `permisos`
--

DROP TABLE IF EXISTS `permisos`;
CREATE TABLE IF NOT EXISTS `permisos` (
  `idPermisos` int(11) NOT NULL AUTO_INCREMENT,
  `Perfil_idPerfil` int(11) NOT NULL,
  `Permiso1` int(11) NOT NULL,
  `Permiso2` int(11) NOT NULL,
  `Permiso3` int(11) NOT NULL,
  `Permiso4` int(11) NOT NULL,
  `Permiso5` int(11) NOT NULL,
  `Permiso6` int(11) NOT NULL,
  PRIMARY KEY (`idPermisos`),
  KEY `FK_Perfil_idPerfil` (`Perfil_idPerfil`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `permisos`
--

INSERT INTO `permisos` (`idPermisos`, `Perfil_idPerfil`, `Permiso1`, `Permiso2`, `Permiso3`, `Permiso4`, `Permiso5`, `Permiso6`) VALUES
(4, 1, 1, 1, 1, 1, 1, 1),
(5, 4, 1, 0, 0, 0, 0, 0),
(6, 5, 0, 1, 0, 0, 0, 0),
(7, 6, 0, 0, 0, 1, 0, 0),
(8, 7, 0, 0, 1, 0, 0, 0),
(9, 8, 0, 1, 0, 0, 0, 0);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `persona`
--

DROP TABLE IF EXISTS `persona`;
CREATE TABLE IF NOT EXISTS `persona` (
  `idPersona` int(11) NOT NULL AUTO_INCREMENT,
  `nombrePersona` varchar(50) NOT NULL,
  `apellidoPaterno` varchar(50) NOT NULL,
  `apellidoMaterno` varchar(50) NOT NULL,
  `DNI` char(8) NOT NULL,
  `fechaNacimiento` date DEFAULT NULL,
  `correo` varchar(50) DEFAULT NULL,
  `telefono` char(10) DEFAULT NULL,
  `direccion` text,
  `Estado_idEstado` int(11) NOT NULL,
  `fechaRegistro` datetime NOT NULL,
  PRIMARY KEY (`idPersona`),
  KEY `FK_Estado_idEstado` (`Estado_idEstado`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=140 DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `persona`
--

INSERT INTO `persona` (`idPersona`, `nombrePersona`, `apellidoPaterno`, `apellidoMaterno`, `DNI`, `fechaNacimiento`, `correo`, `telefono`, `direccion`, `Estado_idEstado`, `fechaRegistro`) VALUES
(1, 'ADMINISTRADOR', 'GENERAL', 'DEL SISTEMA', '47040087', '1992-05-18', 'jic_d12@hotmail.com', '5284039', 'aahh enrique milla ochoa mz 74 lt 7 los olicvos', 1, '2018-09-29 13:45:53'),
(40, 'ANTONIO', 'GARCIA', 'ARIAS', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(41, 'JOSE', 'GONZALEZ', 'CARMONA', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(42, 'MANUEL', 'RODRIGUEZ', 'CRESPO', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(43, 'FRANCISCO', 'FERNANDEZ', 'ROMAN', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(44, 'DAVID', 'LOPEZ', 'PASTOR', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(45, 'JUAN', 'MARTINEZ', 'SOTO', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(46, 'JOSE ANTONIO', 'SANCHEZ', 'SAEZ', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(47, 'JAVIER', 'PEREZ', 'VELASCO', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(48, 'JOSE LUIS', 'GOMEZ', 'MOYA', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(49, 'DANIEL', 'MARTIN', 'SOLER', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(50, 'FRANCISCO JAVIER', 'JIMENEZ', 'PARRA', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(51, 'JESUS', 'RUIZ', 'ESTEBAN', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(52, 'CARLOS', 'HERNANDEZ', 'BRAVO', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(53, 'ALEJANDRO', 'DIAZ', 'GALLARDO', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(54, 'MIGUEL', 'MORENO', 'ROJAS', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(55, 'JOSE MANUEL', 'MUÑOZ', 'HERRERO', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(56, 'RAFAEL', 'ALVAREZ', 'MONTERO', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(57, 'PEDRO', 'ROMERO', 'LORENZO', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(58, 'MIGUEL ANGEL', 'ALONSO', 'HIDALGO', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(59, 'ANGEL', 'GUTIERREZ', 'GIMENEZ', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(60, 'PABLO', 'NAVARRO', 'IBAÑEZ', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(61, 'JOSE MARIA', 'TORRES', 'FERRER', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(62, 'FERNANDO', 'DOMINGUEZ', 'DURAN', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(63, 'SERGIO', 'VAZQUEZ', 'SANTIAGO', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(64, 'LUIS', 'RAMOS', 'BENITEZ', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(65, 'JORGE', 'GIL', 'VARGAS', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(66, 'ALBERTO', 'RAMIREZ', 'MORA', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(67, 'JUAN CARLOS', 'SERRANO', 'VICENTE', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(68, 'ALVARO', 'BLANCO', 'VIDAL', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(69, 'JUAN JOSE', 'MOLINA', 'PEÑA', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(70, 'DIEGO', 'MORALES', 'FLORES', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(71, 'ADRIAN', 'SUAREZ', 'CABRERA', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(72, 'RAUL', 'ORTEGA', 'CAMPOS', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(73, 'JUAN ANTONIO', 'DELGADO', 'VEGA', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(74, 'IVAN', 'CASTRO', 'FUENTES', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(75, 'ENRIQUE', 'ORTIZ', 'CARRASCO', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(76, 'RUBEN', 'RUBIO', 'DIEZ', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(77, 'RAMON', 'MARIN', 'REYES', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(78, 'VICENTE', 'SANZ', 'CABALLERO', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(79, 'OSCAR', 'NUÑEZ', 'NIETO', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(80, 'ANDRES', 'IGLESIAS', 'AGUILAR', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(81, 'JOAQUIN', 'MEDINA', 'PASCUAL', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(82, 'JUAN MANUEL', 'GARRIDO', 'SANTANA', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(83, 'SANTIAGO', 'CORTES', 'MORALES', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(84, 'EDUARDO', 'CASTILLO', 'SUAREZ', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(85, 'VICTOR', 'SANTOS', 'ORTEGA', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(86, 'MARIO', 'LOZANO', 'DELGADO', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(87, 'ROBERTO', 'GUERRERO', 'CASTRO', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(88, 'JAIME', 'CANO', 'ORTIZ', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(89, 'ANGELA', 'PRIETO', 'MOLINA', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(90, 'SONIA', 'MENDEZ', 'RUBIO', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(91, 'SANDRA', 'CRUZ', 'MARIN', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(92, 'MARINA', 'CALVO', 'SANZ', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(93, 'SUSANA', 'GALLEGO', 'NUÑEZ', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(94, 'YOLANDA', 'HERRERA', 'IGLESIAS', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(95, 'NATALIA', 'MARQUEZ', 'MEDINA', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(96, 'MARGARITA', 'LEON', 'GARRIDO', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(97, 'MARIA JOSEFA', 'VIDAL', 'CORTES', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(98, 'MARIA ROSARIO', 'PEÑA', 'CASTILLO', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(99, 'EVA', 'FLORES', 'SANTOS', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(100, 'INMACULADA', 'CABRERA', 'LOZANO', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(101, 'CLAUDIA', 'CAMPOS', 'GUERRERO', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(102, 'MARIA MERCEDES', 'VEGA', 'CANO', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(103, 'ANA ISABEL', 'FUENTES', 'PRIETO', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(104, 'ESTHER', 'CARRASCO', 'MENDEZ', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(105, 'NOELIA', 'DIEZ', 'CRUZ', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(106, 'CARLA', 'REYES', 'CALVO', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(107, 'VERONICA', 'CABALLERO', 'GALLEGO', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(108, 'SOFIA', 'NIETO', 'HERRERA', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(109, 'ANGELES', 'AGUILAR', 'MARQUEZ', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(110, 'CAROLINA', 'PASCUAL', 'LEON', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(111, 'NEREA', 'SANTANA', 'ROMERO', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(112, 'MARIA VICTORIA', 'HERRERO', 'ALONSO', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(113, 'MARIA ROSA', 'MONTERO', 'GUTIERREZ', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(114, 'EVA MARIA', 'LORENZO', 'NAVARRO', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(115, 'AMPARO', 'HIDALGO', 'TORRES', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(116, 'MIRIAM', 'GIMENEZ', 'DOMINGUEZ', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(117, 'LORENA', 'IBAÑEZ', 'VAZQUEZ', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(118, 'INES', 'FERRER', 'RAMOS', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(119, 'MARIA CONCEPCION', 'DURAN', 'GIL', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(120, 'ANA BELEN', 'SANTIAGO', 'RAMIREZ', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(121, 'MARIA ELENA', 'BENITEZ', 'SERRANO', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(122, 'VICTORIA', 'VARGAS', 'BLANCO', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:08:59'),
(123, 'MARIA ANTONIA', 'MORA', 'GARCIA', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:09:00'),
(124, 'DANIELA', 'VICENTE', 'GONZALEZ', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:09:00'),
(125, 'CATALINA', 'ARIAS', 'RODRIGUEZ', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:09:00'),
(126, 'CONSUELO', 'CARMONA', 'FERNANDEZ', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:09:00'),
(127, 'LIDIA', 'CRESPO', 'LOPEZ', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:09:00'),
(128, 'MARIA NIEVES', 'ROMAN', 'MARTINEZ', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:09:00'),
(129, 'CELIA', 'PASTOR', 'SANCHEZ', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:09:00'),
(130, 'ALEJANDRA', 'SOTO', 'PEREZ', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:09:00'),
(131, 'OLGA', 'SAEZ', 'GOMEZ', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:09:00'),
(132, 'EMILIA', 'VELASCO', 'MARTIN', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:09:00'),
(133, 'GLORIA', 'MOYA', 'JIMENEZ', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:09:00'),
(134, 'LUISA', 'SOLER', 'RUIZ', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:09:00'),
(135, 'AINHOA', 'PARRA', 'HERNANDEZ', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:09:00'),
(136, 'AURORA', 'ESTEBAN', 'DIAZ', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:09:00'),
(137, 'MARIA SOLEDAD', 'BRAVO', 'MORENO', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:09:00'),
(138, 'MARTINA', 'GALLARDO', 'MUÑOZ', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:09:00'),
(139, 'FATIMA', 'ROJAS', 'ALVAREZ', '44444444', '1989-01-01', 'example@hotmail.com', '999999999', 'direccion ejemplo', 1, '2018-10-20 14:09:00');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario`
--

DROP TABLE IF EXISTS `usuario`;
CREATE TABLE IF NOT EXISTS `usuario` (
  `idUsuario` int(11) NOT NULL AUTO_INCREMENT,
  `usuario` varchar(50) NOT NULL,
  `pass` varchar(100) NOT NULL,
  `Perfil_idPerfil` int(11) NOT NULL,
  `Persona_idPersona` int(11) NOT NULL,
  `Estado_idEstado` int(11) NOT NULL,
  `fechaRegistro` datetime NOT NULL,
  PRIMARY KEY (`idUsuario`),
  KEY `Perfil_idPerfil` (`Perfil_idPerfil`) USING BTREE,
  KEY `Persona_idPersona` (`Persona_idPersona`) USING BTREE,
  KEY `Estado_idEstado` (`Estado_idEstado`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `usuario`
--

INSERT INTO `usuario` (`idUsuario`, `usuario`, `pass`, `Perfil_idPerfil`, `Persona_idPersona`, `Estado_idEstado`, `fechaRegistro`) VALUES
(1, 'admin', '$2a$08$Vo4zFrwFG.k2ZHhln/fQVu5NoeJdzJUSG6HOVA6fBCknS/umS0bki', 1, 1, 1, '2018-09-29 14:03:15'),
(44, 'agarciaa', '$2a$08$4FcoicSzHyUZCZAP76PE9eNXmdizVYnXLp6XjabD0Z3TBodC9XKKG', 4, 40, 1, '2018-11-08 15:52:43'),
(45, 'jgonzalezc', '$2a$08$Bm3JV6ZXIiORzaasavTtfu71LnjPj7iMafiQYIDJb/rYqxq7JOdg.', 5, 41, 1, '2018-11-08 15:54:41'),
(46, 'mrodriguezc', '$2a$08$0w8fLxGJqogkYYKUIDa8X.lkTwlE94Hui8FQR4YMpwGvrObtBrmcm', 6, 42, 1, '2018-11-08 15:55:08'),
(47, 'dlopezp', '$2a$08$0ijzyw.L4ST2Dh07eJ7D7eRUAKSI01l6LGpZnugT97CM3yliJE.oG', 7, 44, 1, '2018-11-08 15:55:42'),
(48, 'jmartinezs', '$2a$08$Jgoh55qD5d3Xq5/nGJeGJ.tWd4JysmWaoSBLZGpty6ebkxhCtTKdO', 8, 45, 1, '2018-11-08 15:56:05');

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `login`
--
ALTER TABLE `login`
  ADD CONSTRAINT `FK_Usuario_idUsuario2` FOREIGN KEY (`Usuario_idUsuario`) REFERENCES `usuario` (`idUsuario`);

--
-- Filtros para la tabla `material`
--
ALTER TABLE `material`
  ADD CONSTRAINT `FK_EstadoMaterial` FOREIGN KEY (`Estado_idEstado`) REFERENCES `estado` (`idEstado`);

--
-- Filtros para la tabla `orden`
--
ALTER TABLE `orden`
  ADD CONSTRAINT `FK_ESTADO_ORDEN` FOREIGN KEY (`Estado_idEstado`) REFERENCES `estado` (`idEstado`),
  ADD CONSTRAINT `FK_MATERIAL_ORDEN` FOREIGN KEY (`Material_idMaterial`) REFERENCES `material` (`idMaterial`);

--
-- Filtros para la tabla `ovillado`
--
ALTER TABLE `ovillado`
  ADD CONSTRAINT `FK_ESTADOL_OVILLADO` FOREIGN KEY (`Estado_idEstado`) REFERENCES `estado` (`idEstado`),
  ADD CONSTRAINT `FK_MATERIAL_OVILLADO` FOREIGN KEY (`Material_idMaterial`) REFERENCES `material` (`idMaterial`),
  ADD CONSTRAINT `FK_ORDEN_OVILLADO` FOREIGN KEY (`Orden_idOrden`) REFERENCES `orden` (`idOrden`),
  ADD CONSTRAINT `FK_PERSONA_OVILLADO` FOREIGN KEY (`Persona_idPersona`) REFERENCES `persona` (`idPersona`);

--
-- Filtros para la tabla `perfil`
--
ALTER TABLE `perfil`
  ADD CONSTRAINT `FK_ESTADO` FOREIGN KEY (`Estado_idEstado`) REFERENCES `estado` (`idEstado`);

--
-- Filtros para la tabla `permisos`
--
ALTER TABLE `permisos`
  ADD CONSTRAINT `FK_Perfil_idPerfil` FOREIGN KEY (`Perfil_idPerfil`) REFERENCES `perfil` (`idPerfil`);

--
-- Filtros para la tabla `persona`
--
ALTER TABLE `persona`
  ADD CONSTRAINT `FK_Estado_idEstado` FOREIGN KEY (`Estado_idEstado`) REFERENCES `estado` (`idEstado`);

--
-- Filtros para la tabla `usuario`
--
ALTER TABLE `usuario`
  ADD CONSTRAINT `FK_Estado_idEstado2` FOREIGN KEY (`Estado_idEstado`) REFERENCES `estado` (`idEstado`),
  ADD CONSTRAINT `FK_Perfil_idPerfil2` FOREIGN KEY (`Perfil_idPerfil`) REFERENCES `perfil` (`idPerfil`),
  ADD CONSTRAINT `FK_Persona_idPersona2` FOREIGN KEY (`Persona_idPersona`) REFERENCES `persona` (`idPersona`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
