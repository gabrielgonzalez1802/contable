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
@Table(name = "entradas_ingresos_contables")
public class EntradaIngresoContable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private Date fecha;
	private String info;
	private Integer cantidad;
	private Double costo = 0.0;
	private Double total = 0.0;
	private Double balance = 0.0;
	
	private String tipo;
	
	@Column(name = "balance_contable")
	private Double balanceContable;
	
	@Column(name = "balance_contable_inicial")
	private Double balanceContableInicial;
	
	@OneToOne
	@JoinColumn(name = "id_compra")
	private Compra compra;
	
	@OneToOne
	@JoinColumn(name = "id_cuenta_contable")
	private CuentaContable cuentaContable;
	
	@OneToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;
	
	@OneToOne
	@JoinColumn(name = "id_empresa")
	private Empresa empresa;

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

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}

	public Double getCosto() {
		return costo;
	}

	public void setCosto(Double costo) {
		this.costo = costo;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public CuentaContable getCuentaContable() {
		return cuentaContable;
	}

	public void setCuentaContable(CuentaContable cuentaContable) {
		this.cuentaContable = cuentaContable;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public Compra getCompra() {
		return compra;
	}

	public void setCompra(Compra compra) {
		this.compra = compra;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public Double getBalanceContable() {
		return balanceContable;
	}

	public void setBalanceContable(Double balanceContable) {
		this.balanceContable = balanceContable;
	}

	public Double getBalanceContableInicial() {
		return balanceContableInicial;
	}

	public void setBalanceContableInicial(Double balanceContableInicial) {
		this.balanceContableInicial = balanceContableInicial;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	@Override
	public String toString() {
		return "EntradaIngresoContable [id=" + id + ", fecha=" + fecha + ", info=" + info + ", cantidad=" + cantidad
				+ ", costo=" + costo + ", total=" + total + ", balance=" + balance + ", tipo=" + tipo
				+ ", balanceContable=" + balanceContable + ", balanceContableInicial=" + balanceContableInicial
				+ ", compra=" + compra + ", cuentaContable=" + cuentaContable + ", usuario=" + usuario + ", empresa="
				+ empresa + "]";
	}
}
