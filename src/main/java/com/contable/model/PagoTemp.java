package com.contable.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "pagos_temp")
public class PagoTemp {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private Double monto = 0.0;
	private Integer tipo;
	
	private String imagen;
	
	@OneToOne
	@JoinColumn(name = "id_prestamo")
	private Prestamo prestamo;
	
	public Double getMonto() {
		return monto;
	}
	public void setMonto(Double monto) {
		this.monto = monto;
	}
	public Integer getTipo() {
		return tipo;
	}
	public void setTipo(Integer tipo) {
		this.tipo = tipo;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Prestamo getPrestamo() {
		return prestamo;
	}
	public void setPrestamo(Prestamo prestamo) {
		this.prestamo = prestamo;
	}
	public void setImagen(String imagen) {
		this.imagen = imagen;
	}
	public String getImagen() {
		return imagen;
	}
	@Override
	public String toString() {
		return "PagoTemp [id=" + id + ", monto=" + monto + ", tipo=" + tipo + ", imagen=" + imagen + ", prestamo="
				+ prestamo + "]";
	}
}
