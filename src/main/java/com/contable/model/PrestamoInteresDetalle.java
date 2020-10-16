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

@Entity
@Table(name = "prestamos_interes_detalles")
public class PrestamoInteresDetalle {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private Date fecha;
	private Double capital = 0.0;
	
	@Column(name = "interes_generado")
	private Double interes = 0.0;
	
	private Integer estado = 0;
	
	@OneToOne
	@JoinColumn(name = "id_prestamo")
	private Prestamo prestamo;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public Double getCapital() {
		return capital;
	}

	public void setCapital(Double capital) {
		this.capital = capital;
	}

	public Double getInteres() {
		return interes;
	}

	public void setInteres(Double interes) {
		this.interes = interes;
	}

	public Integer getEstado() {
		return estado;
	}

	public void setEstado(Integer estado) {
		this.estado = estado;
	}

	public Prestamo getPrestamo() {
		return prestamo;
	}

	public void setPrestamo(Prestamo prestamo) {
		this.prestamo = prestamo;
	}

	@Override
	public String toString() {
		return "PrestamoInteresDetalle [id=" + id + ", fecha=" + fecha + ", capital=" + capital + ", interes=" + interes
				+ ", estado=" + estado + ", prestamo=" + prestamo + "]";
	}
}
