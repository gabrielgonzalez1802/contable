package com.contable.model;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "entradas_diario")
public class EntradaDiario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@OneToOne
	@JoinColumn(name = "id_cuenta_contable")
	private CuentaContable cuentaContable;
	
	private BigDecimal debito;
	private BigDecimal credito;
	
	private String detalle;
	
	@OneToOne
	@JoinColumn(name = "id_diario")
	private Diario diario;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public CuentaContable getCuentaContable() {
		return cuentaContable;
	}

	public void setCuentaContable(CuentaContable cuentaContable) {
		this.cuentaContable = cuentaContable;
	}

	public BigDecimal getDebito() {
		return debito;
	}

	public void setDebito(BigDecimal debito) {
		this.debito = debito;
	}

	public BigDecimal getCredito() {
		return credito;
	}

	public void setCredito(BigDecimal credito) {
		this.credito = credito;
	}

	public String getDetalle() {
		return detalle;
	}

	public void setDetalle(String detalle) {
		this.detalle = detalle;
	}

	public Diario getDiario() {
		return diario;
	}

	public void setDiario(Diario diario) {
		this.diario = diario;
	}

	@Override
	public String toString() {
		return "EntradaDdiario [id=" + id + ", cuentaContable=" + cuentaContable + ", debito=" + debito + ", credito="
				+ credito + ", detalle=" + detalle + ", diario=" + diario + "]";
	}
}
