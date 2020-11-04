package com.contable.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "notas_prestamos")
public class Nota {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String nota;
	private Date fecha;
	
	@OneToOne
	@JoinColumn(name = "id_prestamo")
	private Prestamo prestamo;
	
	@OneToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;
	
	@OneToOne
	@JoinColumn(name = "id_prestamo_interes_detalle")
	private PrestamoInteresDetalle prestamoInteresDetalle;
	
	@OneToOne
	@JoinColumn(name = "id_prestamo_detalle")
	private PrestamoDetalle prestamoDetalle;
	
	@Transient
	private Integer numeroNota = 0;
	
	private Integer tipo = 1;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNota() {
		return nota;
	}

	public void setNota(String nota) {
		this.nota = nota;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public Prestamo getPrestamo() {
		return prestamo;
	}

	public void setPrestamo(Prestamo prestamo) {
		this.prestamo = prestamo;
	}

	public PrestamoInteresDetalle getPrestamoInteresDetalle() {
		return prestamoInteresDetalle;
	}

	public void setPrestamoInteresDetalle(PrestamoInteresDetalle prestamoInteresDetalle) {
		this.prestamoInteresDetalle = prestamoInteresDetalle;
	}

	public PrestamoDetalle getPrestamoDetalle() {
		return prestamoDetalle;
	}

	public void setPrestamoDetalle(PrestamoDetalle prestamoDetalle) {
		this.prestamoDetalle = prestamoDetalle;
	}

	public Integer getTipo() {
		return tipo;
	}

	public void setTipo(Integer tipo) {
		this.tipo = tipo;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Integer getNumeroNota() {
		return numeroNota;
	}

	public void setNumeroNota(Integer numeroNota) {
		this.numeroNota = numeroNota;
	}

	@Override
	public String toString() {
		return "Nota [id=" + id + ", nota=" + nota + ", fecha=" + fecha + ", prestamo=" + prestamo + ", usuario="
				+ usuario + ", prestamoInteresDetalle=" + prestamoInteresDetalle + ", prestamoDetalle="
				+ prestamoDetalle + ", numeroNota=" + numeroNota + ", tipo=" + tipo + "]";
	}
}
