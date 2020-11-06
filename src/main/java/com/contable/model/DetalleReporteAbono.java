package com.contable.model;

public class DetalleReporteAbono {

	private Integer id;
	private String tipo;
	private Integer cuota;
	private Double pagoCapital;
	private Double pagoInteres;
	private Double pagoMoras;
	private Double pagoCargos;
	private Double total;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public Integer getCuota() {
		return cuota;
	}
	public void setCuota(Integer cuota) {
		this.cuota = cuota;
	}
	public Double getPagoCapital() {
		return pagoCapital;
	}
	public void setPagoCapital(Double pagoCapital) {
		this.pagoCapital = pagoCapital;
	}
	public Double getPagoInteres() {
		return pagoInteres;
	}
	public void setPagoInteres(Double pagoInteres) {
		this.pagoInteres = pagoInteres;
	}
	public Double getPagoMoras() {
		return pagoMoras;
	}
	public void setPagoMoras(Double pagoMoras) {
		this.pagoMoras = pagoMoras;
	}
	public Double getPagoCargos() {
		return pagoCargos;
	}
	public void setPagoCargos(Double pagoCargos) {
		this.pagoCargos = pagoCargos;
	}
	public Double getTotal() {
		return total;
	}
	public void setTotal(Double total) {
		this.total = total;
	}
	@Override
	public String toString() {
		return "DetalleReporteAbono [id=" + id + ", tipo=" + tipo + ", cuota=" + cuota + ", pagoCapital=" + pagoCapital
				+ ", pagoInteres=" + pagoInteres + ", pagoMoras=" + pagoMoras + ", pagoCargos=" + pagoCargos
				+ ", total=" + total + "]";
	}
}
