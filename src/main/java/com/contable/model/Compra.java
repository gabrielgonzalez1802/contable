package com.contable.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "compras")
public class Compra {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private Date fecha;
	
	@OneToOne
	@JoinColumn(name = "id_suplidor")
	private Suplidor suplidor; 
	
	private String factura;
	private String comprobante;
	
	private Integer tipo;
	
	private Double subtotal = 0.0;
	private Double total = 0.0;
	private Double balance = 0.0;
	private Double retencion = 0.0;
	
	private Double abono = 0.0;
	
	@OneToOne
	@JoinColumn(name = "id_cuenta_contable")
	private CuentaContable cuentaContable;
	
	@OneToOne
	@JoinColumn(name = "id_empresa")
	private Empresa empresa;
	
	@OneToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;

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

	public Suplidor getSuplidor() {
		return suplidor;
	}

	public void setSuplidor(Suplidor suplidor) {
		this.suplidor = suplidor;
	}

	public String getFactura() {
		return factura;
	}

	public void setFactura(String factura) {
		this.factura = factura;
	}

	public String getComprobante() {
		return comprobante;
	}

	public void setComprobante(String comprobante) {
		this.comprobante = comprobante;
	}

	public Integer getTipo() {
		return tipo;
	}

	public void setTipo(Integer tipo) {
		this.tipo = tipo;
	}

	public Double getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(Double subtotal) {
		this.subtotal = subtotal;
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

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public Double getRetencion() {
		return retencion;
	}

	public void setRetencion(Double retencion) {
		this.retencion = retencion;
	}

	public Double getAbono() {
		return abono;
	}

	public void setAbono(Double abono) {
		this.abono = abono;
	}

	@Override
	public String toString() {
		return "Compra [id=" + id + ", fecha=" + fecha + ", suplidor=" + suplidor + ", factura=" + factura
				+ ", comprobante=" + comprobante + ", tipo=" + tipo + ", subtotal=" + subtotal + ", total=" + total
				+ ", balance=" + balance + ", retencion=" + retencion + ", abono=" + abono + ", cuentaContable="
				+ cuentaContable + ", empresa=" + empresa + ", usuario=" + usuario + "]";
	}
}
