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
@Table(name = "pagos_temp")
public class PagoTemp {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private Double monto = 0.0;
	private Integer tipo;
	
	private String imagen;
	
	@OneToOne
	@JoinColumn(name = "id_empresa")
	private Empresa empresa;
	
	@OneToOne
	@JoinColumn(name = "id_cuenta_contable")
	private CuentaContable cuentaContable;
	
	@OneToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;
	
	@OneToOne
	@JoinColumn(name = "id_entrada_diario")
	private EntradaDiario entradaDiario;
	
	@OneToOne
	@JoinColumn(name = "id_compra")
	private Compra compra;
	
	private Date fecha;
	
	@OneToOne
	@JoinColumn(name = "id_prestamo")
	private Prestamo prestamo;
	
	public Double getMonto() {
		return monto;
	}
	public void setMonto(Double monto) {
		this.monto = monto;
	}
	public Integer getTipo() {
		return tipo;
	}
	public void setTipo(Integer tipo) {
		this.tipo = tipo;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Prestamo getPrestamo() {
		return prestamo;
	}
	public void setPrestamo(Prestamo prestamo) {
		this.prestamo = prestamo;
	}
	public void setImagen(String imagen) {
		this.imagen = imagen;
	}
	public String getImagen() {
		return imagen;
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
	public EntradaDiario getEntradaDiario() {
		return entradaDiario;
	}
	public void setEntradaDiario(EntradaDiario entradaDiario) {
		this.entradaDiario = entradaDiario;
	}
	public Compra getCompra() {
		return compra;
	}
	public void setCompra(Compra compra) {
		this.compra = compra;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public CuentaContable getCuentaContable() {
		return cuentaContable;
	}
	public void setCuentaContable(CuentaContable cuentaContable) {
		this.cuentaContable = cuentaContable;
	}
	@Override
	public String toString() {
		return "PagoTemp [id=" + id + ", monto=" + monto + ", tipo=" + tipo + ", imagen=" + imagen + ", empresa="
				+ empresa + ", cuentaContable=" + cuentaContable + ", usuario=" + usuario + ", entradaDiario="
				+ entradaDiario + ", compra=" + compra + ", fecha=" + fecha + ", prestamo=" + prestamo + "]";
	}
}
