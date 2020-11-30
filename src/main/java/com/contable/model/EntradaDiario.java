package com.contable.model;

import java.math.BigDecimal;
import java.util.Date;

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
	
	private BigDecimal debito = new BigDecimal(0);
	private BigDecimal credito = new BigDecimal(0);
	
	private String detalle;
	
	@OneToOne
	@JoinColumn(name = "id_diario")
	private Diario diario;
	
	@OneToOne
	@JoinColumn(name = "id_cuenta_contable_ref")
	private CuentaContable cuentaContableRef;
	
	@OneToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;
	
	private Date fecha;
	
	@OneToOne
	@JoinColumn(name = "id_carpeta")
	private Carpeta carpeta;
	
	@OneToOne
	@JoinColumn(name = "id_empresa")
	private Empresa empresa;

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

	public Carpeta getCarpeta() {
		return carpeta;
	}

	public void setCarpeta(Carpeta carpeta) {
		this.carpeta = carpeta;
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

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public CuentaContable getCuentaContableRef() {
		return cuentaContableRef;
	}

	public void setCuentaContableRef(CuentaContable cuentaContableRef) {
		this.cuentaContableRef = cuentaContableRef;
	}

	@Override
	public String toString() {
		return "EntradaDiario [id=" + id + ", cuentaContable=" + cuentaContable + ", debito=" + debito + ", credito="
				+ credito + ", detalle=" + detalle + ", diario=" + diario + ", cuentaContableRef=" + cuentaContableRef
				+ ", usuario=" + usuario + ", fecha=" + fecha + ", carpeta=" + carpeta + ", empresa=" + empresa + "]";
	}
}
