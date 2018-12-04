package net.servir.expedientes.model.entity;

public class Apelado {

	  private Number id_expediente;
	  private Number id_entidad;
	  private String direccion;
	  //private String ubigeo;
	  private Entidad entidad;
	  private OficioApertura oficioApertura;
	  private String dni;
	  private Persona persona;
	  private Number id_persona;
	  private String coddep;
	  private String codpro;
	  private String coddis;
	  
	public Entidad getEntidad() {
		return entidad;
	}
	public void setEntidad(Entidad entidad) {
		this.entidad = entidad;
	}
	public Number getId_expediente() {
		return id_expediente;
	}
	public void setId_expediente(Number id_expediente) {
		this.id_expediente = id_expediente;
	}
	public Number getId_entidad() {
		return id_entidad;
	}
	public void setId_entidad(Number id_entidad) {
		this.id_entidad = id_entidad;
	}
	public String getDireccion() {
		return direccion;
	}
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	/*public String getUbigeo() {
		return ubigeo;
	}
	public void setUbigeo(String ubigeo) {
		this.ubigeo = ubigeo;
	}*/
	
	public String getCoddep() {
		return coddep;
	}

	public void setCoddep(String coddep) {
		this.coddep = coddep;
	}

	public String getCodpro() {
		return codpro;
	}

	public void setCodpro(String codpro) {
		this.codpro = codpro;
	}

	public String getCoddis() {
		return coddis;
	}

	public void setCoddis(String coddis) {
		this.coddis = coddis;
	}
	
	public void setOficioApertura(OficioApertura oficioApertura) {
		this.oficioApertura = oficioApertura;
	}
	public OficioApertura getOficioApertura() {
		return oficioApertura;
	}
	public String getDni() {
		return dni;
	}
	public void setDni(String dni) {
		this.dni = dni;
	}
	public Persona getPersona() {
		return persona;
	}
	public void setPersona(Persona persona) {
		this.persona = persona;
	}
	public Number getId_persona() {
		return id_persona;
	}
	public void setId_persona(Number id_persona) {
		this.id_persona = id_persona;
	}

	  
	  
	}
