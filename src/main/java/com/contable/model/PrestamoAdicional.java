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
@Table(name = "prestamos_adicionales")
public class PrestamoAdicional {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@OneToOne
	@JoinColumn(name = "id_prestamo")
	private Prestamo prestamo;
	
	@OneToOne
	@JoinColumn(name = "id_prestamo_detalle")
	private PrestamoDetalle prestamoDetalle;
	
	private String motivo;
	private Date fecha;
	private Date fecha_vencimiento;
	
	private Double monto = 0.0;
	
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
	public PrestamoDetalle getPrestamoDetalle() {
		return prestamoDetalle;
	}
	public void setPrestamoDetalle(PrestamoDetalle prestamoDetalle) {
		this.prestamoDetalle = prestamoDetalle;
	}
	public String getMotivo() {
		return motivo;
	}
	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public Date getFecha_vencimiento() {
		return fecha_vencimiento;
	}
	public void setFecha_vencimiento(Date fecha_vencimiento) {
		this.fecha_vencimiento = fecha_vencimiento;
	}
	public Double getMonto() {
		return monto;
	}
	public void setMonto(Double monto) {
		this.monto = monto;
	}
	@Override
	public String toString() {
		return "PrestamoAdicional [id=" + id + ", prestamo=" + prestamo + ", prestamoDetalle=" + prestamoDetalle
				+ ", motivo=" + motivo + ", fecha=" + fecha + ", fecha_vencimiento=" + fecha_vencimiento + ", monto="
				+ monto + "]";
	}
}
