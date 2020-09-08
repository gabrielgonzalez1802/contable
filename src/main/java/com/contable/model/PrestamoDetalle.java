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
@Table(name = "prestamos_detalle")
public class PrestamoDetalle {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@OneToOne
	@JoinColumn(name = "id_prestamo")
	private Prestamo prestamo;
	
	private Date fecha;
	private Double monto = 0.0;
	private Double capital = 0.0;
	private Double interes = 0.0;
	
	@Column(name = "no_cuota")
	private Integer cuota;
	
	private Double balance;
	private Integer pago;
	private Double mora = 0.0;
	
	@Column(name = "fecha_generada")
	private Double fechaGenerada;
	
	@Column(name = "fecha_interes")
	private Double fechaInteres;
	
	@Column(name = "generar_interes")
	private Integer generarInteres = 0;
	
	private Integer estado = 0;

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

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public Double getMonto() {
		return monto;
	}

	public void setMonto(Double monto) {
		this.monto = monto;
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

	public Integer getCuota() {
		return cuota;
	}

	public void setCuota(Integer cuota) {
		this.cuota = cuota;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public Integer getPago() {
		return pago;
	}

	public void setPago(Integer pago) {
		this.pago = pago;
	}

	public Double getMora() {
		return mora;
	}

	public void setMora(Double mora) {
		this.mora = mora;
	}

	public Double getFechaGenerada() {
		return fechaGenerada;
	}

	public void setFechaGenerada(Double fechaGenerada) {
		this.fechaGenerada = fechaGenerada;
	}

	public Double getFechaInteres() {
		return fechaInteres;
	}

	public void setFechaInteres(Double fechaInteres) {
		this.fechaInteres = fechaInteres;
	}

	public Integer getGenerarInteres() {
		return generarInteres;
	}

	public void setGenerarInteres(Integer generarInteres) {
		this.generarInteres = generarInteres;
	}

	public Integer getEstado() {
		return estado;
	}

	public void setEstado(Integer estado) {
		this.estado = estado;
	}
	
	
}