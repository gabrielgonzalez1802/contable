package com.contable.model;

import java.util.Date;

public class DetalleMultiPrestamo {
	
	private Integer id;
	private Date fecha;
	private Double monto;
	private Double descuento_adicionales;
	private Double monto_pagado;
	private String motivo;
	private String nota;
	private Double interes;
	private Double interes_pagado;
	private Double mora;
	private Double descuento_mora;
	private Double mora_pagada;
	private Double capital;
	private Double capital_pagado;
	
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
	public Double getMonto() {
		return monto;
	}
	public void setMonto(Double monto) {
		this.monto = monto;
	}
	public Double getDescuento_adicionales() {
		return descuento_adicionales;
	}
	public void setDescuento_adicionales(Double descuento_adicionales) {
		this.descuento_adicionales = descuento_adicionales;
	}
	public Double getMonto_pagado() {
		return monto_pagado;
	}
	public void setMonto_pagado(Double monto_pagado) {
		this.monto_pagado = monto_pagado;
	}
	public String getMotivo() {
		return motivo;
	}
	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}
	public String getNota() {
		return nota;
	}
	public void setNota(String nota) {
		this.nota = nota;
	}
	public Double getInteres() {
		return interes;
	}
	public void setInteres(Double interes) {
		this.interes = interes;
	}
	public Double getInteres_pagado() {
		return interes_pagado;
	}
	public void setInteres_pagado(Double interes_pagado) {
		this.interes_pagado = interes_pagado;
	}
	public Double getMora() {
		return mora;
	}
	public void setMora(Double mora) {
		this.mora = mora;
	}
	public Double getDescuento_mora() {
		return descuento_mora;
	}
	public void setDescuento_mora(Double descuento_mora) {
		this.descuento_mora = descuento_mora;
	}
	public Double getMora_pagada() {
		return mora_pagada;
	}
	public void setMora_pagada(Double mora_pagada) {
		this.mora_pagada = mora_pagada;
	}
	public Double getCapital() {
		return capital;
	}
	public void setCapital(Double capital) {
		this.capital = capital;
	}
	public Double getCapital_pagado() {
		return capital_pagado;
	}
	public void setCapital_pagado(Double capital_pagado) {
		this.capital_pagado = capital_pagado;
	}
	@Override
	public String toString() {
		return "DetalleMultiPrestamo [id=" + id + ", fecha=" + fecha + ", monto=" + monto + ", descuento_adicionales="
				+ descuento_adicionales + ", monto_pagado=" + monto_pagado + ", motivo=" + motivo + ", nota=" + nota
				+ ", interes=" + interes + ", interes_pagado=" + interes_pagado + ", mora=" + mora + ", descuento_mora="
				+ descuento_mora + ", mora_pagada=" + mora_pagada + ", capital=" + capital + ", capital_pagado="
				+ capital_pagado + "]";
	}
}
