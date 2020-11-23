package com.contable.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "cuentas_contable")
public class CuentaContable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String codigo;
	
	@Column(name = "nombre_cuenta")
	private String nombreCuenta;
	
	@Column(name = "cuenta_control")
	private String cuentaControl;
	
	private String tipo;
	
	@Column(name = "id_cuenta_control")
	private Integer idCuentaControl;
	
	@Column(name = "grupo_cuenta")
	private String grupoCuenta;
	
	private Integer estado = 0;
		
	@Transient
	private String clase = "";
	
	@OneToOne
	@JoinColumn(name = "id_empresa")
	private Empresa empresa;
	
	@Transient
	private Double monto = 0.0;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getNombreCuenta() {
		return nombreCuenta;
	}

	public void setNombreCuenta(String nombreCuenta) {
		this.nombreCuenta = nombreCuenta;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public String getGrupoCuenta() {
		return grupoCuenta;
	}

	public void setGrupoCuenta(String grupoCuenta) {
		this.grupoCuenta = grupoCuenta;
	}

	public String getCuentaControl() {
		return cuentaControl;
	}

	public void setCuentaControl(String cuentaControl) {
		this.cuentaControl = cuentaControl;
	}

	public Integer getIdCuentaControl() {
		return idCuentaControl;
	}

	public void setIdCuentaControl(Integer idCuentaControl) {
		this.idCuentaControl = idCuentaControl;
	}

	public String getClase() {
		return clase;
	}

	public void setClase(String clase) {
		this.clase = clase;
	}

	public Integer getEstado() {
		return estado;
	}

	public void setEstado(Integer estado) {
		this.estado = estado;
	}

	public Double getMonto() {
		return monto;
	}

	public void setMonto(Double monto) {
		this.monto = monto;
	}

	@Override
	public String toString() {
		return "CuentaContable [id=" + id + ", codigo=" + codigo + ", nombreCuenta=" + nombreCuenta + ", cuentaControl="
				+ cuentaControl + ", tipo=" + tipo + ", idCuentaControl=" + idCuentaControl + ", grupoCuenta="
				+ grupoCuenta + ", estado=" + estado + ", clase=" + clase + ", empresa=" + empresa + ", monto=" + monto
				+ "]";
	}
}
