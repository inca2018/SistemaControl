package net.servir.expedientes.dao;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import net.servir.expedientes.model.auxiliar.Cargo;
import net.servir.expedientes.model.auxiliar.Generica;
import net.servir.expedientes.model.auxiliar.TipoDocumento;
import net.servir.expedientes.model.auxiliar.TipoOficio;
import net.servir.expedientes.model.entity.Apelado;
import net.servir.expedientes.model.entity.Entidad;
import net.servir.expedientes.model.entity.OficioApertura;
import net.servir.expedientes.model.entity.Persona;
import net.servir.expedientes.model.rpt.RptRepresentante;
import net.servir.expedientes.model.seguridad.Usuario;

public interface ApeladoDAO {

	Apelado getApelado(Number id_expediente);

	LinkedList<TipoDocumento> getTipoDocumentos();
	
	LinkedList<TipoOficio> getTipoOficios(Number id_expediente);
	
	LinkedList<TipoOficio> getTipoDocumentos(Number id_expediente);

	LinkedList<TipoOficio> getTipoRespuesta();

	boolean saveApelado(Apelado apelado, Apelado apelado_actual, Usuario usuario) throws SQLException;
	
	//void saveApeladoCargo(Apelado apelado, Usuario usuario) throws SQLException;
	boolean saveApeladoCargo(Apelado apelado, Usuario usuario) throws SQLException;

	LinkedList<Entidad> getEntidadesBuqueda(String direccion);

	Entidad getEntidad(Number id_entidad_busqueda);
	
	String getEntidadDenominacion(Number idEntidad);

	Integer getVerNumeroRegistro(OficioApertura oficioApertura);

	LinkedList<Generica> getSectors();

	LinkedList<Generica> ObtenerDepartamentos();

	LinkedList<Generica> ObtenerProvincias(String coddep);

	LinkedList<Generica> ObtenerDistritos(String coddep, String codpro);

	LinkedList<Generica> ObtenerDepartamentosReniec();

	LinkedList<Generica> ObtenerProvinciasReniec(String coddep);

	LinkedList<Generica> ObtenerDistritosReniec(String coddep, String codpro);

	//void saveEntidad(Apelado apelado, Usuario usuario) throws SQLException;
	boolean saveEntidad(Apelado apelado, Usuario usuario) throws SQLException;
	
	Integer getVerNumeroExpediente(Entidad entidad);

	LinkedList<Entidad> getEntidadesBuquedaAdm(String direccion);
	
	Persona getPersona(String dni); // para la busqueda de persona en apelado
	
	public List<RptRepresentante> getListRepresentantes(Number idEntidad,Number id_expediente);
	
	public List<Entidad> getListEntidadSubSede(String entidad);
	
	LinkedList<Cargo> getCargos(String tipo_cargo);
	
	Entidad getEntidadXRuc(String rucEntidad);
	
	LinkedList<?> getEntidadesXRazonSocial(String razonsocial, String esSubSede);
	
	public List<RptRepresentante> getListJefesRRHH(Number id_entidad);
	
	OficioApertura getOficioApertura(Number idExpediente);
	
}

