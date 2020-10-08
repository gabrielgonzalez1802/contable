package com.contable.model;

public class Amortizacion {
	
	private Integer numero = 0;
	private String fecha;
	private Double cuota;
	private Double capital;
	private Double interes;
	private Double saldo;
	
	public String getFecha() {
		return fecha;
	}
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	public Double getCuota() {
		return cuota;
	}
	public void setCuota(Double cuota) {
		this.cuota = cuota;
	}
	public Double getCapital() {
		return capital;
	}
	public void setCapital(Double capital) {
		this.capital = capital;
	}
	public Double getInteres() {
		return interes;
	}
	public void setInteres(Double interes) {
		this.interes = interes;
	}
	public Double getSaldo() {
		return saldo;
	}
	public void setSaldo(Double saldo) {
		this.saldo = saldo;
	}
	public Integer getNumero() {
		return numero;
	}
	public void setNumero(Integer numero) {
		this.numero = numero;
	}
	@Override
	public String toString() {
		return "Amortizacion [numero=" + numero + ", fecha=" + fecha + ", cuota=" + cuota + ", capital=" + capital
				+ ", interes=" + interes + ", saldo=" + saldo + "]";
	}
}
