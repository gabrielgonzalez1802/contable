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
	
	private Date fecha_cuota;
	
	@Column(name = "fecha_vencimiento")
	private Date vencimiento;
	
	private Double capital = 0.0;
	
	@Column(name = "interes_generado")
	private Double interes = 0.0;
	
	@Column(name = "mora_generada")
	private Double mora = 0.0;
	
	@Column(name = "monto_pagado")
	private Double pagado = 0.0;
	
	private Double balance = 0.0;
	
	private Integer estado = 0;
	
	@Column(name = "pagado")
	private Integer estadoPago = 0;
	
	private Double tasa = 0.0;
	
	@OneToOne
	@JoinColumn(name = "id_prestamo")
	private Prestamo prestamo;
	
	private Integer dias_atraso = 0;
	
	private Double mora_pagada = 0.0;
	private Double interes_pagado = 0.0;
	private Double capital_pagado = 0.0;

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

	public Date getVencimiento() {
		return vencimiento;
	}

	public void setVencimiento(Date vencimiento) {
		this.vencimiento = vencimiento;
	}

	public Double getMora() {
		return mora;
	}

	public void setMora(Double mora) {
		this.mora = mora;
	}

	public Double getPagado() {
		return pagado;
	}

	public void setPagado(Double pagado) {
		this.pagado = pagado;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public Integer getEstadoPago() {
		return estadoPago;
	}

	public void setEstadoPago(Integer estadoPago) {
		this.estadoPago = estadoPago;
	}

	public Double getTasa() {
		return tasa;
	}

	public void setTasa(Double tasa) {
		this.tasa = tasa;
	}

	public Integer getDias_atraso() {
		return dias_atraso;
	}

	public void setDias_atraso(Integer dias_atraso) {
		this.dias_atraso = dias_atraso;
	}

	public Date getFecha_cuota() {
		return fecha_cuota;
	}

	public void setFecha_cuota(Date fecha_cuota) {
		this.fecha_cuota = fecha_cuota;
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
		return "PrestamoInteresDetalle [id=" + id + ", fecha=" + fecha + ", fecha_cuota=" + fecha_cuota
				+ ", vencimiento=" + vencimiento + ", capital=" + capital + ", interes=" + interes + ", mora=" + mora
				+ ", pagado=" + pagado + ", balance=" + balance + ", estado=" + estado + ", estadoPago=" + estadoPago
				+ ", tasa=" + tasa + ", prestamo=" + prestamo + ", dias_atraso=" + dias_atraso + ", mora_pagada="
				+ mora_pagada + ", interes_pagado=" + interes_pagado + ", capital_pagado=" + capital_pagado + "]";
	}
}
