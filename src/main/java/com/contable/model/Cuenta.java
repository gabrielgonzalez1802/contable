package com.contable.model;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "cuentas")
public class Cuenta {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String banco;
	private String numero;
	
	private String moneda;
	
	@Transient
	private String montoPlano;
	
	@Transient
	private BigDecimal montoBigDecimal;
		
	@OneToOne
	@JoinColumn(name = "id_carpeta")
	private Carpeta carpeta;
	
	@OneToOne
	@JoinColumn(name = "id_empresa")
	private Empresa empresa;
	
	private Double monto = 0.0;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getBanco() {
		return banco;
	}

	public void setBanco(String banco) {
		this.banco = banco;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public Carpeta getCarpeta() {
		return carpeta;
	}

	public void setCarpeta(Carpeta carpeta) {
		this.carpeta = carpeta;
	}

	public Double getMonto() {
		return monto;
	}

	public void setMonto(Double monto) {
		this.monto = monto;
	}

	public String getMontoPlano() {
		return montoPlano;
	}

	public void setMontoPlano(String montoPlano) {
		this.montoPlano = montoPlano;
	}

	public BigDecimal getMontoBigDecimal() {
		return montoBigDecimal;
	}

	public void setMontoBigDecimal(BigDecimal montoBigDecimal) {
		this.montoBigDecimal = montoBigDecimal;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public String getMoneda() {
		return moneda;
	}

	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}

	@Override
	public String toString() {
		return "Cuenta [id=" + id + ", banco=" + banco + ", numero=" + numero + ", moneda=" + moneda + ", montoPlano="
				+ montoPlano + ", montoBigDecimal=" + montoBigDecimal + ", carpeta=" + carpeta + ", empresa=" + empresa
				+ ", monto=" + monto + "]";
	}
}
