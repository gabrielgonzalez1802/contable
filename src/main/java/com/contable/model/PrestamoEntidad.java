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
@Table(name = "prestamos_entidades")
public class PrestamoEntidad {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@OneToOne
	@JoinColumn(name = "id_inversionista")
	private Inversionista inversionista;
	
	private Double monto = 0.0;
	private Double tasa = 0.0;
	private Date fecha;
	
	@Column(name = "interes_generado")
	private Double interesGenerado = 0.0;
	
	@Column(name = "interes_pagado")
	private Double interesPagado = 0.0;
	
	private Double balance = 0.0;
	
	@OneToOne
	@JoinColumn(name = "id_empresa")
	private Empresa empresa;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Inversionista getInversionista() {
		return inversionista;
	}

	public void setInversionista(Inversionista inversionista) {
		this.inversionista = inversionista;
	}

	public Double getMonto() {
		return monto;
	}

	public void setMonto(Double monto) {
		this.monto = monto;
	}

	public Double getTasa() {
		return tasa;
	}

	public void setTasa(Double tasa) {
		this.tasa = tasa;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public Double getInteresGenerado() {
		return interesGenerado;
	}

	public void setInteresGenerado(Double interesGenerado) {
		this.interesGenerado = interesGenerado;
	}

	public Double getInteresPagado() {
		return interesPagado;
	}

	public void setInteresPagado(Double interesPagado) {
		this.interesPagado = interesPagado;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	@Override
	public String toString() {
		return "PrestamoEntidad [id=" + id + ", inversionista=" + inversionista + ", monto=" + monto + ", tasa=" + tasa
				+ ", fecha=" + fecha + ", interesGenerado=" + interesGenerado + ", interesPagado=" + interesPagado
				+ ", balance=" + balance + ", empresa=" + empresa + "]";
	}
}
