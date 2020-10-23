package com.contable.model;

import java.util.Date;

public class PrestamoDetalleMixto {
	private Integer id;
	private Integer numero;
	private Date fecha;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getNumero() {
		return numero;
	}
	public void setNumero(Integer numero) {
		this.numero = numero;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	@Override
	public String toString() {
		return "PrestamoDetalleMixto [id=" + id + ", numero=" + numero + ", fecha=" + fecha + "]";
	}
}
