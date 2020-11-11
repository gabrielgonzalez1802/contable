package com.contable.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.web.multipart.MultipartFile;

@Entity
@Table(name = "empresas")
public class Empresa {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String nombre;
	private String direccion;
	private String telefono;
	private String rnc;
	private String logo;
	
	@Transient
	private MultipartFile logoTemp;
	
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
	public String getRnc() {
		return rnc;
	}
	public void setRnc(String rnc) {
		this.rnc = rnc;
	}
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public MultipartFile getLogoTemp() {
		return logoTemp;
	}
	public void setLogoTemp(MultipartFile logoTemp) {
		this.logoTemp = logoTemp;
	}
	@Override
	public String toString() {
		return "Empresa [id=" + id + ", nombre=" + nombre + ", direccion=" + direccion + ", telefono=" + telefono
				+ ", rnc=" + rnc + ", logo=" + logo + ", logoTemp=" + logoTemp + "]";
	}
}
