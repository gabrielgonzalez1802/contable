package com.contable.model;

public class PrestamoReportePendiente {
	
	private Prestamo prestamo;
	private double moraPendiente = 0.0;
	private double adicionalPendiente = 0.0;
	private double capitalPendiente = 0.0;
	private double interesPendiente = 0.0;
	private double prestamoPendiente = 0.0;
	private String cliente;
	private double montoPrestamo = 0.0;
	
	public Prestamo getPrestamo() {
		return prestamo;
	}
	public void setPrestamo(Prestamo prestamo) {
		this.prestamo = prestamo;
	}
	public double getMoraPendiente() {
		return moraPendiente;
	}
	public void setMoraPendiente(double moraPendiente) {
		this.moraPendiente = moraPendiente;
	}
	public double getAdicionalPendiente() {
		return adicionalPendiente;
	}
	public void setAdicionalPendiente(double adicionalPendiente) {
		this.adicionalPendiente = adicionalPendiente;
	}
	public double getCapitalPendiente() {
		return capitalPendiente;
	}
	public void setCapitalPendiente(double capitalPendiente) {
		this.capitalPendiente = capitalPendiente;
	}
	public double getInteresPendiente() {
		return interesPendiente;
	}
	public void setInteresPendiente(double interesPendiente) {
		this.interesPendiente = interesPendiente;
	}
	public double getPrestamoPendiente() {
		return prestamoPendiente;
	}
	public void setPrestamoPendiente(double prestamoPendiente) {
		this.prestamoPendiente = prestamoPendiente;
	}
	public String getCliente() {
		return cliente;
	}
	public void setCliente(String cliente) {
		this.cliente = cliente;
	}
	public double getMontoPrestamo() {
		return montoPrestamo;
	}
	public void setMontoPrestamo(double montoPrestamo) {
		this.montoPrestamo = montoPrestamo;
	}
	@Override
	public String toString() {
		return "PrestamoReportePendiente [prestamo=" + prestamo + ", moraPendiente=" + moraPendiente
				+ ", adicionalPendiente=" + adicionalPendiente + ", capitalPendiente=" + capitalPendiente
				+ ", interesPendiente=" + interesPendiente + ", prestamoPendiente=" + prestamoPendiente + ", cliente="
				+ cliente + ", montoPrestamo=" + montoPrestamo + "]";
	}
}
