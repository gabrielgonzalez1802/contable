package com.contable.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "suplidores_cuentas_contables")
public class SuplidorCuentaContable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@OneToOne
	@JoinColumn(name = "id_suplidor")
	private Suplidor suplidor;
	
	@OneToOne
	@JoinColumn(name = "id_cuenta_contable")
	private CuentaContable cuentaContable;
	
	private Integer impuesto;
	
	@OneToOne
	@JoinColumn(name = "id_empresa")
	private Empresa empresa;
	
	@OneToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;
	
	@Transient
	private Double valorPorcentajeItbis = 0.0;

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

	public Integer getImpuesto() {
		return impuesto;
	}

	public void setImpuesto(Integer impuesto) {
		this.impuesto = impuesto;
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
	
	public Suplidor getSuplidor() {
		return suplidor;
	}

	public void setSuplidor(Suplidor suplidor) {
		this.suplidor = suplidor;
	}

	public Double getValorPorcentajeItbis() {
		return valorPorcentajeItbis;
	}

	public void setValorPorcentajeItbis(Double valorPorcentajeItbis) {
		this.valorPorcentajeItbis = valorPorcentajeItbis;
	}

	@Override
	public String toString() {
		return "SuplidorCuentaContable [id=" + id + ", suplidor=" + suplidor + ", cuentaContable=" + cuentaContable
				+ ", impuesto=" + impuesto + ", empresa=" + empresa + ", usuario=" + usuario + ", valorPorcentajeItbis="
				+ valorPorcentajeItbis + "]";
	}
}
