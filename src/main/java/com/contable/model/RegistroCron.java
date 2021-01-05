package com.contable.model;

public class RegistroCron {
	
	private Empresa empresa;
	private Prestamo prestamo;
	private Double mora;
	private String moneda;
	
	public Prestamo getPrestamo() {
		return prestamo;
	}
	public void setPrestamo(Prestamo prestamo) {
		this.prestamo = prestamo;
	}
	public Double getMora() {
		return mora;
	}
	public void setMora(Double mora) {
		this.mora = mora;
	}
	public String getMoneda() {
		return moneda;
	}
	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}
	
	public Empresa getEmpresa() {
		return empresa;
	}
	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}
	@Override
	public String toString() {
		return "RegistroCron [empresa=" + empresa + ", prestamo=" + prestamo + ", mora=" + mora + ", moneda=" + moneda
				+ "]";
	}
}
