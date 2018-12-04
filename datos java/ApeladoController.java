package net.servir.expedientes.web.controller;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import net.servir.expedientes.dao.jdbc.JdbcApeladoDAO;
import net.servir.expedientes.model.auxiliar.Cargo;
import net.servir.expedientes.model.entity.Apelado;
import net.servir.expedientes.model.entity.Entidad;
import net.servir.expedientes.model.entity.Expediente;
import net.servir.expedientes.model.entity.OficioApertura;
import net.servir.expedientes.model.entity.Persona;
import net.servir.expedientes.model.jsp.JspApelado;
import net.servir.expedientes.model.rpt.RptRepresentante;
import net.servir.expedientes.model.seguridad.Usuario;
import net.servir.expedientes.service.ApeladoService;
import net.servir.expedientes.service.ApelanteService;
import net.servir.expedientes.service.ExpedienteService;
import net.servir.expedientes.service.SelectorService;
import net.servir.expedientes.util.ConsultaWSUtil;
import net.servir.expedientes.util.Entorno;
import net.servir.expedientes.util.NumeroUtil;
import net.servir.expedientes.util.VOUtil;
import net.servir.expedientes.web.util.ListadosController;

@Controller
public class ApeladoController extends ConsultaWSUtil {
	
	private static final Logger logger = Logger.getLogger(ApeladoController.class);

	@Autowired
	private ApeladoService apeladoService;
	
	@Autowired
	private ApelanteService apelanteService;
	
	@Autowired
	private SelectorService selectorService;
	
	@Autowired
	private ExpedienteService expedienteService;
	
	@Autowired
	private ListadosController listadosController;
	
