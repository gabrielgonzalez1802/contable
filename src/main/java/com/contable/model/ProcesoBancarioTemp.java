package com.contable.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "procesos_bancarios_temp")
public class ProcesoBancarioTemp {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@OneToOne
	@JoinColumn(name = "id_forma_pago_desde")
	private FormaPago formaPagoDesde;
	
	@OneToOne
	@JoinColumn(name = "id_forma_pago_hasta")
	private FormaPago formaPagoHasta;
	
	private Double monto = 0.0;
	private Double tasa = 0.0;
	private Double total = 0.0;
	
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

	public FormaPago getFormaPagoDesde() {
		return formaPagoDesde;
	}

	public void setFormaPagoDesde(FormaPago formaPagoDesde) {
		this.formaPagoDesde = formaPagoDesde;
	}

	public FormaPago getFormaPagoHasta() {
		return formaPagoHasta;
	}

	public void setFormaPagoHasta(FormaPago formaPagoHasta) {
		this.formaPagoHasta = formaPagoHasta;
	}

	public Double getMonto() {
		return monto;
	}

	public void setMonto(Double monto) {
		this.monto = monto;
	}

	public Double getTasa() {
		return tasa;
	}

	public void setTasa(Double tasa) {
		this.tasa = tasa;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
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

	@Override
	public String toString() {
		return "ProcesoBancarioTemp [id=" + id + ", formaPagoDesde=" + formaPagoDesde + ", formaPagoHasta="
				+ formaPagoHasta + ", monto=" + monto + ", tasa=" + tasa + ", total=" + total + ", usuario=" + usuario
				+ ", empresa=" + empresa + "]";
	}
}
