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

import org.springframework.web.multipart.MultipartFile;

@Entity
@Table(name = "abonos")
public class Abono {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@OneToOne
	@JoinColumn(name = "id_prestamo")
	private Prestamo prestamo;
	
	@Column(name = "abono_numero")
	private Integer numero;
	
	private Double monto = 0.0;
	
	private Date fecha;
	
	private Double efectivo = 0.0;
	private Double cheque = 0.0;
	private Double transferencia_deposito = 0.0;
	
	private String imagen_cheque;
	private String imagen_deposito_transferencia;
	
	@OneToOne
	@JoinColumn(name = "id_cliente")
	private Cliente cliente;
	
	@OneToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;
	
	@Transient 
	private MultipartFile imagen;
	
	@Transient
	private String nombreImagen;
	
	@Transient
	private Integer tipoPago;
	
	private String nota;
	private Integer estado = 0;
	
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
	public Integer getNumero() {
		return numero;
	}
	public void setNumero(Integer numero) {
		this.numero = numero;
	}
	public Double getMonto() {
		return monto;
	}
	public void setMonto(Double monto) {
		this.monto = monto;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public Cliente getCliente() {
		return cliente;
	}
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	public String getNota() {
		return nota;
	}
	public void setNota(String nota) {
		this.nota = nota;
	}
	public Integer getEstado() {
		return estado;
	}
	public void setEstado(Integer estado) {
		this.estado = estado;
	}
	public Integer getTipoPago() {
		return tipoPago;
	}
	public void setTipoPago(Integer tipoPago) {
		this.tipoPago = tipoPago;
	}
	public MultipartFile getImagen() {
		return imagen;
	}
	public void setImagen(MultipartFile imagen) {
		this.imagen = imagen;
	}
	public String getNombreImagen() {
		return nombreImagen;
	}
	public void setNombreImagen(String nombreImagen) {
		this.nombreImagen = nombreImagen;
	}
	public Double getEfectivo() {
		return efectivo;
	}
	public void setEfectivo(Double efectivo) {
		this.efectivo = efectivo;
	}
	public Double getCheque() {
		return cheque;
	}
	public void setCheque(Double cheque) {
		this.cheque = cheque;
	}
	public Double getTransferencia_deposito() {
		return transferencia_deposito;
	}
	public void setTransferencia_deposito(Double transferencia_deposito) {
		this.transferencia_deposito = transferencia_deposito;
	}
	public String getImagen_cheque() {
		return imagen_cheque;
	}
	public void setImagen_cheque(String imagen_cheque) {
		this.imagen_cheque = imagen_cheque;
	}
	public String getImagen_deposito_transferencia() {
		return imagen_deposito_transferencia;
	}
	public void setImagen_deposito_transferencia(String imagen_deposito_transferencia) {
		this.imagen_deposito_transferencia = imagen_deposito_transferencia;
	}
	@Override
	public String toString() {
		return "Abono [id=" + id + ", prestamo=" + prestamo + ", numero=" + numero + ", monto=" + monto + ", fecha="
				+ fecha + ", efectivo=" + efectivo + ", cheque=" + cheque + ", transferencia_deposito="
				+ transferencia_deposito + ", imagen_cheque=" + imagen_cheque + ", imagen_deposito_transferencia="
				+ imagen_deposito_transferencia + ", cliente=" + cliente + ", usuario=" + usuario + ", imagen=" + imagen
				+ ", nombreImagen=" + nombreImagen + ", tipoPago=" + tipoPago + ", nota=" + nota + ", estado=" + estado
				+ "]";
	}
}
