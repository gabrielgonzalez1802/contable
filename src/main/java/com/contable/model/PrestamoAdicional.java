package com.contable.model;

import java.util.Date;

import javax.persistence.Column;
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
	
	private Integer estado = 0;
	
	@Column(name = "numero_cuota")
	private Integer numeroCuota = 0;
	
	private Double monto_pagado = 0.0;
	
	private Double descuento_adicionales = 0.0;
	
	private String nota;
	
	@OneToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;

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
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	public Integer getEstado() {
		return estado;
	}
	public void setEstado(Integer estado) {
		this.estado = estado;
	}
	public Double getMonto_pagado() {
		return monto_pagado;
	}
	public void setMonto_pagado(Double monto_pagado) {
		this.monto_pagado = monto_pagado;
	}
	public Integer getNumeroCuota() {
		return numeroCuota;
	}
	public void setNumeroCuota(Integer numeroCuota) {
		this.numeroCuota = numeroCuota;
	}
	public String getNota() {
		return nota;
	}
	public void setNota(String nota) {
		this.nota = nota;
	}
	public Double getDescuento_adicionales() {
		return descuento_adicionales;
	}
	public void setDescuento_adicionales(Double descuento_adicionales) {
		this.descuento_adicionales = descuento_adicionales;
	}
	@Override
	public String toString() {
		return "PrestamoAdicional [id=" + id + ", prestamo=" + prestamo + ", prestamoDetalle=" + prestamoDetalle
				+ ", motivo=" + motivo + ", fecha=" + fecha + ", fecha_vencimiento=" + fecha_vencimiento + ", monto="
				+ monto + ", estado=" + estado + ", numeroCuota=" + numeroCuota + ", monto_pagado=" + monto_pagado
				+ ", descuento_adicionales=" + descuento_adicionales + ", nota=" + nota + ", usuario=" + usuario + "]";
	}
}
