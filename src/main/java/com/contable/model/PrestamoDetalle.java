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
	
	@Column(name = "fecha_creado")
	private Date fecha;
	
	private Double monto = 0.0;
	private Double capital = 0.0;
	private Double interes = 0.0;
	
	@Column(name = "no_cuota")
	private Integer numero;
	
	private Double cuota = 0.0;
	
	private Double balance;
	private Integer pago;
	private Double mora = 0.0;
	private Double mora_pagada = 0.0;
	private Double interes_pagado = 0.0;
	private Double capital_pagado = 0.0;
	
	@Column(name = "fecha_generada")
	private Date fechaGenerada;
	
	@Column(name = "fecha_interes")
	private Date fechaInteres;
	
	@Column(name = "generar_interes")
	private Integer generarInteres = 0;
	
	private Double interes_mora = 0.0;
	
	private Integer estado = 0;
	
	private Integer dias_atraso = 0;
	
	private String estado_cuota;

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

	public Date getFechaGenerada() {
		return fechaGenerada;
	}

	public void setFechaGenerada(Date fechaGenerada) {
		this.fechaGenerada = fechaGenerada;
	}

	public Date getFechaInteres() {
		return fechaInteres;
	}

	public void setFechaInteres(Date fechaInteres) {
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

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public Double getCuota() {
		return cuota;
	}

	public void setCuota(Double cuota) {
		this.cuota = cuota;
	}

	public Double getInteres_mora() {
		return interes_mora;
	}

	public void setInteres_mora(Double interes_mora) {
		this.interes_mora = interes_mora;
	}

	public Integer getDias_atraso() {
		return dias_atraso;
	}

	public void setDias_atraso(Integer dias_atraso) {
		this.dias_atraso = dias_atraso;
	}

	public String getEstado_cuota() {
		return estado_cuota;
	}

	public void setEstado_cuota(String estado_cuota) {
		this.estado_cuota = estado_cuota;
	}

	public Double getMora_pagada() {
		return mora_pagada;
	}

	public void setMora_pagada(Double mora_pagada) {
		this.mora_pagada = mora_pagada;
	}

	public Double getInteres_pagado() {
		return interes_pagado;
	}

	public void setInteres_pagado(Double interes_pagado) {
		this.interes_pagado = interes_pagado;
	}

	public Double getCapital_pagado() {
		return capital_pagado;
	}

	public void setCapital_pagado(Double capital_pagado) {
		this.capital_pagado = capital_pagado;
	}

	@Override
	public String toString() {
		return "PrestamoDetalle [id=" + id + ", prestamo=" + prestamo + ", fecha=" + fecha + ", monto=" + monto
				+ ", capital=" + capital + ", interes=" + interes + ", numero=" + numero + ", cuota=" + cuota
				+ ", balance=" + balance + ", pago=" + pago + ", mora=" + mora + ", mora_pagada=" + mora_pagada
				+ ", interes_pagado=" + interes_pagado + ", capital_pagado=" + capital_pagado + ", fechaGenerada="
				+ fechaGenerada + ", fechaInteres=" + fechaInteres + ", generarInteres=" + generarInteres
				+ ", interes_mora=" + interes_mora + ", estado=" + estado + ", dias_atraso=" + dias_atraso
				+ ", estado_cuota=" + estado_cuota + "]";
	}
}
