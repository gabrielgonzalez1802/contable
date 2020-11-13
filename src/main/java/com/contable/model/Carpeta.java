package com.contable.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "carpetas")
public class Carpeta {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String nombre;
	private Integer principal = 0;
	
	@OneToOne
	@JoinColumn(name = "id_empresa")
	private Empresa empresa;
	
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
	public Integer getPrincipal() {
		return principal;
	}
	public void setPrincipal(Integer principal) {
		this.principal = principal;
	}
	
	public Empresa getEmpresa() {
		return empresa;
	}
	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}
	
	@Override
	public String toString() {
		return "Carpeta [id=" + id + ", nombre=" + nombre + ", principal=" + principal + ", empresa=" + empresa + "]";
	}
}
