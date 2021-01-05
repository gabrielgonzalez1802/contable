package com.contable.model;

public class PrestamoCalculoTotal {

	private Prestamo prestamo;
	private Double capital;
	private Double interes;
	private Double mora;
	private Double adicionales;
	private Double balance;
	private String moneda;
	
	public Prestamo getPrestamo() {
		return prestamo;
	}
	public void setPrestamo(Prestamo prestamo) {
		this.prestamo = prestamo;
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
	public Double getMora() {
		return mora;
	}
	public void setMora(Double mora) {
		this.mora = mora;
	}
	public Double getAdicionales() {
		return adicionales;
	}
	public void setAdicionales(Double adicionales) {
		this.adicionales = adicionales;
	}
	public Double getBalance() {
		return balance;
	}
	public void setBalance(Double balance) {
		this.balance = balance;
	}
	public String getMoneda() {
		return moneda;
	}
	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}
	@Override
	public String toString() {
		return "PrestamoCalculoTotal [prestamo=" + prestamo + ", capital=" + capital + ", interes=" + interes
				+ ", mora=" + mora + ", adicionales=" + adicionales + ", balance=" + balance + ", moneda=" + moneda
				+ "]";
	}
}
