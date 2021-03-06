package com.contable.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.web.multipart.MultipartFile;

@Entity
@Table(name = "productos")
public class Producto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String nombre;
	private Double costo = 0.0;
	
	private Integer exento = 0;
	
	@Column(name = "precio_venta")
	private Double precioVenta = 0.0;
	
	private String imagen;
	
	@Transient
	private MultipartFile imagenTemp;
	
	@Column(name = "activo_fijo")
	private Integer activoFijo;
	
	@OneToOne
	@JoinColumn(name = "id_cuenta_contable")
	private CuentaContable cuentaContable;
	
	@OneToOne
	@JoinColumn(name = "id_empresa")
	private Empresa empresa;
	
	private Integer cantidad = 0;

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

	public Double getCosto() {
		return costo;
	}

	public void setCosto(Double costo) {
		this.costo = costo;
	}

	public Double getPrecioVenta() {
		return precioVenta;
	}

	public void setPrecioVenta(Double precioVenta) {
		this.precioVenta = precioVenta;
	}

	public String getImagen() {
		return imagen;
	}

	public void setImagen(String imagen) {
		this.imagen = imagen;
	}

	public MultipartFile getImagenTemp() {
		return imagenTemp;
	}

	public void setImagenTemp(MultipartFile imagenTemp) {
		this.imagenTemp = imagenTemp;
	}

	public Integer getActivoFijo() {
		return activoFijo;
	}

	public void setActivoFijo(Integer activoFijo) {
		this.activoFijo = activoFijo;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}

	public CuentaContable getCuentaContable() {
		return cuentaContable;
	}

	public void setCuentaContable(CuentaContable cuentaContable) {
		this.cuentaContable = cuentaContable;
	}

	public Integer getExento() {
		return exento;
	}

	public void setExento(Integer exento) {
		this.exento = exento;
	}

	@Override
	public String toString() {
		return "Producto [id=" + id + ", nombre=" + nombre + ", costo=" + costo + ", exento=" + exento
				+ ", precioVenta=" + precioVenta + ", imagen=" + imagen + ", imagenTemp=" + imagenTemp + ", activoFijo="
				+ activoFijo + ", cuentaContable=" + cuentaContable + ", empresa=" + empresa + ", cantidad=" + cantidad
				+ "]";
	}
}
