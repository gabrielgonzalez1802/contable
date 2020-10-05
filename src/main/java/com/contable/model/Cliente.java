package com.contable.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.web.multipart.MultipartFile;

@Entity
@Table(name = "clientes")
public class Cliente {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String tipoDocumento;
	
	@Transient
	private String doctypeTemp;
	
	private String nombre;
	private String cedula;
	private String rnc;
	
	private String direccion;
	private String telefono;
	private String celular;
	
	private String fotoFrontal;
	private String fotoTrasera;
	
	private String nota;
	
	@Transient
	private MultipartFile frontal;
	
	@Transient
	private MultipartFile trasera;
	
	@OneToOne
	@JoinColumn(name = "usuario_eliminado")
	private Usuario usuarioEliminado;
	
	@OneToOne
	@JoinColumn(name = "usuario_modificado")
	private Usuario usuario_modificado;
	
	private Date modificado = new Date();
	private Date creado = new Date();
	private Date eliminado;
	
	@OneToOne
	@JoinColumn(name = "comprobante_fiscal_id")
	private ComprobanteFiscal comprobanteFiscal;
	
	@Column(name = "nombre_empresa")
	private String nombreEmpresa;
	
	@Column(name = "direccion_empresa")
	private String direccionEmpresa;
	
	@Column(name = "telefono_empresa")
	private String telefonoEmpresa;
	
	private Integer estado = 1;
	
	@OneToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getCedula() {
		return cedula;
	}

	public void setCedula(String cedula) {
		this.cedula = cedula;
	}

	public String getRnc() {
		return rnc;
	}

	public void setRnc(String rnc) {
		this.rnc = rnc;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public Date getCreado() {
		return creado;
	}

	public void setCreado(Date creado) {
		this.creado = creado;
	}

	public String getNombreEmpresa() {
		return nombreEmpresa;
	}

	public void setNombreEmpresa(String nombreEmpresa) {
		this.nombreEmpresa = nombreEmpresa;
	}

	public String getDireccionEmpresa() {
		return direccionEmpresa;
	}

	public void setDireccionEmpresa(String direccionEmpresa) {
		this.direccionEmpresa = direccionEmpresa;
	}

	public String getTelefonoEmpresa() {
		return telefonoEmpresa;
	}

	public void setTelefonoEmpresa(String telefonoEmpresa) {
		this.telefonoEmpresa = telefonoEmpresa;
	}

	public Integer getEstado() {
		return estado;
	}

	public void setEstado(Integer estado) {
		this.estado = estado;
	}

	public String getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(String tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}
	
	public String getCelular() {
		return celular;
	}

	public void setCelular(String celular) {
		this.celular = celular;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Usuario getUsuarioEliminado() {
		return usuarioEliminado;
	}

	public void setUsuarioEliminado(Usuario usuarioEliminado) {
		this.usuarioEliminado = usuarioEliminado;
	}

	public Date getModificado() {
		return modificado;
	}

	public void setModificado(Date modificado) {
		this.modificado = modificado;
	}

	public Date getEliminado() {
		return eliminado;
	}

	public void setEliminado(Date eliminado) {
		this.eliminado = eliminado;
	}

	public Usuario getUsuario_modificado() {
		return usuario_modificado;
	}

	public void setUsuario_modificado(Usuario usuario_modificado) {
		this.usuario_modificado = usuario_modificado;
	}

	public String getFotoFrontal() {
		return fotoFrontal;
	}

	public void setFotoFrontal(String fotoFrontal) {
		this.fotoFrontal = fotoFrontal;
	}

	public String getFotoTrasera() {
		return fotoTrasera;
	}

	public void setFotoTrasera(String fotoTrasera) {
		this.fotoTrasera = fotoTrasera;
	}

	public MultipartFile getFrontal() {
		return frontal;
	}

	public void setFrontal(MultipartFile frontal) {
		this.frontal = frontal;
	}

	public MultipartFile getTrasera() {
		return trasera;
	}

	public void setTrasera(MultipartFile trasera) {
		this.trasera = trasera;
	}

	public ComprobanteFiscal getComprobanteFiscal() {
		return comprobanteFiscal;
	}

	public void setComprobanteFiscal(ComprobanteFiscal comprobanteFiscal) {
		this.comprobanteFiscal = comprobanteFiscal;
	}

	public String getDoctypeTemp() {
		return doctypeTemp;
	}

	public void setDoctypeTemp(String doctypeTemp) {
		this.doctypeTemp = doctypeTemp;
	}
	

	public String getNota() {
		return nota;
	}

	public void setNota(String nota) {
		this.nota = nota;
	}

	@Override
	public String toString() {
		return "Cliente [id=" + id + ", tipoDocumento=" + tipoDocumento + ", doctypeTemp=" + doctypeTemp + ", nombre="
				+ nombre + ", cedula=" + cedula + ", rnc=" + rnc + ", direccion=" + direccion + ", telefono=" + telefono
				+ ", celular=" + celular + ", fotoFrontal=" + fotoFrontal + ", fotoTrasera=" + fotoTrasera + ", nota="
				+ nota + ", frontal=" + frontal + ", trasera=" + trasera + ", usuarioEliminado=" + usuarioEliminado
				+ ", usuario_modificado=" + usuario_modificado + ", modificado=" + modificado + ", creado=" + creado
				+ ", eliminado=" + eliminado + ", comprobanteFiscal=" + comprobanteFiscal + ", nombreEmpresa="
				+ nombreEmpresa + ", direccionEmpresa=" + direccionEmpresa + ", telefonoEmpresa=" + telefonoEmpresa
				+ ", estado=" + estado + ", usuario=" + usuario + "]";
	}
}
