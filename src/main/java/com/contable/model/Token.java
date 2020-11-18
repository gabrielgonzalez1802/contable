package com.contable.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tokens")
public class Token {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String token;
	private Integer estado = 1;
	
	@Column(name = "fecha_creado")
	private Date fechaCreado = new Date();
	
	@Column(name = "fecha_expiracion")
	private Date fechaExpiracion;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Integer getEstado() {
		return estado;
	}

	public void setEstado(Integer estado) {
		this.estado = estado;
	}

	public Date getFechaCreado() {
		return fechaCreado;
	}

	public void setFechaCreado(Date fechaCreado) {
		this.fechaCreado = fechaCreado;
	}

	public Date getFechaExpiracion() {
		return fechaExpiracion;
	}

	public void setFechaExpiracion(Date fechaExpiracion) {
		this.fechaExpiracion = fechaExpiracion;
	}

	@Override
	public String toString() {
		return "Token [id=" + id + ", token=" + token + ", estado=" + estado + ", fechaCreado=" + fechaCreado
				+ ", fechaExpiracion=" + fechaExpiracion + "]";
	}
}
