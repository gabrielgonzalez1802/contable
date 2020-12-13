package com.contable.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "compras_itbis")
public class CompraItbis {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@OneToOne
	@JoinColumn(name = "id_compra")
	private Compra compra;
	
	private Double montoItbis = 0.0;
	
	@OneToOne
	@JoinColumn(name = "id_cuenta_contable")
	private CuentaContable cuentaContable;
	
	private Integer itbis;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Compra getCompra() {
		return compra;
	}

	public void setCompra(Compra compra) {
		this.compra = compra;
	}

	public Integer getItbis() {
		return itbis;
	}

	public void setItbis(Integer itbis) {
		this.itbis = itbis;
	}

	public Double getMontoItbis() {
		return montoItbis;
	}

	public void setMontoItbis(Double montoItbis) {
		this.montoItbis = montoItbis;
	}

	public CuentaContable getCuentaContable() {
		return cuentaContable;
	}

	public void setCuentaContable(CuentaContable cuentaContable) {
		this.cuentaContable = cuentaContable;
	}

	@Override
	public String toString() {
		return "CompraItbis [id=" + id + ", compra=" + compra + ", montoItbis=" + montoItbis + ", cuentaContable="
				+ cuentaContable + ", itbis=" + itbis + "]";
	}
}