	private JdbcApeladoDAO apeladoDAO= new JdbcApeladoDAO();
	@SuppressWarnings("unused")
	@RequestMapping("/apelado.htm")
	public @ModelAttribute("objmain") JspApelado difusion(HttpServletRequest request) {
		
		HttpSession session = request.getSession();
		JspApelado 	objmain = new JspApelado();
		
		String accion = request.getParameter("accion");
		
		
		objmain.setAccion(accion);
		
		String busquedaPor = request.getParameter("busquedaPor"); // Puede ser null, RUC o ENT
		
		if(!VOUtil.isEmpty(busquedaPor)){	// Existe dato en radio button
			if(busquedaPor.equals("RUC")){
				objmain.setBusqueda("RUC");
			} else {
				objmain.setBusqueda("ENT");
			}
		} else {	// Por defecto
			objmain.setBusqueda("RUC");
		}
		
		if (objmain.getAccion() != null) {
			
			// Gestión de Usuario
			Usuario usuario = (Usuario) session.getAttribute("usuario");
			// usuario = selectorService.ObtenerUsuario(usuario.getUsuario(), usuario.getClave());
			usuario.setAd_id_session(session.getId());
			usuario.setAd_id_usuario(usuario.getId_usuario());
			
			session.setAttribute("usuario", usuario);
			
			objmain.setUsuario(usuario);
			Entorno.setUsuario(usuario);
			
			/** RECUPERANDO DATOS **/
			String id_expediente 	= request.getParameter("id_expediente");
			
			String id_entidad 		= request.getParameter("id_entidad");
			String fecha_sistema 	= request.getParameter("fecha_sistema");
			String id_persona 		= request.getParameter("id_persona");
			
			String rucEntidad 		= request.getParameter("rucEntidad");
			
			String id_entidad_busqueda = request.getParameter("id_entidad_busqueda");
			
			String entidadNombre	= request.getParameter("entidad");
			String direccion 		= request.getParameter("direccion");
			String id_sector 		= request.getParameter("id_sector");
			String coddep 			= request.getParameter("coddep");
			String codpro 			= request.getParameter("codpro");
			String coddis 			= request.getParameter("coddis");
			
			String dni 				= request.getParameter("dni");
			String cod_tipo 		= request.getParameter("cod_tipo");
			String numero_siglas 	= request.getParameter("numero_siglas");
			String nombre 			= request.getParameter("nombre");
			String primer_apellido 	= request.getParameter("primer_apellido");
			String segundo_apellido = request.getParameter("segundo_apellido");
			String id_cargo 		= request.getParameter("id_cargo");
			String f_documento 		= request.getParameter("f_documento");
			//String f_ingreso_servir = request.getParameter("f_ingreso_servir");
			String f_creacion_exp = request.getParameter("f_creacion_exp");
			String f_ingreso_tsc 	= request.getParameter("f_ingreso_tsc");
			String numero_registro 	= request.getParameter("numero_registro");
			String cod_tipo_documento = request.getParameter("cod_tipo_documento");
			String cex				= request.getParameter("cex");
			/** RECUPERANDO DATOS **/
			
			
			Apelado apelado = new Apelado();
			Entidad entidad = new Entidad();
			Persona persona = new Persona();
			
			OficioApertura oficioApertura = new OficioApertura();

			Apelado apelado_inicial = null;

			List<RptRepresentante> RptRepresentante = null;
			
			List<Entidad> entidadesSubSedes  = null;
			
			String entidadr = "";

			String ubigeo = "000000";
			
			
			objmain.setApelado(apelado);
			objmain.getApelado().setEntidad(entidad);
			objmain.getApelado().setOficioApertura(oficioApertura);
			objmain.getApelado().setPersona(persona);

			
			if (	id_expediente != null
				&& 	!id_expediente.trim().equals("")) {
				
				objmain.setId_expediente(Integer.parseInt(id_expediente));
				Expediente expediente = expedienteService.getExpediente(objmain.getId_expediente());
				objmain.setExpediente(expediente);
				if(expediente!=null)
					objmain.setRutaCMS(expediente.getRutaCMS());
			}
			
			if (	id_entidad != null
				&& 	!id_entidad.trim().equals("")) {
			
				entidad.setId_entidad(Integer.parseInt(id_entidad));
				apelado.setEntidad(entidad);
			}
			
			objmain.setFecha_sistema(fecha_sistema);
			
			if (	id_persona != null
				&& 	!id_persona.trim().equals("")) {
			
				oficioApertura.setId_persona(Integer.parseInt(id_persona));
			}
			
			objmain.getApelado().getEntidad().setNumRucEntidad(rucEntidad);
			
			objmain.getApelado().getEntidad().setEntidad(entidadNombre);
			
			objmain.getApelado().getEntidad().setDireccion(direccion);
			
			if (	id_sector != null
				&& 	!id_sector.trim().equals("")) {
			
				objmain.getApelado().getEntidad().setId_sector(Integer.parseInt(id_sector));
			}
			
			objmain.getApelado().getEntidad().setCoddep(coddep);
			objmain.getApelado().getEntidad().setCodpro(codpro);
			objmain.getApelado().getEntidad().setCoddis(coddis);
			
			objmain.getApelado().getOficioApertura().setDni(dni);
			
			objmain.getApelado().getOficioApertura().setCod_tipo(cod_tipo);
			
			objmain.getApelado().getOficioApertura().setNumero_siglas(numero_siglas);
			
			objmain.getApelado().getOficioApertura().setNombre(nombre);
			
			objmain.getApelado().getOficioApertura().setPrimer_apellido(primer_apellido);
			
			objmain.getApelado().getOficioApertura().setSegundo_apellido(segundo_apellido);
			
			if (	id_cargo != null
				&& 	!id_cargo.trim().equals("")) {
				
				objmain.getApelado().getOficioApertura().setId_cargo(Integer.parseInt(id_cargo));
			}
			
			objmain.getApelado().getOficioApertura().setF_documento(f_documento);
			
			//objmain.getApelado().getOficioApertura().setF_ingreso_servir(f_ingreso_servir);
			objmain.getApelado().getOficioApertura().setF_creacion_exp(f_creacion_exp);
			
			objmain.getApelado().getOficioApertura().setF_ingreso_tsc(f_ingreso_tsc);
			
			objmain.getApelado().getOficioApertura().setNumero_registro(numero_registro);
			
			objmain.getApelado().getOficioApertura().setId_cargo(NumeroUtil.parsearAInteger(id_cargo));
			
			/** arreglos para combo **/
			objmain.setSectors(apeladoService.getSectors());
			objmain.setTipoDocumentos(apeladoService.getTipoDocumentos());
			objmain.setCargos(apeladoService.getCargos("APD"));
			objmain.setDepartamentos(apeladoService.ObtenerDepartamentos());
			
			
			// cargar apelante
			if (objmain.getAccion().equals("INICIAR")) {

				/** Datos iniciales y básicos **/
				if (	objmain.getId_expediente() != null
					&& 	objmain.getId_expediente().intValue() != 0) {
					
					// arreglos de asignaciones
					Apelado ape = apeladoService.getApelado(objmain.getId_expediente());
					
					if(ape != null) {
						
						objmain.setApelado(ape);
						
						if(objmain.getApelado().getOficioApertura() != null) {
							
							if(objmain.getApelado().getOficioApertura().getId_persona() != null) {
								
								persona= apelanteService.getPersona(objmain.getApelado().getOficioApertura().getId_persona());
								objmain.getApelado().setPersona(persona);
							}
							
							apelado_inicial = objmain.getApelado();
						}
						
					}
				}
				
				
				if (	 objmain.getApelado() != null
					 &&  objmain.getApelado().getEntidad() != null) {
						
					coddep = objmain.getApelado().getEntidad().getCoddep();//ubigeo.substring(0, 2);
					codpro = objmain.getApelado().getEntidad().getCodpro();//ubigeo.substring(2, 4);
					coddis = objmain.getApelado().getEntidad().getCoddis();//ubigeo.substring(4, 6);
					
					objmain.getApelado().getEntidad().setCoddep(coddep);
					objmain.getApelado().getEntidad().setCodpro(codpro);
					objmain.getApelado().getEntidad().setCoddis(coddis);
					
					objmain.setJefes_rrhh(apeladoService.getListJefesRRHH(objmain.getApelado().getEntidad().getId_entidad()));
					
				} else {
					
					apelado.setEntidad(entidad);
					
					if (objmain.getApelado() == null){
						objmain.setApelado(apelado);
					}
					
				}
				
				if (objmain.getApelado().getEntidad() != null) {
					
					entidadr = objmain.getApelado().getEntidad().getId_entidad() == null ? "0" : objmain.getApelado().getEntidad().getId_entidad().toString();
					
					RptRepresentante = apeladoService.getListRepresentante(entidadr == null ? 0 : NumeroUtil.parsearAInteger((entidadr)),NumeroUtil.parsearAInteger(id_expediente));
				}
				
				logger.info("ENT: " + entidadr);
				
				//objmain.setBusqueda("ENT");
				
			}
			
			// Buscar x Sunat
			if (objmain.getAccion().equals("BUSCARSUNAT")) {
				
				rucEntidad = request.getParameter("rucEntidad").trim();
				/*
				if(objmain.getBusqueda().equals("RUC")){
					rucEntidad = request.getParameter("rucEntidadPadre").trim();
				} else if(objmain.getBusqueda().equals("ENT")){
					rucEntidad = request.getParameter("rucEntidad").trim();
				}
				*/
				
				entidad.setNumRucEntidad(rucEntidad);
				
				// puede ser nullo el apelado
				if (objmain.getApelado() == null) {
					
					apelado.setEntidad(entidad);
					objmain.setApelado(apelado);
				
				} else {
					objmain.getApelado().setEntidad(entidad);
				}
				
				Entidad wsEntidad = super.buscarEntidadXRuc(rucEntidad);
				
				if (	wsEntidad != null
					&& 	(	wsEntidad.getNumRucEntidad() != null 
						&& 	wsEntidad.getNumRucEntidad().length() > 0)) {
					
					logger.info("ApeladoController: Se obtuvieron datos de entidad");
					
					entidad.setId_entidad(wsEntidad.getId_entidad());
					entidad.setEntidad(wsEntidad.getEntidad());
					entidad.setDireccion(wsEntidad.getDireccion());
					//entidad.setUbigeo(wsEntidad.getUbigeo());
					entidad.setId_sector(wsEntidad.getId_sector()); // Se escojera el sector
					
					if (wsEntidad != null) {
					
						coddep = wsEntidad.getCoddep();//ubigeo.substring(0, 2);
						codpro = wsEntidad.getCodpro();//ubigeo.substring(2, 4);
						coddis = wsEntidad.getCoddis();//ubigeo.substring(4, 6);
						
						entidad.setCoddep(coddep);
						entidad.setCodpro(codpro);
						entidad.setCoddis(coddis);
					}
					
					apelado.setEntidad(entidad);
					persona.setNombre(nombre);
					persona.setPrimer_apellido(primer_apellido);
					persona.setSegundo_apellido(segundo_apellido);
					persona.setDni(dni);
					persona.setCex(cex);
					persona.setCod_tipo_documento(cod_tipo_documento);
					persona.setId_persona(NumeroUtil.parsearAInt(id_persona));
					if(NumeroUtil.parsearAInt(id_persona)==0)
						persona.setId_persona(null);
					
					apelado.setPersona(persona);
					objmain.setApelado(apelado);
					
					objmain.setJefes_rrhh(apeladoService.getListJefesRRHH(objmain.getApelado().getEntidad().getId_entidad()));
					
				} else {
					logger.info("ApeladoController: No se pudieron obtener datos de entidad");
					objmain.getApelado().setEntidad(null);
					objmain.setMensaje_error("Sin resultados para mostrar.");
				}
				
			}
			
			
			/*Antes : BUSCARDNI*/
			
			
			if(objmain.getAccion().equals("OBTENER_JEFE_RRHH")) {
				
				if(id_persona!=null && !id_persona.equals("")) {
					
					objmain.getApelado().setPersona(apelanteService.getPersona(NumeroUtil.parsearAInteger(id_persona)));
					objmain.getApelado().getOficioApertura().setDni(objmain.getApelado().getPersona().getDni());
					objmain.getApelado().setDni(objmain.getApelado().getOficioApertura().getDni());
					
					objmain.getApelado().getOficioApertura().setId_persona(objmain.getApelado().getPersona().getId_persona());
					
					apelado.getOficioApertura().setId_persona(objmain.getApelado().getOficioApertura().getId_persona());
				}
			}
			
			
			if(objmain.getAccion().equals("OBTENER_JEFE_RRHH") || objmain.getAccion().equals("BUSCARDNI")) {
				
				if (objmain.getApelado().getPersona() == null) {
					
					objmain.setMensaje_error("Sin resultado para mostrar.");
					
				} else {
					
					objmain.getApelado().getOficioApertura().setId_persona(objmain.getApelado().getPersona().getId_persona());
					objmain.getApelado().getOficioApertura().setDni(objmain.getApelado().getPersona().getDni());
					objmain.getApelado().getOficioApertura().setNombre(objmain.getApelado().getPersona().getNombre());
					objmain.getApelado().getOficioApertura().setPrimer_apellido(objmain.getApelado().getPersona().getPrimer_apellido());
					objmain.getApelado().getOficioApertura().setSegundo_apellido(objmain.getApelado().getPersona().getSegundo_apellido());
					
					if(!VOUtil.isEmpty(cod_tipo))
						oficioApertura.setCod_tipo(cod_tipo);
					if(!VOUtil.isEmpty(numero_siglas))
						oficioApertura.setNumero_siglas(numero_siglas);
					if(!VOUtil.isEmpty(f_documento))	
						oficioApertura.setF_documento(f_documento);
					/*if(!VOUtil.isEmpty(f_ingreso_servir))
						oficioApertura.setF_ingreso_servir(f_ingreso_servir);*/
					if(!VOUtil.isEmpty(f_creacion_exp))
						oficioApertura.setF_creacion_exp(f_creacion_exp);
					
					if(!VOUtil.isEmpty(f_ingreso_tsc))
						oficioApertura.setF_ingreso_tsc(f_ingreso_tsc);
					
					oficioApertura.setId_persona(objmain.getApelado().getPersona().getId_persona());
					
					if(!VOUtil.isEmpty(id_cargo))
						oficioApertura.setId_cargo(Integer.parseInt(id_cargo));
					if(!VOUtil.isEmpty(numero_registro))
						oficioApertura.setNumero_registro(numero_registro);
					
					apelado.setOficioApertura(oficioApertura);
				}
				
				if(!VOUtil.isEmpty(id_entidad)){
					RptRepresentante = apeladoService.getListRepresentante(Integer.parseInt(id_entidad),NumeroUtil.parsearAInteger(id_expediente));
					objmain.setJefes_rrhh(apeladoService.getListJefesRRHH(NumeroUtil.parsearAInteger(id_entidad)));
				}
				
			}

			// grabar
			if (objmain.getAccion().equals("GRABAR")) {
				
				if(		id_expediente != null 
					&& 	id_expediente != "") {
					
					oficioApertura.setId_expediente(Integer.parseInt(id_expediente));
				}
				
				Integer verNumeroRegistro = apeladoService.getVerNumeroRegistro(oficioApertura);

				if (verNumeroRegistro > 0) {
					
					objmain.setMensaje_usuario("El número de registro esta asignado a uno ya existente, por favor verifique el dato ingresado");

					objmain.setTipo_mensaje("2");// aviso
//					oficioApertura.setF_documento(f_documento_pro);
//					oficioApertura.setF_ingreso_servir(f_ingreso_servir_pro);
//					oficioApertura.setF_ingreso_tsc(f_ingreso_tsc_pro);
				}
				
				if (verNumeroRegistro == 0) {

					try {
						
						apelado_inicial = apeladoService.getApelado(objmain.getId_expediente());
						
						id_persona =request.getParameter("id_persona");
						
						if (	id_persona != null 
							&& 	id_persona != "" 
							&& 	!id_persona.equals("0")) {
							
							apelado.getOficioApertura().setId_persona(NumeroUtil.parsearAInteger(id_persona));
						
						} else {
							
							if (	nombre != null 
								&& 	nombre != "" 
								&& 	primer_apellido != null 
								&& 	primer_apellido != "") {
								
								persona.setDni(dni);
								persona.setCod_tipo_documento(cod_tipo_documento);
								persona.setCex(cex);
								persona.setNombre(nombre);
								persona.setPrimer_apellido(primer_apellido);
								persona.setSegundo_apellido(segundo_apellido);
								
								if (	dni == "" 
									&&  cex == "") {
									
									apelado.getOficioApertura().setId_persona(apelanteService.insertPersona(persona, usuario));
								
								} else {
									
									Persona personaTmp=null;
									
									if (cod_tipo_documento.equals("CEX")){
										
										personaTmp =apelanteService.getPersona_dni(cex);
									
									} else {
										
										personaTmp =apelanteService.getPersona_dni(dni);
									}
									
									if (personaTmp == null) {
										
										apelado.getOficioApertura().setId_persona(apelanteService.insertPersona(persona, usuario));
									
									} else {
										persona.setId_persona(personaTmp.getId_persona());
										apelado.getOficioApertura().setId_persona(persona.getId_persona());
									}
								}
							}
							
						}
						
						if(apelado.getId_expediente()==null && id_expediente!="") {
							apelado.setId_expediente(Integer.parseInt(id_expediente));
						}
					
						logger.info("---");
						logger.info(oficioApertura);
						logger.info(apelado);
						logger.info(apelado.getEntidad());
						logger.info(apelado.getOficioApertura());
						logger.info("persona:"+ apelado.getOficioApertura().getId_persona());
						logger.info("ot entidad:" + apelado.getEntidad().getId_entidad());
						
						if(		apelado.getOficioApertura().getId_entidad() == null 
							&& 	id_entidad != "" 
							&& 	id_entidad != null){
							
							apelado.getOficioApertura().setId_entidad(Integer.parseInt(id_entidad));
						}
						
						apelado.getEntidad().setSub_sede(request.getParameter("subSede"));
						
						if(apelado.getEntidad().getSub_sede()==null){
							apelado.getEntidad().setSub_sede("0");
						}
						
						if (apeladoService.saveApelado(apelado, apelado_inicial, (Usuario) session.getAttribute("usuario"))){
							
							objmain.setMensaje_usuario("Proceso ejecutado correctamente");
							objmain.setTipo_mensaje("1");// exit
						
						} else {
							objmain.setMensaje_usuario("Hubo un problema al ejecutar proceso");
							objmain.setTipo_mensaje("0");// error
						}
					
					} catch (SQLException e) {
						objmain.setMensaje_usuario("Error al ejecutar proceso");
						objmain.setTipo_mensaje("0");// error
						e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
					}
					
					objmain.setApelado(apeladoService.getApelado(objmain.getId_expediente()));
					
					//ubigeo = objmain.getApelado().getEntidad().getUbigeo();
					
					//if (	ubigeo != null 
						//&& 	!ubigeo.equals("")) {
						
						coddep = /*objmain.getApelado()*/apelado.getEntidad().getCoddep();//ubigeo.substring(0, 2);
						codpro = /*objmain.getApelado()*/apelado.getEntidad().getCodpro();//ubigeo.substring(2, 4);
						coddis = /*objmain.getApelado()*/apelado.getEntidad().getCoddis();//ubigeo.substring(4, 6);
				//	}
					
					if (objmain.getApelado().getEntidad() == null) {
						
						objmain.getApelado().setEntidad(new Entidad());						
					}
					
					objmain.getApelado().getEntidad().setCoddep(coddep);
					objmain.getApelado().getEntidad().setCodpro(codpro);
					objmain.getApelado().getEntidad().setCoddis(coddis);
					
					/** Validar por cambio en BD - Se va adicionar metodo de buscar persona al obtener apelado**/
					Persona prmPersona = new Persona();
					if(!VOUtil.isEmpty(dni))
						prmPersona.setDni(dni); 
					if(!VOUtil.isEmpty(cex))
						prmPersona.setCex(cex); 
					if(!VOUtil.isEmpty(nombre))
						prmPersona.setNombre(nombre); 
					if(!VOUtil.isEmpty(primer_apellido))
						prmPersona.setPrimer_apellido(primer_apellido); 
					if(!VOUtil.isEmpty(segundo_apellido))
						prmPersona.setSegundo_apellido(segundo_apellido); 
					prmPersona.setCod_tipo_documento(cod_tipo_documento);
					objmain.getApelado().setPersona(prmPersona);
				}
				
				// List RptRepresentante = null;
				if(objmain.getApelado().getId_entidad() != null) {
					
					RptRepresentante = apeladoService.getListRepresentante(objmain.getApelado().getId_entidad(),NumeroUtil.parsearAInteger(id_expediente));
					
					objmain.setJefes_rrhh(apeladoService.getListJefesRRHH(objmain.getApelado().getId_entidad()));
				}
			}
			
			if(objmain.getAccion().equals("BUSCAR_SUB_SEDES")){
				String entidadSub= request.getParameter("nomEntidad");
				apelado.setEntidad(apeladoService.getEntidad(NumeroUtil.parsearAInteger(id_entidad)));
				if(apelado.getEntidad()!=null){
					coddep=apelado.getEntidad().getCoddep();
					coddis=apelado.getEntidad().getCoddis();
					codpro=apelado.getEntidad().getCodpro();
					objmain.setJefes_rrhh(apeladoService.getListJefesRRHH(apelado.getEntidad().getId_entidad()));
				}
				persona.setNombre(nombre);
				persona.setPrimer_apellido(primer_apellido);
				persona.setSegundo_apellido(segundo_apellido);
				persona.setDni(dni);
				persona.setCex(cex);
				persona.setCod_tipo_documento(cod_tipo_documento);
				persona.setId_persona(NumeroUtil.parsearAInt(id_persona));
				if(NumeroUtil.parsearAInt(id_persona)==0)
					persona.setId_persona(null);
				
				apelado.setPersona(persona);
				objmain.setApelado(apelado);
				
				/*if(entidadSub!=null && entidadSub!=""){
					entidad.setSub_sede("1");
					entidadesSubSedes=apeladoService.getListEntidadSubSede(entidadSub);
					objmain.setDataListSubSedes(entidadesSubSedes);
				}*/
			}
			
			if(objmain.getAccion().equals("OBTENER_SUBSEDE") || (!VOUtil.isEmpty(request.getParameter("subSedeHdn")) && request.getParameter("subSedeHdn").equals("1")
					&& !objmain.getAccion().equals("BUSCARSUNAT"))){
				String id_entidad_sub= request.getParameter("sub_entidad");
				if(NumeroUtil.parsearAInteger(id_entidad_sub)!=0){
					apelado.setEntidad(apeladoService.getEntidad(NumeroUtil.parsearAInteger(id_entidad_sub)));
				}else{
					apelado.setEntidad(apeladoService.getEntidad(NumeroUtil.parsearAInteger(id_entidad)));
					if(apelado.getEntidad()!=null){
						if(NumeroUtil.parsearAInteger(apelado.getEntidad().getId_padre())!=0){
							apelado.setEntidad(apeladoService.getEntidad(NumeroUtil.parsearAInteger(apelado.getEntidad().getId_padre())));
						}else{
							apelado.setEntidad(apeladoService.getEntidad(apelado.getEntidad().getId_entidad()));
						}
						
					}
				}
				if(apelado.getEntidad()!=null){
					coddep=apelado.getEntidad().getCoddep();
					coddis=apelado.getEntidad().getCoddis();
					codpro=apelado.getEntidad().getCodpro();
					objmain.setJefes_rrhh(apeladoService.getListJefesRRHH(apelado.getEntidad().getId_entidad()));
				}
				persona.setNombre(nombre);
				persona.setPrimer_apellido(primer_apellido);
				persona.setSegundo_apellido(segundo_apellido);
				persona.setDni(dni);
				persona.setCex(cex);
				persona.setCod_tipo_documento(cod_tipo_documento);
				persona.setId_persona(NumeroUtil.parsearAInt(id_persona));
				if(NumeroUtil.parsearAInt(id_persona)==0)
					persona.setId_persona(null);
				
				apelado.setPersona(persona);
				objmain.setApelado(apelado);
				
			}
			
			// busqueda X DNI
			if (objmain.getAccion().equals("BUSCARDNI")) {

				oficioApertura.setDni(request.getParameter("dni"));
				
				/* Inicio de consulta de datos de la persona SGE,Maestras,Reniec */
				apelado.setDni(request.getParameter("dni"));
				Persona wsPersonaReniec = super.buscarPersonaXDni(apelado.getDni());
				
				if (wsPersonaReniec != null) {
					
					if (	wsPersonaReniec.getDni() != null
						||  wsPersonaReniec.getDni().toString().trim().length() > 0) {
						
						objmain.getApelado().setPersona(apeladoService.getPersona(wsPersonaReniec.getDni()));
						objmain.getApelado().getOficioApertura().setDni(objmain.getApelado().getPersona().getDni());
						objmain.getApelado().setDni(objmain.getApelado().getOficioApertura().getDni());
						
						objmain.getApelado().getOficioApertura().setId_persona(objmain.getApelado().getPersona().getId_persona());
						
						apelado.getOficioApertura().setId_persona(objmain.getApelado().getOficioApertura().getId_persona());
					}
				} else {
					objmain.getApelado().setPersona(null);
				}
				
				/* Fin de consulta de datos de la persona SGE,Maestras,Reniec */
			}
			
			/** AFTER ALL ACTIONS **/
			if(coddep != null){
				if (!coddep.equals("00")) {
					objmain.setProvincias(apeladoService.ObtenerProvincias(coddep));
				}
	
				if (!codpro.equals("00")) {
					objmain.setDistritos(apeladoService.ObtenerDistritos(coddep, codpro));
				}
			}
			
			/** GALAXYBIS **/
			objmain.setDataList(RptRepresentante);
			/** GALAXYBIS **/

			request.setAttribute("USUARIO", usuario);
		}
		
		if (objmain.getApelado().getEntidad() != null) {
			
			if (objmain.getApelado().getEntidad().getSub_sede()!=null){
				
				if (objmain.getApelado().getEntidad().getSub_sede().equals("1")) {
					
					objmain.setListSubsedes(apeladoDAO.getListSubSedes(objmain.getApelado().getEntidad().getId_padre()));
					objmain.setEntidad(apeladoDAO.getEntidad(NumeroUtil.parsearAInteger(objmain.getApelado().getEntidad().getId_padre())));
				
				} else {
					
					if (objmain.getApelado().getEntidad().getId_entidad() != null) {
						
						objmain.setListSubsedes(apeladoDAO.getListSubSedes(objmain.getApelado().getEntidad().getId_entidad().toString()));
						objmain.setEntidad(apeladoDAO.getEntidad(NumeroUtil.parsearAInteger(objmain.getApelado().getEntidad().getId_entidad())));	
					}
				}
			} else {
				
				if (objmain.getApelado().getEntidad().getId_entidad() != null) {
					
					objmain.setListSubsedes(apeladoDAO.getListSubSedes(objmain.getApelado().getEntidad().getId_entidad().toString()));
					objmain.setEntidad(apeladoDAO.getEntidad(NumeroUtil.parsearAInteger(objmain.getApelado().getEntidad().getId_entidad())));	
				}
			}
		}
		
		if (	objmain.getApelado() != null
			&&	objmain.getApelado().getOficioApertura() != null
			&&	objmain.getApelado().getOficioApertura().getId_cargo() != null) {
			
			if (objmain.getCargos() != null) {
				
				boolean existe = false;
				
				for (int i = 0; i < objmain.getCargos().size(); i++) {
					
					Cargo cargo = objmain.getCargos().get(i);
					
					if (cargo.getId_cargo().intValue() == objmain.getApelado().getOficioApertura().getId_cargo().intValue()) {
						
						existe = true;
						break;
					}
				}
				
				if (!existe) {

					Cargo cargo = listadosController.getCargo(objmain.getApelado().getOficioApertura().getId_cargo().intValue());
					
					objmain.getCargos().addFirst(cargo);
				}
			}
		}
		
		return objmain;
	}
}
