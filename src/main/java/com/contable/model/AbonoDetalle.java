package com.contable.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "abonos_detalles")
public class AbonoDetalle {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@OneToOne
	@JoinColumn(name = "id_abono")
	private Abono abono;
	
	private Double monto = 0.0;
	
	private String concepto;
	
	@Column(name = "balance_inicial")
	private Double balanceInicial = 0.0;
	
	@Column(name = "balance_final")
	private Double balanceFinal = 0.0;
	
	@Column(name = "numero_cuota")
	private Integer numeroCuota;
	
	private String detalle;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Abono getAbono() {
		return abono;
	}

	public void setAbono(Abono abono) {
		this.abono = abono;
	}

	public Double getMonto() {
		return monto;
	}

	public void setMonto(Double monto) {
		this.monto = monto;
	}

	public String getConcepto() {
		return concepto;
	}

	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}

	public Double getBalanceInicial() {
		return balanceInicial;
	}

	public void setBalanceInicial(Double balanceInicial) {
		this.balanceInicial = balanceInicial;
	}

	public Double getBalanceFinal() {
		return balanceFinal;
	}

	public void setBalanceFinal(Double balanceFinal) {
		this.balanceFinal = balanceFinal;
	}

	public Integer getNumeroCuota() {
		return numeroCuota;
	}

	public void setNumeroCuota(Integer numeroCuota) {
		this.numeroCuota = numeroCuota;
	}

	public String getDetalle() {
		return detalle;
	}

	public void setDetalle(String detalle) {
		this.detalle = detalle;
	}

	@Override
	public String toString() {
		return "AbonoDetalle [id=" + id + ", abono=" + abono + ", monto=" + monto + ", concepto=" + concepto
				+ ", balanceInicial=" + balanceInicial + ", balanceFinal=" + balanceFinal + ", numeroCuota="
				+ numeroCuota + ", detalle=" + detalle + "]";
	}
}
