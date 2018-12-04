package net.servir.expedientes.dao.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.servir.expedientes.dao.ApeladoDAO;
import net.servir.expedientes.model.auxiliar.Cargo;
import net.servir.expedientes.model.auxiliar.EntidadVO;
import net.servir.expedientes.model.auxiliar.Generica;
import net.servir.expedientes.model.auxiliar.TipoDocumento;
import net.servir.expedientes.model.auxiliar.TipoOficio;
import net.servir.expedientes.model.entity.Apelado;
import net.servir.expedientes.model.entity.Entidad;
import net.servir.expedientes.model.entity.OficioApertura;
import net.servir.expedientes.model.entity.Persona;
import net.servir.expedientes.model.rpt.RptRepresentante;
import net.servir.expedientes.model.seguridad.Usuario;
import net.servir.expedientes.util.ConexionUtil;
import net.servir.expedientes.util.ConnectionJndi;
import net.servir.expedientes.util.NumeroUtil;
import net.servir.expedientes.util.VOUtil;
import oracle.jdbc.OracleTypes;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcApeladoDAO implements ApeladoDAO {

	private final Logger logger = Logger.getLogger(this.getClass());
	
	public JdbcApeladoDAO() {
	}

	public LinkedList<Cargo> getCargos(String tipo_cargo) {

		LinkedList<Cargo> list = new LinkedList<Cargo>();
		
		ConnectionJndi connectionJndi = null;
		
		Connection conn = null;
		CallableStatement cstmt = null;
		ResultSet resultSets = null;
		
		try {

			connectionJndi = ConexionUtil.getConnectionJndiExpediente();
			
			conn = connectionJndi.getConnection();
			
			cstmt = conn.prepareCall("{ CALL PKG_APELANTE_GESTION.SP_LISTR_CARGOS(?, ?) }");
			
			cstmt.setString("IN_TIPOS_CARGOS", tipo_cargo);
			
			cstmt.registerOutParameter("P_C_CURSOR", OracleTypes.CURSOR);
			
			cstmt.execute();
			
			resultSets = (ResultSet) cstmt.getObject("P_C_CURSOR");
			
			while(resultSets.next()) {
				
				Cargo obj = new Cargo();
				
				obj.setId_cargo((Integer)resultSets.getInt("ID_CARGO"));
				obj.setDescripcion((String)resultSets.getString("DESCRIPCION"));
				
				list.add(obj);
			}	
		} catch (NumberFormatException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		} catch (SQLException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		}finally {
			ConexionUtil.closeSqlConnections(resultSets, cstmt, conn);
			ConexionUtil.closeContext(connectionJndi);
		}
		
		return list;
	}

	public Apelado getApelado(Number id_expediente) {

		Apelado obj = null;
		
		ConnectionJndi connectionJndi = null;
		
		Connection conn = null;
		CallableStatement cstmt = null;
		ResultSet resultSets = null;

		try {

			connectionJndi = ConexionUtil.getConnectionJndiExpediente();
			
			conn = connectionJndi.getConnection();
			
			cstmt = conn.prepareCall("{ CALL PKG_APELADO_CONSULTA.SP_OBTENER_APELADO(?,?)}");
			
			cstmt.setInt("IN_ID_EXPEDIENTE", (Integer) id_expediente);
			
			cstmt.registerOutParameter("P_C_CURSOR", OracleTypes.CURSOR);
			
			cstmt.execute();
			
			resultSets = (ResultSet) cstmt.getObject("P_C_CURSOR");
			
			while(resultSets.next()) {
				
				obj = new Apelado();
				
				obj.setId_expediente((Number)resultSets.getInt("ID_EXPEDIENTE"));
				obj.setId_entidad((Number)resultSets.getInt("ID_ENTIDAD"));
				obj.setDireccion(resultSets.getString("DIRECCION"));
			}	
			
			if (obj == null) {
				obj = new Apelado();
				obj.setId_expediente(id_expediente);
			}
			
			// Obtener Entidad
			obj.setEntidad(this.getEntidad(obj.getId_entidad()));
			
			// Obtener Oficio Apertura
			obj.setOficioApertura(this.getOficioApertura(obj.getId_expediente()));
			
		} catch (NumberFormatException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		} catch (SQLException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		}finally {
			ConexionUtil.closeSqlConnections(resultSets, cstmt, conn);
			ConexionUtil.closeContext(connectionJndi);
		}
		
		return obj;
	}

	public Entidad getEntidad(Number idEntidad) {

		Entidad obj = null;
		
		ConnectionJndi connectionJndi = null;
		
		Connection conn = null;
		CallableStatement cstmt = null;
		ResultSet resultSets = null;
		if (idEntidad == null){
			idEntidad = 0;
		}
		try {

			connectionJndi = ConexionUtil.getConnectionJndiExpediente();
			
			conn = connectionJndi.getConnection();
			
			cstmt = conn.prepareCall("{ CALL PKG_APELADO_CONSULTA.SP_OBTENER_ENTIDAD_APELADO(?,?)}");
			cstmt.setInt("IN_ID_ENTIDAD", (Integer)  idEntidad);
			cstmt.registerOutParameter("P_C_CURSOR", OracleTypes.CURSOR);
			cstmt.execute();
			resultSets = (ResultSet) cstmt.getObject("P_C_CURSOR");
			
			while(resultSets.next()){
				obj = new Entidad();
				obj.setId_entidad((Number)resultSets.getInt("ID_ENTIDAD"));
				obj.setDireccion((String)resultSets.getString("DIRECCION"));
				obj.setCodpro((String)resultSets.getString("CODPRO"));
				obj.setCoddis((String)resultSets.getString("CODDIS"));
				obj.setCoddep((String)resultSets.getString("CODDEP"));
				obj.setEntidad((String)resultSets.getString("ENTIDAD"));
				obj.setId_sector(resultSets.getInt("ID_SECTOR"));
				obj.setCargo((String)resultSets.getString("DESCRIPCION"));
				obj.setPrimer_apellido((String)resultSets.getString("PRIMER_APELLIDO"));
				obj.setSegundo_apellido((String)resultSets.getString("SEGUNDO_APELLIDO"));
				obj.setNombre((String)resultSets.getString("NOMBRE"));
				obj.setEstado((String)resultSets.getString("ESTADO"));
				//obj.setNumRucEntidad((String)resultSets.getString("RUC"));
				obj.setSub_sede((String)resultSets.getString("SUB_SEDE"));
				
				if(!VOUtil.isEmpty(obj.getSub_sede()) && obj.getSub_sede().equals("1")){	// Es SubSede
					obj.setNumRucEntidadSubSede((String)resultSets.getString("RUC"));
				} else {
					obj.setNumRucEntidad((String)resultSets.getString("RUC"));
				}
				
				obj.setId_padre(resultSets.getString("ID_ENTIDAD_PADRE"));
				obj.setFlagValidadoSunat(resultSets.getString("FLAG_VALIDADO_X_SUNAT"));
			}	
			
		} catch (NumberFormatException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		} catch (SQLException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		}finally {
			ConexionUtil.closeSqlConnections(resultSets, cstmt, conn);
			ConexionUtil.closeContext(connectionJndi);
		}
		return obj;

	}


	public String getEntidadDenominacion(Number idEntidad) {
		
		ConnectionJndi connectionJndi = null;
		
		Connection conn = null;
		CallableStatement cstmt = null;
		ResultSet resultSets = null;
		String entidadDenominacion = "";
		try {

			connectionJndi = ConexionUtil.getConnectionJndiExpediente();
			
			conn = connectionJndi.getConnection();
			
			cstmt = conn.prepareCall("{ CALL PKG_APELADO_CONSULTA.SP_OBTENER_ENT_DENOM_APELADO(?,?)}");
			cstmt.setInt("IN_ID_ENTIDAD", (Integer) idEntidad);
			cstmt.registerOutParameter("P_C_CURSOR", OracleTypes.CURSOR);
			cstmt.execute();
			resultSets = (ResultSet) cstmt.getObject("P_C_CURSOR");
			
			while(resultSets.next()){
				entidadDenominacion = (String)resultSets.getString("ENTIDAD");
			}	
			
		} catch (NumberFormatException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		} catch (SQLException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		}finally {
			ConexionUtil.closeSqlConnections(resultSets, cstmt, conn);
			ConexionUtil.closeContext(connectionJndi);
		}
		
		return entidadDenominacion;

	}

	
	public OficioApertura getOficioApertura(Number idExpediente) {

		OficioApertura obj = null;
		
		ConnectionJndi connectionJndi = null;
		
		Connection conn = null;
		CallableStatement cstmt = null;
		ResultSet resultSets = null;

		try {

			connectionJndi = ConexionUtil.getConnectionJndiExpediente();
			
			conn = connectionJndi.getConnection();
			
			cstmt = conn.prepareCall("{ CALL PKG_APELADO_CONSULTA.SP_OBTEN_OFIC_APERTURA_APELADO(?,?)}");
			cstmt.setInt("IN_ID_EXPEDIENTE", (Integer) idExpediente);
			cstmt.registerOutParameter("P_C_CURSOR", OracleTypes.CURSOR);
			cstmt.execute();
			resultSets = (ResultSet) cstmt.getObject("P_C_CURSOR");
			
			while(resultSets.next()){
				obj = new OficioApertura();
				obj.setId_expediente((Number)resultSets.getInt("ID_EXPEDIENTE"));
				obj.setNumero_siglas((String)resultSets.getString("NUMERO_SIGLAS"));
				obj.setDescripcion((String)resultSets.getString("DESCRIPCION"));
				obj.setF_documento((String)resultSets.getString("F_DOCUMENTO"));
				obj.setF_ingreso_servir((String)resultSets.getString("F_INGRESO_SERVIR"));
				obj.setCod_tipo((String)resultSets.getString("COD_TIPO"));
				obj.setF_ingreso_tsc((String)resultSets.getString("F_INGRESO_TSC"));
				obj.setNumero_registro((String)resultSets.getString("NUMERO_REGISTRO"));
				obj.setNombre((String)resultSets.getString("NOMBRE"));
				obj.setPrimer_apellido((String)resultSets.getString("PRIMER_APELLIDO"));
				obj.setSegundo_apellido((String)resultSets.getString("SEGUNDO_APELLIDO"));
				//obj.setCargo((String)resultSets.getString("ID_CARGO"));
				obj.setId_cargo((Integer)resultSets.getInt("ID_CARGO"));
				obj.setDni((String)resultSets.getString("DNI"));
				obj.setId_persona(resultSets.getInt("ID_PERSONA"));
				obj.setRuc_nom_entidad((String)resultSets.getString("RUC_NOM_ENTIDAD"));
				obj.setF_creacion_exp(resultSets.getString("F_CREACION_EXPEDIENTE"));
				
			}	
			
		} catch (NumberFormatException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		} catch (SQLException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		} finally {
			ConexionUtil.closeSqlConnections(resultSets, cstmt, conn);
			ConexionUtil.closeContext(connectionJndi);  
		}
		
		return obj;
	}

	public LinkedList<TipoDocumento> getTipoDocumentos() {

		LinkedList<TipoDocumento> list = new LinkedList<TipoDocumento>();

		ConnectionJndi connectionJndi = null;
		
		Connection conn = null;
		CallableStatement cstmt = null;
		ResultSet resultSets = null;

		try {

			connectionJndi = ConexionUtil.getConnectionJndiExpediente();
			
			conn = connectionJndi.getConnection();
			
			cstmt = conn.prepareCall("{ CALL PKG_APELADO_CONSULTA.SP_OBTEN_TODOS_TIPO_DOCUMENTO(?)}");
			cstmt.registerOutParameter("P_C_CURSOR", OracleTypes.CURSOR);
			cstmt.execute();
			resultSets = (ResultSet) cstmt.getObject("P_C_CURSOR");
			
			while(resultSets.next()){
				TipoDocumento obj = new TipoDocumento();
				obj.setCod_tipo((String)resultSets.getString("COD_TIPO"));
				obj.setDescripcion((String)resultSets.getString("DESCRIPCION"));
				list.add(obj);
			}	
		} catch (NumberFormatException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		} catch (SQLException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		}finally {
			ConexionUtil.closeSqlConnections(resultSets, cstmt, conn);
			ConexionUtil.closeContext(connectionJndi);
		}
		
		return list;
	}
	
	public LinkedList<TipoOficio> getTipoOficios(Number id_expediente) {

		LinkedList<TipoOficio> list = new LinkedList<TipoOficio>();
		
		ConnectionJndi connectionJndi = null;
		
		Connection conn = null;
		CallableStatement cstmt = null;
		ResultSet resultSets = null;

		try {

			connectionJndi = ConexionUtil.getConnectionJndiExpediente();
			
			conn = connectionJndi.getConnection();
			
			cstmt = conn.prepareCall("{ CALL PKG_APELADO_CONSULTA.SP_OBTEN_TODOS_TIPO_OFICIO(?, ?)}");
			
			if (id_expediente != null) {
				
				cstmt.setInt("IN_ID_EXPEDIENTE", id_expediente.intValue());
					
			} else {
				
				cstmt.setString("IN_ID_EXPEDIENTE", null);
			}
			
			cstmt.registerOutParameter("P_C_CURSOR", OracleTypes.CURSOR);
			
			cstmt.execute();
			
			resultSets = (ResultSet) cstmt.getObject("P_C_CURSOR");
			
			while(resultSets.next()){
				
				TipoOficio obj = new TipoOficio();
				
				obj.setCod_tipo((String)resultSets.getString("COD_TIPO"));
				obj.setDescripcion((String)resultSets.getString("DESCRIPCION"));
				list.add(obj);
			}	
		} catch (NumberFormatException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		} catch (SQLException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		}finally {
			ConexionUtil.closeSqlConnections(resultSets, cstmt, conn);
			ConexionUtil.closeContext(connectionJndi);
		}
		
		return list;
	}
	
	public LinkedList<TipoOficio> getTipoDocumentos(Number id_expediente) {

		LinkedList<TipoOficio> list = new LinkedList<TipoOficio>();
		
		ConnectionJndi connectionJndi = null;
		
		Connection conn = null;
		CallableStatement cstmt = null;
		ResultSet resultSets = null;

		try {

			connectionJndi = ConexionUtil.getConnectionJndiExpediente();
			
			conn = connectionJndi.getConnection();
			
			cstmt = conn.prepareCall("{ CALL PKG_EXPEDIENTE_DOCUMENTOS.SP_OBTENER_TIPO_OFICIO(?, ?)}");
			
			if (id_expediente != null) {
				
				cstmt.setInt("IN_ID_EXPEDIENTE", id_expediente.intValue());
					
			} else {
				
				cstmt.setString("IN_ID_EXPEDIENTE", null);
			}
			
			cstmt.registerOutParameter("P_C_CURSOR", OracleTypes.CURSOR);
			
			cstmt.execute();
			
			resultSets = (ResultSet) cstmt.getObject("P_C_CURSOR");
			
			while(resultSets.next()){
				
				TipoOficio obj = new TipoOficio();
				
				obj.setCod_tipo((String)resultSets.getString("COD_TIPO"));
				obj.setDescripcion((String)resultSets.getString("DESCRIPCION"));
				list.add(obj);
			}	
		} catch (NumberFormatException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		} catch (SQLException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		}finally {
			ConexionUtil.closeSqlConnections(resultSets, cstmt, conn);
			ConexionUtil.closeContext(connectionJndi);
		}
		
		return list;
	}
	
	public LinkedList<TipoOficio> getTipoRespuesta() {

		LinkedList<TipoOficio> list = new LinkedList<TipoOficio>();

		ConnectionJndi connectionJndi = null;
		
		Connection conn = null;
		CallableStatement cstmt = null;
		ResultSet resultSets = null;

		try {

			connectionJndi = ConexionUtil.getConnectionJndiExpediente();
			
			conn = connectionJndi.getConnection();
			
			cstmt = conn.prepareCall("{ CALL PKG_APELADO_CONSULTA.SP_OBTEN_TODOS_TIPO_RESPUESTA(?)}");
			cstmt.registerOutParameter("P_C_CURSOR", OracleTypes.CURSOR);
			cstmt.execute();
			resultSets = (ResultSet) cstmt.getObject("P_C_CURSOR");
			
			while(resultSets.next()){
				TipoOficio obj = new TipoOficio();
				obj.setCod_tipo((String)resultSets.getString("COD_TIPO"));
				obj.setDescripcion((String)resultSets.getString("DESCRIPCION"));
				list.add(obj);
			}	
		} catch (NumberFormatException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		} catch (SQLException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		}finally {
			ConexionUtil.closeSqlConnections(resultSets, cstmt, conn);
			ConexionUtil.closeContext(connectionJndi);
		}
		
		return list;
	}

	public boolean saveApelado(Apelado apelado_grabar, Apelado apelado_actual, Usuario usuario) throws SQLException {	
		
		boolean res=false;
		
		//if (apelado_grabar.getEntidad().getId_entidad().intValue() == 0) {
		if (apelado_grabar.getEntidad() == null) {
			//apelado_grabar.setId_entidad(this.insertEntidad(apelado_grabar)); <-- Se encontraba comentado desde un inicio
		} else {
			if (NumeroUtil.parsearAInteger(apelado_grabar.getEntidad().getId_entidad()) == 0) {
				
			}
			else{
				res = this.updateEntidad(apelado_grabar, usuario);
			}
		}
		
		
		if (apelado_actual != null) {
	
			if (apelado_actual.getOficioApertura() == null) {
				res = this.insertOficioApertuda2(apelado_grabar.getOficioApertura(), usuario);
			} else {
				res = this.updateOficioApertuda(apelado_grabar.getOficioApertura(), usuario);
			}
			
			if (apelado_grabar.getOficioApertura().getId_entidad() != null) {
				
				this.actualizarRepresentanteApelado(apelado_grabar.getOficioApertura(), usuario);			//SICE V4.1.0.0 (Integración SGE - SICE)
			}
			
			if (apelado_actual.getEntidad() == null) {
				
				if (	apelado_grabar.getEntidad() != null
					&&	apelado_grabar.getEntidad().getId_entidad() != null) {
					
					res = this.insertApelado(apelado_grabar, usuario);	
				}
			} else {
				res = this.updateApelado(apelado_grabar, usuario);
			}
		} else {
			res = this.insertOficioApertuda(apelado_grabar.getOficioApertura(), usuario);
			res = this.insertApelado(apelado_grabar, usuario);
		}
		
		this.updateOficiosTSCNuevos(apelado_grabar.getOficioApertura(), usuario);
		
		return res;
	}
	
	/** GALAXY - ESTE METODO NO SE USA **/
	public boolean saveApeladoCargo(Apelado apelado, Usuario usuario) throws SQLException{	
		
		boolean resultEntidad = false;
		boolean resultOfiApertura = false;
		boolean resultOfiTSCNuevos = false;
		boolean swSaveApeCargo = false;
		
		resultEntidad = this.updateEntidadCargo(apelado, usuario);
		
		if(resultEntidad){
			
			resultOfiApertura = this.updateOficioApertudaCargo(apelado, usuario);
			
			if(resultOfiApertura){
				
				resultOfiTSCNuevos = this.updateOficiosTSCNuevos(apelado.getOficioApertura(), usuario);
				
				if(resultOfiTSCNuevos){
					swSaveApeCargo = true;
				}else{
					swSaveApeCargo = false;
				}
			}else{
				swSaveApeCargo = false;
			}
		}else{
			swSaveApeCargo = false;
		}
		
		return swSaveApeCargo;

	}
	
	public boolean updateEntidadCargo(Apelado obj, Usuario usuario) throws SQLException {
		
		ConnectionJndi connectionJndi = null;
		
		Connection conn = null;
		CallableStatement cstmt = null;
		boolean sw = false;
		
		try {

			connectionJndi = ConexionUtil.getConnectionJndiExpediente();
			
			conn = connectionJndi.getConnection();
			
			conn.setAutoCommit(false);
			
			cstmt = conn.prepareCall("{ Call PKG_APELADO_CONSULTA.SP_ACTUALIZAR_ENTIDAD_CARGO ( ?,?,?,?,? ) }");									
			cstmt.setInt("IN_ID_CARGO", (Integer)obj.getEntidad().getId_cargo());
			//cstmt.setString(2, obj.getOficioApertura().getPrimer_apellido());
			//cstmt.setString(3, obj.getOficioApertura().getSegundo_apellido());
			//cstmt.setString(4, obj.getOficioApertura().getNombre());
			cstmt.setString("IN_AD_ID_SESSION", usuario.getAd_id_session());		
			cstmt.setInt("IN_AD_ID_USUARIO", Integer.parseInt(usuario.getAd_id_usuario().toString()));	
			cstmt.setInt("IN_ID_ENTIDAD", Integer.parseInt(obj.getEntidad().getId_entidad().toString()));
			cstmt.registerOutParameter("IN_RETORNO", OracleTypes.VARCHAR);																												
			cstmt.execute();
			
			Object object = cstmt.getObject("IN_RETORNO");
			
			if (object!=null) {
				Integer ret = Integer.valueOf(object.toString());
				if ((ret > 0)) {
					sw = true;
					conn.commit();
				}else{
					conn.rollback();
					sw = false;
				}							
				
			}else{
				conn.rollback();
				sw = false;
			}	
									
		} catch (SQLException e) {
			conn.rollback();
			sw = false;
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		}
		finally{
			ConexionUtil.closeSqlConnections(cstmt, conn);
			ConexionUtil.closeContext(connectionJndi);
		}
		return sw;
	}
	
	public boolean updateOficioApertudaCargo(Apelado obj, Usuario usuario) throws SQLException {
		
		ConnectionJndi connectionJndi = null;
		
		Connection conn = null;
		CallableStatement cstmt = null;
		boolean sw = false;
		
		try {

			connectionJndi = ConexionUtil.getConnectionJndiExpediente();
			
			conn = connectionJndi.getConnection();
			
			conn.setAutoCommit(false);
			cstmt = conn.prepareCall("{ Call PKG_APELADO_CONSULTA.SP_ACTUALZR_OFIC_APERTUR_CARGO ( ?,?,?,?,? ) }");									
			/*cstmt.setString(1, obj.getOficioApertura().getNombre());
			cstmt.setString(2, obj.getOficioApertura().getPrimer_apellido());
			cstmt.setString(3, obj.getOficioApertura().getSegundo_apellido());*/
			cstmt.setInt("IN_ID_CARGO", (Integer)obj.getOficioApertura().getId_cargo());
			cstmt.setString("IN_AD_ID_SESSION", usuario.getAd_id_session());		
			cstmt.setInt("IN_AD_ID_USUARIO", Integer.parseInt(usuario.getAd_id_usuario().toString()));	
			cstmt.setInt("IN_ID_EXPEDIENTE", Integer.parseInt(obj.getId_expediente().toString()));
			cstmt.registerOutParameter("IN_RETORNO", OracleTypes.VARCHAR);																												
			cstmt.execute();
			
			Object object = cstmt.getObject("IN_RETORNO");
			
			if (object!=null) {
				Integer ret = Integer.valueOf(object.toString());
				if ((ret > 0)) {
					sw = true;
					conn.commit();
				}else{
					conn.rollback();
					sw = false;
				}							
				
			}else{
				conn.rollback();
				sw = false;
			}	
									
		} catch (SQLException e) {
			conn.rollback();
			sw = false;
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		}
		finally{
			ConexionUtil.closeSqlConnections(cstmt, conn);
			ConexionUtil.closeContext(connectionJndi);
		}
		
		return sw;
	}
	
	public boolean updateEntidad(Apelado obj, Usuario usuario) throws SQLException {
		
		ConnectionJndi connectionJndi = null;
		
		Connection conn = null;
		CallableStatement cstmt = null;
		boolean sw = false;
		Number num=0;
		try {

			connectionJndi = ConexionUtil.getConnectionJndiExpediente();
			
			conn = connectionJndi.getConnection();
			
			conn.setAutoCommit(false);
			
			cstmt = conn.prepareCall("{ Call PKG_APELADO_CONSULTA.SP_ACTUALIZAR_ENTIDAD ( ?,?,?,?,?,?,?,?,?,? ) }");									
			
			cstmt.setInt("IN_ID_CARGO", (Integer)obj.getOficioApertura().getId_cargo());
			cstmt.setString("IN_CODPRO", obj.getEntidad().getCodpro());
			cstmt.setString("IN_CODDIS", obj.getEntidad().getCoddis());
			cstmt.setString("IN_CODDEP", obj.getEntidad().getCoddep());
			cstmt.setString("IN_AD_ID_SESSION", usuario.getAd_id_session());		
			cstmt.setInt("IN_AD_ID_USUARIO", NumeroUtil.parsearAInteger(usuario.getAd_id_usuario()));	
			cstmt.setInt("IN_ID_ENTIDAD", NumeroUtil.parsearAInteger(obj.getEntidad().getId_entidad()));
			if(obj.getEntidad().getId_sector()==num || obj.getEntidad().getId_sector()==null){
				cstmt.setString("IN_ID_SECTOR", null);
			}else{
				cstmt.setInt("IN_ID_SECTOR", NumeroUtil.parsearAInteger(obj.getEntidad().getId_sector()));
			}
			
			cstmt.setString("IN_DIRECCION", obj.getEntidad().getDireccion());
			
			cstmt.registerOutParameter("IN_RETORNO", OracleTypes.VARCHAR);																												
			
			cstmt.execute();
			
			Object object = cstmt.getObject("IN_RETORNO");
			
			if (object!=null) {
				Integer ret = Integer.valueOf(object.toString());
				if ((ret > 0)) {
					sw = true;
					conn.commit();
				}else{
					conn.rollback();
					sw = false;
				}							
				
			}else{
				conn.rollback();
				sw = false;
			}	
									
		} catch (SQLException e) {
			conn.rollback();
			sw = false;
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		}
		finally{
			ConexionUtil.closeSqlConnections(cstmt, conn);
			ConexionUtil.closeContext(connectionJndi);
		}
		return sw;
	}

	public boolean insertOficioApertuda(OficioApertura obj, Usuario usuario) throws SQLException {
		
		ConnectionJndi connectionJndi = null;
		
		Connection conn = null;
		CallableStatement cstmt = null;
		boolean sw = false;
		Number num=0;
		
		try {

			connectionJndi = ConexionUtil.getConnectionJndiExpediente();
			
			conn = connectionJndi.getConnection();
			
			conn.setAutoCommit(false);
			
			cstmt = conn.prepareCall("{ Call PKG_APELADO_CONSULTA.SP_INSERTAR_OFICIO_APERTURA ( ?,?,?,?,?,?,?,?,?,?,?,?,?,? ) }");									
			
			cstmt.setInt("IN_ID_EXPEDIENTE", obj.getId_expediente().intValue());
			cstmt.setString("IN_NUMERO_SIGLAS", obj.getNumero_siglas());
			cstmt.setString("IN_DESCRIPCION", null);
			cstmt.setString("IN_F_DOCUMENTO", obj.getF_documento());
			cstmt.setString("IN_F_INGRESO_SERVIR", obj.getF_ingreso_servir()); //
			cstmt.setString("IN_COD_TIPO", obj.getCod_tipo());
			cstmt.setString("IN_F_INGRESO_TSC", obj.getF_ingreso_tsc()); //
			cstmt.setString("IN_NUMERO_REGISTRO", obj.getNumero_registro());
			
			cstmt.setString("IN_RUC_NOM_ENTIDAD", obj.getRuc_nom_entidad());
			
			if (	obj.getId_cargo() != null 
				&& 	obj.getId_cargo() != num) {
				
				cstmt.setInt("IN_ID_CARGO", (Integer)obj.getId_cargo());
			
			} else {
				cstmt.setString("IN_ID_CARGO", null);
			}
			
			cstmt.setString("IN_AD_ID_SESSION", usuario.getAd_id_session());		
			cstmt.setInt("IN_AD_ID_USUARIO", Integer.parseInt(usuario.getAd_id_usuario().toString()));	
			
			
			if (	obj.getId_persona() != null 
				&& 	obj.getId_persona() != num) {
				
				cstmt.setInt("IN_ID_PERSONA",(Integer)obj.getId_persona());
			
			} else {
				cstmt.setString("IN_ID_PERSONA",null);
			}
			
			cstmt.registerOutParameter("IN_RETORNO", OracleTypes.VARCHAR);																												
			
			cstmt.execute();
			
			Object object = cstmt.getObject("IN_RETORNO");
			
			if (object!=null) {
				
				Integer ret = Integer.valueOf(object.toString());
				
				if ((ret > 0)) {
					sw = true;
					conn.commit();
				} else {
					conn.rollback();
					sw = false;
				}
			} else {
				conn.rollback();
				sw = false;
			}					
		} catch (SQLException e) {
			conn.rollback();
			sw = false;
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		} finally {
			ConexionUtil.closeSqlConnections(cstmt, conn);
			ConexionUtil.closeContext(connectionJndi);
		}
		
		return sw;
	}
	
	public boolean insertOficioApertuda2(OficioApertura obj, Usuario usuario) throws SQLException {
		
		ConnectionJndi connectionJndi = null;
			
		Connection conn = null;
		CallableStatement cstmt = null;
		boolean sw = false;
		
		try {

			connectionJndi = ConexionUtil.getConnectionJndiExpediente();
			
			conn = connectionJndi.getConnection();
			
			conn.setAutoCommit(false);
			cstmt = conn.prepareCall("{ Call PKG_APELADO_CONSULTA.SP_INSERTAR_OFICIO_APERTURA2 ( ?,?,?,?,?,?,?,?,?,?,?,?,?,? ) }");									
			cstmt.setInt("IN_ID_EXPEDIENTE", obj.getId_expediente().intValue());
			cstmt.setString("IN_NUMERO_SIGLAS", obj.getNumero_siglas());
			cstmt.setString("IN_DESCRIPCION", null);
			cstmt.setString("IN_F_DOCUMENTO", obj.getF_documento());
			cstmt.setString("IN_F_CREACION_EXPEDIENTE", obj.getF_creacion_exp());
			cstmt.setString("IN_COD_TIPO", obj.getCod_tipo());
			cstmt.setString("IN_F_INGRESO_TSC", obj.getF_ingreso_tsc());
			cstmt.setString("IN_NUMERO_REGISTRO", obj.getNumero_registro());
			cstmt.setInt("IN_ID_ENTIDAD", (Integer) obj.getId_entidad());
			cstmt.setInt("IN_ID_PERSONA", (Integer) obj.getId_persona());
			/*cstmt.setString(9, obj.getNombre());
			cstmt.setString(10, obj.getPrimer_apellido());
			cstmt.setString(11, obj.getSegundo_apellido());*/
			cstmt.setInt("IN_ID_CARGO", (Integer)obj.getId_cargo());
			cstmt.setString("IN_AD_ID_SESSION", usuario.getAd_id_session());		
			cstmt.setInt("IN_AD_ID_USUARIO", Integer.parseInt(usuario.getAd_id_usuario().toString()));	
			cstmt.registerOutParameter("IN_RETORNO", OracleTypes.VARCHAR);																												
			cstmt.execute();
			
			Object object = cstmt.getObject("IN_RETORNO");
			
			if (object!=null) {
				Integer ret = Integer.valueOf(object.toString());
				if ((ret > 0)) {
					sw = true;
					conn.commit();
				}else{
					conn.rollback();
					sw = false;
				}							
				
			}else{
				conn.rollback();
				sw = false;
			}	
									
		} catch (SQLException e) {
			conn.rollback();
			sw = false;
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		}
		finally{
			ConexionUtil.closeSqlConnections(cstmt, conn);
			ConexionUtil.closeContext(connectionJndi);
		}
		
		return sw;
	}
	
	
	public boolean updateOficiosTSCNuevos(OficioApertura obj, Usuario usuario) throws SQLException {
		
		ConnectionJndi connectionJndi = null;
		
		Connection conn = null;
		CallableStatement cstmt = null;
		boolean sw = false;
		
		try {

			connectionJndi = ConexionUtil.getConnectionJndiExpediente();
			
			conn = connectionJndi.getConnection();
			
			conn.setAutoCommit(false);
			cstmt = conn.prepareCall("{ Call PKG_APELADO_CONSULTA.SP_ACTUALIZAR_OFICIO_TSC_NUEVO ( ?,?,?,? ) }");									
			cstmt.setInt("IN_ID_EXPEDIENTE", Integer.parseInt(obj.getId_expediente().toString()));
			cstmt.setInt("IN_ID_CARGO",(Integer) obj.getId_cargo());
			/*cstmt.setString(3, obj.getNombre());
			cstmt.setString(4, obj.getPrimer_apellido());
			cstmt.setString(5, obj.getSegundo_apellido());*/
			cstmt.setString("IN_AD_ID_SESSION", usuario.getAd_id_session());		
			cstmt.registerOutParameter("IN_RETORNO", OracleTypes.VARCHAR);																												
			cstmt.execute();
			
			Object object = cstmt.getObject("IN_RETORNO");
			
			if (object!=null) {
				Integer ret = Integer.valueOf(object.toString());
				if ((ret > 0)) {
					sw = true;
					conn.commit();
				}else{
					conn.rollback();
					sw = false;
				}							
				
			}else{
				conn.rollback();
				sw = false;
			}	
									
		} catch (SQLException e) {
			conn.rollback();
			sw = false;
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		}
		finally{
			ConexionUtil.closeSqlConnections(cstmt, conn);
			ConexionUtil.closeContext(connectionJndi);
		}
		return sw;
	}
	
	public boolean updateOficioApertuda(OficioApertura obj, Usuario usuario) throws SQLException {
		
		ConnectionJndi connectionJndi = null;
		
		Connection conn = null;
		CallableStatement cstmt = null;
		boolean sw = false;
		
		try {

			connectionJndi = ConexionUtil.getConnectionJndiExpediente();
			
			conn = connectionJndi.getConnection();
			
			conn.setAutoCommit(false);
			cstmt = conn.prepareCall("{ Call PKG_APELADO_CONSULTA.SP_ACTUALIZAR_OFICIO_APERTURA2 ( ?,?,?,?,?,?,?,?,?,?,?,?,?,? ) }");									
			cstmt.setString("IN_NUMERO_SIGLAS", obj.getNumero_siglas());
			cstmt.setString("IN_DESCRIPCION", obj.getDescripcion());
			cstmt.setString("IN_F_DOCUMENTO", obj.getF_documento());
			cstmt.setString("IN_F_CREACION_EXPEDIENTE", obj.getF_creacion_exp());
			cstmt.setString("IN_COD_TIPO", obj.getCod_tipo());
			cstmt.setString("IN_F_INGRESO_TSC", obj.getF_ingreso_tsc());
			cstmt.setString("IN_NUMERO_REGISTRO", obj.getNumero_registro());	
			/*cstmt.setString(8, obj.getNombre());
			cstmt.setString(9, obj.getPrimer_apellido());
			cstmt.setString(10, obj.getSegundo_apellido());*/
			//cstmt.setString(11, obj.getCargo());
			if(NumeroUtil.parsearAInteger(obj.getId_entidad())!=0){
				cstmt.setInt("IN_ID_ENTIDAD", NumeroUtil.parsearAInteger(obj.getId_entidad()));
			}else{
				cstmt.setString("IN_ID_ENTIDAD",null);
			}
			
			if(NumeroUtil.parsearAInteger(obj.getId_persona())!=0){
				cstmt.setInt("IN_ID_PERSONA", NumeroUtil.parsearAInteger(obj.getId_persona()));
			}else{
				cstmt.setString("IN_ID_PERSONA",null);
			}
			
			if(NumeroUtil.parsearAInteger(obj.getId_cargo())!=0){
				cstmt.setInt("IN_ID_CARGO", NumeroUtil.parsearAInteger(obj.getId_cargo()));
			}else{
				cstmt.setString("IN_ID_CARGO",null);
			}
			cstmt.setString("IN_AD_ID_SESSION", usuario.getAd_id_session());
			cstmt.setInt("IN_AD_ID_USUARIO", (Integer)usuario.getAd_id_usuario());	
			cstmt.setInt("IN_ID_EXPEDIENTE", (Integer)obj.getId_expediente());
			//cstmt.setString(13, obj.getDni());
			cstmt.registerOutParameter("IN_RETORNO", OracleTypes.VARCHAR);																												
			cstmt.execute();
			
			Object object = cstmt.getObject("IN_RETORNO");
			
			if (object!=null) {
				Integer ret = Integer.valueOf(object.toString());
				if ((ret > 0)) {
					sw = true;
					conn.commit();
				}else{
					conn.rollback();
					sw = false;
				}							
				
			}else{
				conn.rollback();
				sw = false;
			}	
									
		} catch (SQLException e) {
			conn.rollback();
			sw = false;
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		}
		finally{
			ConexionUtil.closeSqlConnections(cstmt, conn);
			ConexionUtil.closeContext(connectionJndi);
		}
		return sw;
	}
	
	public void saveApeladoEditado(Apelado apelado, Usuario usuario) throws SQLException {
		
		if (apelado == null)
		{
			this.insertApelado(apelado, usuario);
		}
		else
		{
			this.updateApelado(apelado, usuario);
		}
	}

	
	public boolean insertApelado(Apelado apelado, Usuario usuario) throws SQLException {
		
		ConnectionJndi connectionJndi = null;
		
		Connection conn = null;
		CallableStatement cstmt = null;
		boolean sw = false;
		
		try {

			connectionJndi = ConexionUtil.getConnectionJndiExpediente();
			
			conn = connectionJndi.getConnection();
			
			conn.setAutoCommit(false);
			cstmt = conn.prepareCall("{ Call PKG_APELADO_CONSULTA.SP_INSERTAR_APELADO ( ?,?,?,?,?,? ) }");									
			cstmt.setInt("IN_ID_EXPEDIENTE", Integer.parseInt(apelado.getId_expediente().toString()));
			cstmt.setInt("IN_ID_ENTIDAD", Integer.parseInt(apelado.getEntidad().getId_entidad().toString()));
			cstmt.setString("IN_DIRECCION", apelado.getEntidad().getDireccion());
			cstmt.setString("IN_AD_ID_SESSION", usuario.getAd_id_session());
			cstmt.setInt("IN_AD_ID_USUARIO", Integer.parseInt(usuario.getAd_id_usuario().toString()));	
			cstmt.registerOutParameter("IN_RETORNO", OracleTypes.VARCHAR);																												
			cstmt.execute();
			
			Object object = cstmt.getObject("IN_RETORNO");
			
			if (object!=null) {
				Integer ret = Integer.valueOf(object.toString());
				if ((ret > 0)) {
					sw = true;
					conn.commit();
				}else{
					conn.rollback();
					sw = false;
				}							
				
			}else{
				conn.rollback();
				sw = false;
			}	
									
		} catch (SQLException e) {
			conn.rollback();
			sw = false;
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		}
		finally{
			ConexionUtil.closeSqlConnections(cstmt, conn);
			ConexionUtil.closeContext(connectionJndi);
		}
		return sw;
	}
	
	public boolean updateApelado(Apelado apelado, Usuario usuario) throws SQLException {
		
		ConnectionJndi connectionJndi = null;
		
		Connection conn = null;
		CallableStatement cstmt = null;
		boolean sw = false;
		
		try {

			connectionJndi = ConexionUtil.getConnectionJndiExpediente();
			
			conn = connectionJndi.getConnection();
			
			conn.setAutoCommit(false);
			cstmt = conn.prepareCall("{ Call PKG_APELADO_CONSULTA.SP_ACTUALIZAR_APELADO ( ?,?,?,?,?,? ) }");									
			cstmt.setInt("IN_ID_ENTIDAD", (Integer)apelado.getEntidad().getId_entidad());
			cstmt.setString("IN_DIRECCION", apelado.getEntidad().getDireccion());
			cstmt.setString("IN_AD_ID_SESSION", usuario.getAd_id_session());
			cstmt.setInt("IN_AD_ID_USUARIO", Integer.parseInt(usuario.getAd_id_usuario().toString()));	
			cstmt.setInt("IN_ID_EXPEDIENTE", Integer.parseInt(apelado.getId_expediente().toString()));
			cstmt.registerOutParameter("IN_RETORNO", OracleTypes.VARCHAR);																												
			cstmt.execute();
			
			Object object = cstmt.getObject("IN_RETORNO");
			
			if (object!=null) {
				Integer ret = Integer.valueOf(object.toString());
				if ((ret > 0)) {
					sw = true;
					conn.commit();
				}else{
					conn.rollback();
					sw = false;
				}							
				
			}else{
				conn.rollback();
				sw = false;
			}	
									
		} catch (SQLException e) {
			conn.rollback();
			sw = false;
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		}
		finally{
			ConexionUtil.closeSqlConnections(cstmt, conn);
			ConexionUtil.closeContext(connectionJndi);
		}
		return sw;
	}

	public LinkedList<Entidad> getEntidadesBuqueda(String direccion) {
		
		LinkedList<Entidad> list = new LinkedList<Entidad>();
			
		ConnectionJndi connectionJndi = null;
	
		Connection conn = null;
		CallableStatement cstmt = null;
		ResultSet resultSets = null;

		try {

			connectionJndi = ConexionUtil.getConnectionJndiExpediente();
			
			conn = connectionJndi.getConnection();
			
			cstmt = conn.prepareCall("{ CALL PKG_APELADO_CONSULTA.SP_OBTENER_BUSQUEDA_ENTIDAD(?,?)}");
			cstmt.setString("IN_ENTIDAD", direccion.trim());
			cstmt.registerOutParameter("P_C_CURSOR", OracleTypes.CURSOR);
			cstmt.execute();
			resultSets = (ResultSet) cstmt.getObject("P_C_CURSOR");
			
			while(resultSets.next()){
				Entidad obj = new Entidad();
				obj.setId_entidad((Number)resultSets.getInt("ID_ENTIDAD"));
				obj.setEntidad((String)resultSets.getString("ENTIDAD"));
				list.add(obj);
			}	
		} catch (NumberFormatException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		} catch (SQLException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		}finally {
			ConexionUtil.closeSqlConnections(resultSets, cstmt, conn);
			ConexionUtil.closeContext(connectionJndi);
		}
		
		return list;
	}
	
	public LinkedList<Entidad> getEntidadesBuquedaAdm(String direccion) {
		
		LinkedList<Entidad> list = new LinkedList<Entidad>();
		
		ConnectionJndi connectionJndi = null;
		
		Connection conn = null;
		CallableStatement cstmt = null;
		ResultSet resultSets = null;

		try {

			connectionJndi = ConexionUtil.getConnectionJndiExpediente();
			
			conn = connectionJndi.getConnection();
			
			cstmt = conn.prepareCall("{ CALL PKG_APELADO_CONSULTA.SP_OBTENER_BUSQ_ADM_ENTIDAD(?,?)}");
			cstmt.setString("IN_ENTIDAD", direccion.trim());
			cstmt.registerOutParameter("P_C_CURSOR", OracleTypes.CURSOR);
			cstmt.execute();
			resultSets = (ResultSet) cstmt.getObject("P_C_CURSOR");
			
			while(resultSets.next()){
				Entidad obj = new Entidad();
				obj.setId_entidad((Number)resultSets.getInt("ID_ENTIDAD"));
				obj.setEntidad((String)resultSets.getString("ENTIDAD"));
				list.add(obj);
			}	
		} catch (NumberFormatException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		} catch (SQLException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		}finally {
			ConexionUtil.closeSqlConnections(resultSets, cstmt, conn);
			ConexionUtil.closeContext(connectionJndi); 
		}
		
		return list;
	}

	public Integer getVerNumeroRegistro(OficioApertura obj) {
		
		Integer resultado = 0;
		
		ConnectionJndi connectionJndi = null;
		
		Connection conn = null;
		CallableStatement cstmt = null;

		try {

			connectionJndi = ConexionUtil.getConnectionJndiExpediente();
			
			conn = connectionJndi.getConnection();
			
			cstmt = conn.prepareCall("{ CALL PKG_APELADO_CONSULTA.SP_VER_NUM_REGISTRO_OF_APERT(?,?,?)}");
			
			cstmt.setInt("IN_ID_EXPEDIENTE", (Integer) obj.getId_expediente());
			cstmt.setString("IN_NUMERO_REGISTRO", obj.getNumero_registro().trim());
			
			cstmt.registerOutParameter("P_RETORNO", OracleTypes.INTEGER);
			
			cstmt.execute();
			
			Integer retorno = cstmt.getInt("P_RETORNO");
			
			if(retorno > 0){
				resultado = retorno;
			}
			
		} catch (NumberFormatException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		} catch (SQLException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		} finally {
			ConexionUtil.closeSqlConnections(cstmt, conn);
			ConexionUtil.closeContext(connectionJndi);
		}
		
		return resultado ;
	}

	public LinkedList<Generica> getSectors() {
		
		LinkedList<Generica> list = new LinkedList<Generica>();
		
		ConnectionJndi connectionJndi = null;
		
		Connection conn = null;
		CallableStatement cstmt = null;
		ResultSet resultSets = null;

		try {

			connectionJndi = ConexionUtil.getConnectionJndiExpediente();
			
			conn = connectionJndi.getConnection();
			
			cstmt = conn.prepareCall("{ CALL PKG_APELADO_CONSULTA.SP_OBTENER_TODOS_SECTOR(?)}");
			cstmt.registerOutParameter("P_C_CURSOR", OracleTypes.CURSOR);
			cstmt.execute();
			resultSets = (ResultSet) cstmt.getObject("P_C_CURSOR");
			
			while(resultSets.next()){
				Generica obj = new Generica();
				obj.setId_tipo((Number)resultSets.getInt("ID_SECTOR"));
				obj.setDescripcion((String)resultSets.getString("DESCRIPCION"));
				list.add(obj);
			}	
		} catch (NumberFormatException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		} catch (SQLException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		}finally {
			ConexionUtil.closeSqlConnections(resultSets, cstmt, conn);
			ConexionUtil.closeContext(connectionJndi);
		}
		return list;
	}

	public LinkedList<Generica> ObtenerDepartamentos() {
		
		ConnectionJndi connectionJndi = null;
		
		Connection conn = null;
		CallableStatement cstmt = null;
		ResultSet resultSets = null;
		
		LinkedList<Generica> list = new LinkedList<Generica>();
		
		try {

			connectionJndi = ConexionUtil.getConnectionJndiExpediente();
			
			conn = connectionJndi.getConnection();
			
			cstmt = conn.prepareCall("{ CALL PKG_APELADO_CONSULTA.SP_OBTENER_DEPARTAMENTOS(?)}");
			cstmt.registerOutParameter("P_C_CURSOR", OracleTypes.CURSOR);
			cstmt.execute();
			resultSets = (ResultSet) cstmt.getObject("P_C_CURSOR");
			
			while(resultSets.next()){
				Generica obj = new Generica();
				obj.setCod_tipo((String)resultSets.getString("CODDEP"));
				obj.setDescripcion((String)resultSets.getString("DESCRIPCION"));
				list.add(obj);
			}	
		} catch (NumberFormatException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		} catch (SQLException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		}finally {
			ConexionUtil.closeSqlConnections(resultSets, cstmt, conn);
			ConexionUtil.closeContext(connectionJndi); 
		}
		return list;
	}

	public LinkedList<Generica> ObtenerDepartamentosReniec() {
		
		ConnectionJndi connectionJndi = null;
		
		Connection conn = null;
		CallableStatement cstmt = null;
		ResultSet resultSets = null;
		
		LinkedList<Generica> list = new LinkedList<Generica>();
		
		try {

			connectionJndi = ConexionUtil.getConnectionJndiExpediente();
			
			conn = connectionJndi.getConnection();
			
			cstmt = conn.prepareCall("{ CALL PKG_APELADO_CONSULTA.SP_OBTENER_DEPART_RENIEC(?)}");
			cstmt.registerOutParameter("P_C_CURSOR", OracleTypes.CURSOR);
			cstmt.execute();
			resultSets = (ResultSet) cstmt.getObject("P_C_CURSOR");
			
			while(resultSets.next()){
				Generica obj = new Generica();
				obj.setCod_tipo((String)resultSets.getString("CODDEP"));
				obj.setDescripcion((String)resultSets.getString("DESCRIPCION"));
				list.add(obj);
			}	
		} catch (NumberFormatException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		} catch (SQLException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		}finally {
			ConexionUtil.closeSqlConnections(resultSets, cstmt, conn);
			ConexionUtil.closeContext(connectionJndi);
		}
		
		return list;
	}

	public LinkedList<Generica> ObtenerProvincias(String coddep) {
		
		ConnectionJndi connectionJndi = null;
		
		Connection conn = null;
		CallableStatement cstmt = null;
		ResultSet resultSets = null;
		
		LinkedList<Generica> list = new LinkedList<Generica>();
		
		try {

			connectionJndi = ConexionUtil.getConnectionJndiExpediente();
			
			conn = connectionJndi.getConnection();
			
			cstmt = conn.prepareCall("{ CALL PKG_APELADO_CONSULTA.SP_OBTENER_PROVINCIAS(?,?)}");
			cstmt.setString("IN_CODDEP", coddep);
			cstmt.registerOutParameter("P_C_CURSOR", OracleTypes.CURSOR);
			cstmt.execute();
			resultSets = (ResultSet) cstmt.getObject("P_C_CURSOR");
			
			while(resultSets.next()){
				Generica obj = new Generica();
				obj.setCod_tipo((String)resultSets.getString("CODPRO"));
				obj.setDescripcion((String)resultSets.getString("DESCRIPCION"));
				list.add(obj);
			}	
		} catch (NumberFormatException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		} catch (SQLException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		}finally {
			ConexionUtil.closeSqlConnections(resultSets, cstmt, conn);
			ConexionUtil.closeContext(connectionJndi);
		}
		
		return list;
	}
	
	public LinkedList<Generica> ObtenerProvinciasReniec(String coddep) {
		
		ConnectionJndi connectionJndi = null;
		
		Connection conn = null;
		CallableStatement cstmt = null;
		ResultSet resultSets = null;
		
		LinkedList<Generica> list = new LinkedList<Generica>();
		
		try {

			connectionJndi = ConexionUtil.getConnectionJndiExpediente();
			
			conn = connectionJndi.getConnection();
			
			cstmt = conn.prepareCall("{ CALL PKG_APELADO_CONSULTA.SP_OBTENER_PROVINC_RENIEC(?,?)}");
			cstmt.setString("IN_CODDEP", coddep);
			cstmt.registerOutParameter("P_C_CURSOR", OracleTypes.CURSOR);
			cstmt.execute();
			resultSets = (ResultSet) cstmt.getObject("P_C_CURSOR");
			
			while(resultSets.next()){
				Generica obj = new Generica();
				obj.setCod_tipo((String)resultSets.getString("CODPRO"));
				obj.setDescripcion((String)resultSets.getString("DESCRIPCION"));
				list.add(obj);
			}	
		} catch (NumberFormatException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		} catch (SQLException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		}finally {
			ConexionUtil.closeSqlConnections(resultSets, cstmt, conn);
			ConexionUtil.closeContext(connectionJndi);
		}
		return list;
	}
	
	public LinkedList<Generica> ObtenerDistritos(String coddep, String codpro) {
		
		ConnectionJndi connectionJndi = null;
		
		Connection conn = null;
		CallableStatement cstmt = null;
		ResultSet resultSets = null;
		
		LinkedList<Generica> list = new LinkedList<Generica>();
		
		try {

			connectionJndi = ConexionUtil.getConnectionJndiExpediente();
			
			conn = connectionJndi.getConnection();
			
			cstmt = conn.prepareCall("{ CALL PKG_APELADO_CONSULTA.SP_OBTENER_DISTRITOS(?,?,?)}");
			cstmt.setString("IN_CODDEP", coddep);
			cstmt.setString("IN_CODPRO", codpro);
			cstmt.registerOutParameter("P_C_CURSOR", OracleTypes.CURSOR);
			cstmt.execute();
			resultSets = (ResultSet) cstmt.getObject("P_C_CURSOR");
			
			while(resultSets.next()){
				Generica obj = new Generica();
				obj.setCod_tipo((String)resultSets.getString("CODDIS"));
				obj.setDescripcion((String)resultSets.getString("DESCRIPCION"));
				list.add(obj);
			}	
		} catch (NumberFormatException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		} catch (SQLException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		}finally {
			ConexionUtil.closeSqlConnections(resultSets, cstmt, conn);
			ConexionUtil.closeContext(connectionJndi);
		}
		return list;
	}

	public LinkedList<Generica> ObtenerDistritosReniec(String coddep, String codpro) {
		
		ConnectionJndi connectionJndi = null;
		
		Connection conn = null;
		CallableStatement cstmt = null;
		ResultSet resultSets = null;
		
		LinkedList<Generica> list = new LinkedList<Generica>();
		
		try {
			
			connectionJndi = ConexionUtil.getConnectionJndiExpediente();
			
			conn = connectionJndi.getConnection();
			
			cstmt = conn.prepareCall("{ CALL PKG_APELADO_CONSULTA.SP_OBTENER_DISTRITOS_RENIEC(?,?,?)}");	
			cstmt.setString("IN_CODDEP", coddep);
			cstmt.setString("IN_CODPRO", codpro);
			cstmt.registerOutParameter("P_C_CURSOR", OracleTypes.CURSOR);
			cstmt.execute();
			resultSets = (ResultSet) cstmt.getObject("P_C_CURSOR");
			
			while(resultSets.next()){
				Generica obj = new Generica();
				obj.setCod_tipo((String)resultSets.getString("CODDIS"));
				obj.setDescripcion((String)resultSets.getString("DESCRIPCION"));
				list.add(obj);
			}	
		} catch (NumberFormatException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		} catch (SQLException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		}finally {
			ConexionUtil.closeSqlConnections(resultSets, cstmt, conn);
			ConexionUtil.closeContext(connectionJndi);
		}
		
		return list;
	}
	
	public boolean saveEntidad(Apelado apelado, Usuario usuario) throws SQLException {	
		
		boolean swSaveResult = false;
		
		if (	apelado != null
			&&	apelado.getEntidad() != null
			&&	apelado.getEntidad().getId_entidad() != null
			&&	apelado.getEntidad().getId_entidad().intValue() == 0) {
			
			apelado.setId_entidad(this.insertSoloEntidad(apelado, usuario));
			
			/*Agregando validacion para retornar true/false */
			if (	apelado.getId_entidad() != null
				&&	apelado.getId_entidad().intValue() > 0) {
				
				swSaveResult = true;
				
			} else {
				swSaveResult = false;
			}
			/* Fin validacion */
		} else {
			swSaveResult = this.updateSoloEntidad(apelado, usuario);
		}
		
		return swSaveResult;
	}
	
	public Number insertSoloEntidad(Apelado obj, Usuario usuario) {
		
		ConnectionJndi connectionJndi = null;
		
		Connection conn = null;
		CallableStatement cstmt = null;
		Number num =0;
		
		try {

			connectionJndi = ConexionUtil.getConnectionJndiExpediente();
			
			conn = connectionJndi.getConnection();
			conn.setAutoCommit(false);
			
			cstmt = conn.prepareCall("{ CALL PKG_APELADO_CONSULTA.SP_INSERTAR_SOLO_ENTIDAD(?,?,?,?,?,?,?,?,?,?,?,?,?)}");
			
			cstmt.registerOutParameter("IN_ID_ENTIDAD", OracleTypes.NUMBER);
			
			cstmt.setString("IN_DIRECCION", obj.getEntidad().getDireccion());
			cstmt.setString("IN_ID_ENTIDAD_PADRE", obj.getEntidad().getId_padre() == "0" ? null : obj.getEntidad().getId_padre());
			
			cstmt.setString("IN_CODPRO", obj.getEntidad().getCodpro());
			cstmt.setString("IN_CODDIS", obj.getEntidad().getCoddis());
			cstmt.setString("IN_CODDEP", obj.getEntidad().getCoddep());
			
			cstmt.setString("IN_ENTIDAD", obj.getEntidad().getEntidad());
			
			if (	obj.getEntidad().getId_sector() != null 
				&& 	obj.getEntidad().getId_sector() != num) {
				
				cstmt.setInt("IN_ID_SECTOR", (Integer) obj.getEntidad().getId_sector());
			
			} else {
				
				cstmt.setString("IN_ID_SECTOR",null);
			}
			
			cstmt.setString("IN_ESTADO", obj.getEntidad().getEstado());
			
			cstmt.setString("IN_AD_ID_SESSION", usuario.getAd_id_session());
			cstmt.setInt("IN_AD_ID_USUARIO", (Integer) usuario.getAd_id_usuario());
			
			cstmt.setString("IN_SUB_SEDE", obj.getEntidad().getSub_sede());
			
			cstmt.setString("IN_RUC", obj.getEntidad().getRuc());
			
			cstmt.execute();
			
			Number idValor = (Number) cstmt.getInt("IN_ID_ENTIDAD");
			
			if (idValor != null) {
				
				conn.commit();
				
				obj.getEntidad().setId_entidad(idValor.intValue());
				obj.setId_entidad(obj.getEntidad().getId_entidad());
			}
				
		} catch (NumberFormatException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		} catch (SQLException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		} finally {
			ConexionUtil.closeSqlConnections(cstmt, conn);
			ConexionUtil.closeContext(connectionJndi);
		}
		
		return obj.getId_entidad();
	}
	
	public boolean updateSoloEntidad(Apelado obj, Usuario usuario) throws SQLException {
		
		ConnectionJndi connectionJndi = null;
		
		Connection conn = null;
		CallableStatement cstmt = null;
		
		boolean sw = false;
		Number num=0;
		
		try {

			connectionJndi = ConexionUtil.getConnectionJndiExpediente();
			
			conn = connectionJndi.getConnection();
			
			conn.setAutoCommit(false);
			
			cstmt = conn.prepareCall("{ Call PKG_APELADO_CONSULTA.SP_ACTUALIZAR_SOLO_ENTIDAD ( ?,?,?,?,?,?,?,?,?,?,?,?,? ) }");									
			
			cstmt.setInt("IN_ID_ENTIDAD", obj.getEntidad().getId_entidad() != null ? obj.getEntidad().getId_entidad().intValue() : 0);
			
			cstmt.setString("IN_DIRECCION", obj.getEntidad().getDireccion());
			
			if (	obj.getEntidad().getId_padre() != null
				&&	obj.getEntidad().getId_padre().trim().equals("0")) {
				
				cstmt.setString("IN_ID_ENTIDAD_PADRE", null);
				
			} else {
				
				cstmt.setInt("IN_ID_ENTIDAD_PADRE", NumeroUtil.parsearAInteger(obj.getEntidad().getId_padre()));	
			}
			
			cstmt.setString("IN_CODPRO", obj.getEntidad().getCodpro());
			cstmt.setString("IN_CODDIS", obj.getEntidad().getCoddis());
			cstmt.setString("IN_CODDEP", obj.getEntidad().getCoddep());
			
			cstmt.setString("IN_ENTIDAD", obj.getEntidad().getEntidad());
			
			if (	obj.getEntidad().getId_sector() != null 
				&& 	obj.getEntidad().getId_sector() != num) {
				
				cstmt.setInt("IN_ID_SECTOR", (Integer) obj.getEntidad().getId_sector());
			
			} else {
				
				cstmt.setString("IN_ID_SECTOR", null);
			}
			
			cstmt.setString("IN_RUC", obj.getEntidad().getRuc());
			
			cstmt.setString("IN_ESTADO", obj.getEntidad().getEstado());
			
			cstmt.setString("IN_AD_ID_SESSION", usuario.getAd_id_session());
			cstmt.setInt("IN_AD_ID_USUARIO", (Integer) usuario.getAd_id_usuario());
			
			cstmt.registerOutParameter("IN_RETORNO", OracleTypes.VARCHAR);																												
			
			cstmt.execute();
			
			Object object = cstmt.getObject("IN_RETORNO");
			
			if (object != null) {
				
				Integer ret = Integer.valueOf(object.toString());
				
				if ((ret > 0)) {
					sw = true;
					conn.commit();
				} else {
					conn.rollback();
					sw = false;
				}							
			} else {
				conn.rollback();
				sw = false;
			}
		} catch (SQLException e) {
			conn.rollback();
			sw = false;
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		} finally {
			ConexionUtil.closeSqlConnections(cstmt, conn);
			ConexionUtil.closeContext(connectionJndi);
		}
		
		return sw;
	}
	
	public Integer getVerNumeroExpediente(Entidad entidad) {
		return null;
	}

	public Persona getPersona(String dni) {
		//Busqueda de persona dni en apelado
		Persona obj = null;
		
		ConnectionJndi connectionJndi = null;
		
		Connection conn = null;
		CallableStatement cstmt = null;
		ResultSet resultSets = null;
		
		try {

			connectionJndi = ConexionUtil.getConnectionJndiExpediente();
			
			conn = connectionJndi.getConnection();
			
			cstmt = conn.prepareCall("{ CALL PKG_APELADO_CONSULTA.SP_OBTENER_PERSONA(?,?) }");
			cstmt.registerOutParameter("P_C_CURSOR", OracleTypes.CURSOR);
			cstmt.setString("IN_DNI", dni);
			cstmt.execute();
			resultSets = (ResultSet) cstmt.getObject("P_C_CURSOR");
			
			while(resultSets.next()){
				obj = new Persona();
				obj.setDni((String)resultSets.getString("DNI"));
				obj.setNombre((String)resultSets.getString("NOMBRE"));
				obj.setPrimer_apellido((String)resultSets.getString("PRIMER_APELLIDO"));
				obj.setSegundo_apellido((String)resultSets.getString("SEGUNDO_APELLIDO"));
				obj.setId_persona((Integer)resultSets.getInt("ID_PERSONA"));
				break;
			}	
		} catch (NumberFormatException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		} catch (SQLException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		}finally {
			ConexionUtil.closeSqlConnections(resultSets, cstmt, conn);
			ConexionUtil.closeContext(connectionJndi);
		}
		
		return obj;
	}	

	//Se agrega línea para los representantes
	//25/01/2016 - Y
	public List<RptRepresentante> getListRepresentantes(Number idEntidad,Number id_expediente) {
		
		List<RptRepresentante> listaRpt = new ArrayList<RptRepresentante>();
		
		ConnectionJndi connectionJndi = null;
		
		Connection 			conn 		= null;
		CallableStatement 	cstmt 		= null;
		ResultSet 			resultSets 	= null;

		try {

			connectionJndi = ConexionUtil.getConnectionJndiExpediente();
			
			conn = connectionJndi.getConnection();
			
			cstmt = conn.prepareCall("{ CALL PKG_APELADO_CONSULTA.SP_LISTR_REPRESENTANTE(?,?,?) }");
			
			cstmt.registerOutParameter("P_C_CURSOR", OracleTypes.CURSOR);
			cstmt.setInt("IN_ID_ENTIDAD", NumeroUtil.parsearAInteger(idEntidad));
			cstmt.setInt("IN_ID_EXPEDIENTE", NumeroUtil.parsearAInteger(id_expediente));
			cstmt.execute();
			
			resultSets = (ResultSet) cstmt.getObject("P_C_CURSOR");
			
			while (resultSets.next()) {
				
				RptRepresentante obj = new RptRepresentante();
				
				obj.setIdEntidad((Integer) resultSets.getInt("ID_ENTIDAD"));
				obj.setDni((String) resultSets.getString("DNI"));
				obj.setNombre((String)resultSets.getString("NOMBRE"));
				obj.setCargo((String)resultSets.getString("CARGO"));
				obj.setFecha_creacion(resultSets.getString("FECHA_CREACION"));
				
				listaRpt.add(obj);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		} catch (SQLException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		} finally {
			ConexionUtil.closeSqlConnections(resultSets, cstmt, conn);
			ConexionUtil.closeContext(connectionJndi);
		}

		return listaRpt;
	}


	public List<Entidad> getListEntidadSubSede(String entidad) {
		
		List<Entidad> listaEnt = new ArrayList<Entidad>();
		
		ConnectionJndi connectionJndi = null;
		
		Connection conn = null;
		CallableStatement cstmt = null;
		ResultSet resultSets = null;
		entidad="%"+entidad.toUpperCase()+"%";
		
		try {

			connectionJndi = ConexionUtil.getConnectionJndiExpediente();
			
			conn = connectionJndi.getConnection();
			
			cstmt = conn.prepareCall("{ CALL PKG_APELADO_CONSULTA.SP_LISTR_ENTIDAD_SUB_SEDE(?,?) }");
			cstmt.registerOutParameter("P_C_CURSOR", OracleTypes.CURSOR);
			cstmt.setString("IN_ENTIDAD", entidad);
			cstmt.execute();
			resultSets = (ResultSet) cstmt.getObject("P_C_CURSOR");

			while (resultSets.next()) {
				Entidad obj = new Entidad();
				obj.setId_entidad((Integer) resultSets.getInt("ID_ENTIDAD"));
				obj.setEntidad(resultSets.getString("ENTIDAD"));
				listaEnt.add(obj);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		} catch (SQLException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		} finally {
			ConexionUtil.closeSqlConnections(resultSets, cstmt, conn);
			ConexionUtil.closeContext(connectionJndi);
		}

		return listaEnt;
	}

	@Override
	public Entidad getEntidadXRuc(String rucEntidad) {
		
		ConnectionJndi connectionJndi = null;
		
		Connection conn = null;
		CallableStatement cstmt = null;
		ResultSet resultSets = null;
		Entidad obj = null;
		
		try {

			connectionJndi = ConexionUtil.getConnectionJndiExpediente();
			
			conn = connectionJndi.getConnection();
			
			cstmt = conn.prepareCall("{ CALL PKG_APELADO_CONSULTA.SP_OBTENER_ENTIDAD_POR_RUC(?,?)}");
			cstmt.setString("IN_RUC", rucEntidad);
			cstmt.registerOutParameter("P_C_CURSOR", OracleTypes.CURSOR);
			cstmt.execute();
			resultSets = (ResultSet) cstmt.getObject("P_C_CURSOR");
			
			while(resultSets.next()){
				obj = new Entidad();
				obj.setId_entidad((Number)resultSets.getInt("ID_ENTIDAD"));
				obj.setDireccion(resultSets.getString("DIRECCION"));
				//obj.setUbigeo(resultSets.getString("UBIGEO"));
				obj.setCoddep(resultSets.getString("CODDEP"));
				obj.setCoddis(resultSets.getString("CODDIS"));
				obj.setCodpro(resultSets.getString("CODPRO"));
				obj.setEntidad(resultSets.getString("ENTIDAD"));
				obj.setId_sector(resultSets.getInt("ID_SECTOR"));
				//obj.setCargo((String)resultSets.getString("CARGO"));
				//obj.setPrimer_apellido((String)resultSets.getString("PRIMER_APELLIDO"));
				//obj.setSegundo_apellido((String)resultSets.getString("SEGUNDO_APELLIDO"));
				//obj.setNombre((String)resultSets.getString("NOMBRE"));
				obj.setEstado(resultSets.getString("ESTADO"));
				obj.setNumRucEntidad(resultSets.getString("RUC"));
				obj.setSub_sede(resultSets.getString("SUB_SEDE"));
				obj.setFlagValidadoSunat(resultSets.getString("FLAG_VALIDADO_X_SUNAT"));
			}	
			
		} catch (NumberFormatException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		} catch (SQLException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		}finally {
			ConexionUtil.closeSqlConnections(resultSets, cstmt, conn);
			ConexionUtil.closeContext(connectionJndi);
		}
		return obj;
	}
	
	public LinkedList<?> getEntidadesXRazonSocial(String razonsocial, String esSubSede) {
		
		LinkedList<EntidadVO> list = new LinkedList<EntidadVO>();
		
		ConnectionJndi connectionJndi = null;
		
		Connection conn = null;
		CallableStatement cstmt = null;
		ResultSet resultSets = null;

		try {

			connectionJndi = ConexionUtil.getConnectionJndiExpediente();
			
			conn = connectionJndi.getConnection();
			
			cstmt = conn.prepareCall("{ CALL PKG_APELADO_CONSULTA.SP_OBTENER_ENTIDADES_POR_RS(?,?,?)}");
			cstmt.registerOutParameter("P_C_CURSOR", OracleTypes.CURSOR);
			cstmt.setString("IN_RAZONSOCIAL", razonsocial);
			cstmt.setString("IN_SUB_SEDE", esSubSede);
			cstmt.execute();
			resultSets = (ResultSet) cstmt.getObject("P_C_CURSOR");
			
			while(resultSets.next()){
				EntidadVO obj = new EntidadVO();
				obj.setId((Integer)resultSets.getInt("ID_ENTIDAD"));
				obj.setNumruc((String)resultSets.getString("RUC"));
				obj.setNombre((String)resultSets.getString("ENTIDAD"));
				list.add(obj);
			}	
		} catch (NumberFormatException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		} catch (SQLException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		}finally {
			ConexionUtil.closeSqlConnections(resultSets, cstmt, conn);
			ConexionUtil.closeContext(connectionJndi); 
		}
		
		return list;
	}
	
	public List<RptRepresentante> getListJefesRRHH(Number id_entidad) {
		
		List<RptRepresentante> lista = new ArrayList<RptRepresentante>();
		
		ConnectionJndi connectionJndi = null;
		
		Connection conn = null;
		CallableStatement cstmt = null;
		ResultSet resultSets = null;
		
		try {

			connectionJndi = ConexionUtil.getConnectionJndiExpediente();
			
			conn = connectionJndi.getConnection();
			
			cstmt = conn.prepareCall("{ CALL PKG_APELADO_CONSULTA.SP_LISTR_JEFES_RRHH(?,?) }");
			cstmt.setInt("IN_ID_ENTIDAD", NumeroUtil.parsearAInteger(id_entidad));
			cstmt.registerOutParameter("P_C_CURSOR", OracleTypes.CURSOR);
			cstmt.execute();
			resultSets = (ResultSet) cstmt.getObject("P_C_CURSOR");

			while (resultSets.next()) {
				RptRepresentante obj = new RptRepresentante();
				obj.setDni(resultSets.getString("DNI"));
				obj.setNombre(resultSets.getString("NOMBRE"));
				obj.setCargo(resultSets.getString("DESCRIPCION"));
				obj.setId_persona(resultSets.getInt("ID_PERSONA"));
				obj.setId_cargo(resultSets.getInt("ID_CARGO"));
				obj.setTipo_documento(resultSets.getString("COD_TIPO_DOCUMENTO"));
				lista.add(obj);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		} catch (SQLException e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		} finally {
			ConexionUtil.closeSqlConnections(resultSets, cstmt, conn);
			ConexionUtil.closeContext(connectionJndi);
		}

		return lista;
	}
	
	public LinkedList<Entidad> getListSubSedes(String codigo) {

		LinkedList<Entidad> list = new LinkedList<Entidad>();
		String procedimiento = "";
		Connection conn = null;
		ConnectionJndi connectionJndi = null;
		CallableStatement cstm = null;
		ResultSet rec = null;
		procedimiento = "{ CALL PKG_TABLAS_GESTION.SP_GET_SUB_SEDES (?,?)}";
		try {
			connectionJndi = ConexionUtil.getConnectionJndiExpediente();			
			conn = connectionJndi.getConnection();
			cstm = conn.prepareCall(procedimiento);
			cstm.registerOutParameter("P_C_CURSOR", OracleTypes.CURSOR);
			cstm.setInt("IN_ID_ENTIDAD",NumeroUtil.parsearAInteger(codigo));
			cstm.execute();
			logger.info("Ejecuta");
			rec = (ResultSet) cstm.getObject("P_C_CURSOR");
			logger.info("Pasó" + rec);
			while (rec.next()){
				String estado=(String)rec.getString("ESTADO");
				if (estado.equals("1")){
					Entidad obj = new Entidad();
					obj.setId_padre(codigo);
					obj.setId_entidad(NumeroUtil.parsearAInteger(rec.getString("COL_COD")));
					obj.setEntidad((String)rec.getString("COL_DES"));
					list.add(obj);
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		}
		
		finally {
			ConexionUtil.closeSqlConnections(rec, cstm, conn);
			ConexionUtil.closeContext(connectionJndi);
		}
		
		return list;
	}
	
	//SICE V4.1.0.0 (Integración SGE - SICE)
	public boolean actualizarRepresentanteApelado(OficioApertura obj, Usuario usuario) throws SQLException {
		
		ConnectionJndi connectionJndi = null;
			
		Connection conn = null;
		CallableStatement cstmt = null;
		boolean sw = false;
		
		try {

			connectionJndi = ConexionUtil.getConnectionJndiExpediente();
			
			conn = connectionJndi.getConnection();
			
			conn.setAutoCommit(false);
			cstmt = conn.prepareCall("{ Call PKG_CASILLA_SOLICITUD.SP_ACTUALIZAR_REPRESNTNT_APELD ( ?,?,?,?,?,? ) }");									
			cstmt.setInt("P_ID_EXPEDIENTE", obj.getId_expediente().intValue());
			
			cstmt.setInt("P_ID_ENTIDAD", (Integer) obj.getId_entidad());
			cstmt.setInt("P_ID_PERSONA", (Integer) obj.getId_persona());

			//cstmt.setInt("IN_ID_CARGO", (Integer)obj.getId_cargo());
			cstmt.setString("P_AD_ID_SESSION", usuario.getAd_id_session());		
			cstmt.setInt("P_AD_ID_USUARIO", Integer.parseInt(usuario.getAd_id_usuario().toString()));	
			cstmt.registerOutParameter("P_RETORNO", OracleTypes.VARCHAR);																												
			cstmt.execute();
			
			Object object = cstmt.getObject("P_RETORNO");
			
			if (object!=null) {
				Integer ret = Integer.valueOf(object.toString());
				if ((ret > 0)) {
					sw = true;
					conn.commit();
				}else{
					conn.rollback();
					sw = false;
				}							
				
			}else{
				conn.rollback();
				sw = false;
			}	
									
		} catch (SQLException e) {
			conn.rollback();
			sw = false;
			e.printStackTrace();logger.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		}
		finally{
			ConexionUtil.closeSqlConnections(cstmt, conn);
			ConexionUtil.closeContext(connectionJndi);
		}
		
		return sw;
	}
}

	