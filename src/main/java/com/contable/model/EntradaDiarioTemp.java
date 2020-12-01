package com.contable.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "entradas_diario_temp")
public class EntradaDiarioTemp {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private BigDecimal monto;
	private String referencia;
	
	@OneToOne
	@JoinColumn(name = "id_cuenta_contable")
	private CuentaContable cuentaContable;
	
	private Integer tipo;
	
	@Column(name = "balance_inicial")
	private BigDecimal balanceInicial;
	
	@Column(name = "balance_final")
	private BigDecimal balanceFinal;
	
	@OneToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;
	
	@OneToOne
	@JoinColumn(name = "id_empresa")
	private Empresa empresa;
	
	private BigDecimal debito;
	private BigDecimal credito;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public BigDecimal getMonto() {
		return monto;
	}

	public void setMonto(BigDecimal monto) {
		this.monto = monto;
	}

	public String getReferencia() {
		return referencia;
	}

	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

	public CuentaContable getCuentaContable() {
		return cuentaContable;
	}

	public void setCuentaContable(CuentaContable cuentaContable) {
		this.cuentaContable = cuentaContable;
	}

	public Integer getTipo() {
		return tipo;
	}

	public void setTipo(Integer tipo) {
		this.tipo = tipo;
	}

	public BigDecimal getBalanceInicial() {
		return balanceInicial;
	}

	public void setBalanceInicial(BigDecimal balanceInicial) {
		this.balanceInicial = balanceInicial;
	}

	public BigDecimal getBalanceFinal() {
		return balanceFinal;
	}

	public void setBalanceFinal(BigDecimal balanceFinal) {
		this.balanceFinal = balanceFinal;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
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

	public void setCredio(BigDecimal credito) {
		this.credito = credito;
	}

	@Override
	public String toString() {
		return "EntradaDiarioTemp [id=" + id + ", monto=" + monto + ", referencia=" + referencia + ", cuentaContable="
				+ cuentaContable + ", tipo=" + tipo + ", balanceInicial=" + balanceInicial + ", balanceFinal="
				+ balanceFinal + ", usuario=" + usuario + ", empresa=" + empresa + ", debito=" + debito + ", credio="
				+ credito + "]";
	}
}
