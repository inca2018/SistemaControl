DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_MATERIAL_ACTUALIZAR`(IN `Descri` VARCHAR(100), IN `estado` INT(11), IN `idMaterialE` INT(11), IN `creador` INT(11))
    NO SQL
BEGIN

UPDATE `material` SET  `Descripcion`=UPPER(Descri),`Estado_idEstado`=estado WHERE `idMaterial`=idMaterialE;

/* ------ REGISTRO DE BITACORA ------ */
SET @NombreUsuario=(SELECT u.usuario FROM usuario u WHERE u.idUsuario=creador);

INSERT INTO `bitacora`(`idBitacora`, `usuarioAccion`, `Accion`, `tablaAccion`,`Detalle`, `fechaRegistro`) VALUES (null,@NombreUsuario,'ACTUALIZACION','Material',CONCAt("SE ACTUALIZO MATERIAL:",Descri),NOW());
END$$
DELIMITER ;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_MATERIAL_HABILITACION`(IN `idMaterialE` INT(11), IN `codigo` INT(11), IN `creador` INT(11))
    NO SQL
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

set @tipotar=(SELECT ni.Descripcion FROM material ni WHERE ni.idMaterial=idMaterialE);

INSERT INTO `bitacora`(`usuarioAccion`, `Accion`, `tablaAccion`,`Detalle`, `fechaRegistro`) VALUES (@usuario,@Mensaje,'MATERIAL',CONCAT(@Mensaje," :", @tipotar),NOW());

END$$
DELIMITER ;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_MATERIAL_RECUPERAR`(IN `idMaterialE` INT(11))
    NO SQL
BEGIN

SELECT * FROM material ni WHERE ni.idMaterial=idMaterialE;
END$$
DELIMITER ;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_MATERIAL_LISTAR`()
    NO SQL
BEGIN

SELECT * FROM material n INNER JOIN estado e ON e.idEstado=n.Estado_idEstado;

END$$
DELIMITER ;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_MATERIAL_REGISTRO`(IN `nom` VARCHAR(100), IN `estado` INT(11), IN `creador` INT(11))
    NO SQL
BEGIN

-- REGISTRAR TIPO DE TARJETA --
INSERT INTO `material`(`idMaterial`, `Descripcion`, `fechaRegistro`, `Estado_idEstado`) VALUES (NULL,UPPER(nom),NOW(),estado);



SET @NombreUsuario=(SELECT concat(p.nombrePersona,' ',p.apellidoPaterno,' ',p.apellidoMaterno) as NombresPersona FROM usuario u inner join persona p ON p.idPersona=u.Persona_idPersona WHERE u.idUsuario=creador);


INSERT INTO `bitacora`(`idBitacora`, `usuarioAccion`, `Accion`, `tablaAccion`,`Detalle`, `fechaRegistro`) VALUES (null,@NombreUsuario,'INSERTAR','SE REGISTRO MATERIAL','MATERIAL',NOW());

END$$
DELIMITER ;
