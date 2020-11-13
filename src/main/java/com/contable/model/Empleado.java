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
import javax.persistence.Transient;

@Entity
@Table(name = "empleados")
public class Empleado {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String nombre;
	private String apellido;
	private String direccion;
	private String cedula;
	private String telefono;
	
	@Column(name = "numero_emergencia")
	private String numeroEmergencia;
	
	private String puesto;
	private Double sueldo = 0.0;
	
	@Column(name = "fecha_nacimiento")
	private Date fechaNacimiento;
	
	@Column(name = "fecha_ingreso")
	private Date fechaIngreso;
	
	@Transient
	private Double totalNeto = 0.0;
	
	@Transient
	private Double totalAsignaciones = 0.0;
	
	@Transient
	private Double totalDeducciones = 0.0;
	
	@OneToOne
	@JoinColumn(name = "id_empresa")
	private Empresa empresa;
	
	private Integer estado = 1;
	
	private String recurrencia = "QUINCENAL";

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getCedula() {
		return cedula;
	}

	public void setCedula(String cedula) {
		this.cedula = cedula;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getNumeroEmergencia() {
		return numeroEmergencia;
	}

	public void setNumeroEmergencia(String numeroEmergencia) {
		this.numeroEmergencia = numeroEmergencia;
	}

	public String getPuesto() {
		return puesto;
	}

	public void setPuesto(String puesto) {
		this.puesto = puesto;
	}

	public Double getSueldo() {
		return sueldo;
	}

	public void setSueldo(Double sueldo) {
		this.sueldo = sueldo;
	}

	public Date getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(Date fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}

	public Date getFechaIngreso() {
		return fechaIngreso;
	}

	public void setFechaIngreso(Date fechaIngreso) {
		this.fechaIngreso = fechaIngreso;
	}

	public Integer getEstado() {
		return estado;
	}

	public void setEstado(Integer estado) {
		this.estado = estado;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public String getRecurrencia() {
		return recurrencia;
	}

	public void setRecurrencia(String recurrencia) {
		this.recurrencia = recurrencia;
	}

	public Double getTotalNeto() {
		return totalNeto;
	}

	public void setTotalNeto(Double totalNeto) {
		this.totalNeto = totalNeto;
	}

	public Double getTotalAsignaciones() {
		return totalAsignaciones;
	}

	public void setTotalAsignaciones(Double totalAsignaciones) {
		this.totalAsignaciones = totalAsignaciones;
	}

	public Double getTotalDeducciones() {
		return totalDeducciones;
	}

	public void setTotalDeducciones(Double totalDeducciones) {
		this.totalDeducciones = totalDeducciones;
	}

	@Override
	public String toString() {
		return "Empleado [id=" + id + ", nombre=" + nombre + ", apellido=" + apellido + ", direccion=" + direccion
				+ ", cedula=" + cedula + ", telefono=" + telefono + ", numeroEmergencia=" + numeroEmergencia
				+ ", puesto=" + puesto + ", sueldo=" + sueldo + ", fechaNacimiento=" + fechaNacimiento
				+ ", fechaIngreso=" + fechaIngreso + ", totalNeto=" + totalNeto + ", totalAsignaciones="
				+ totalAsignaciones + ", totalDeducciones=" + totalDeducciones + ", empresa=" + empresa + ", estado="
				+ estado + ", recurrencia=" + recurrencia + "]";
	}
}
