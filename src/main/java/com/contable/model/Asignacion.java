package com.contable.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "asignaciones")
public class Asignacion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String Motivo;
	private Double Monto = 0.0;
	private Integer estado = 0;
	
	@OneToOne
	@JoinColumn(name = "id_empleado")
	private Empleado empleado;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getMotivo() {
		return Motivo;
	}
	public void setMotivo(String motivo) {
		Motivo = motivo;
	}
	public Double getMonto() {
		return Monto;
	}
	public void setMonto(Double monto) {
		Monto = monto;
	}
	public Integer getEstado() {
		return estado;
	}
	public void setEstado(Integer estado) {
		this.estado = estado;
	}
	public Empleado getEmpleado() {
		return empleado;
	}
	public void setEmpleado(Empleado empleado) {
		this.empleado = empleado;
	}
	@Override
	public String toString() {
		return "Asignacion [id=" + id + ", Motivo=" + Motivo + ", Monto=" + Monto + ", estado=" + estado + ", empleado="
				+ empleado + "]";
	}
}
