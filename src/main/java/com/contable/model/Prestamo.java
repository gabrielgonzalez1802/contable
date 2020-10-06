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
@Table(name = "prestamos")
public class Prestamo {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private Date fecha = new Date();
	
	@OneToOne
	@JoinColumn(name = "id_cliente")
	private Cliente cliente;
	
	private String tipo;
	private String tipo_prestamo;
	
	private Integer dias_gracia = 0;
	private Double monto = 0.0;
	private Integer pagos = 0;
	private Double tasa = 0.0;
	
	private String forma_pago;
	
	private Double valor_cuota = 0.0;
	private Integer mora;
	private String observacion;
	private Double balance = 0.0;
	
	private Double valor_interes= 0.0;
	
	@Column(name = "total_a_pagar")
	private Double totalPagar = 0.0;
	
	@Column(name = "ultimo_pago")
	private Date ultimoPago;
	
	@OneToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;
	
	private Integer estado;
	private String moneda;
	
	@OneToOne
	@JoinColumn(name = "carpeta")
	private Carpeta carpeta;
	
	@OneToOne
	@JoinColumn(name = "id_cuenta")
	private Cuenta cuenta; 

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getTipo_prestamo() {
		return tipo_prestamo;
	}

	public void setTipo_prestamo(String tipo_prestamo) {
		this.tipo_prestamo = tipo_prestamo;
	}

	public Integer getDias_gracia() {
		return dias_gracia;
	}

	public void setDias_gracia(Integer dias_gracia) {
		this.dias_gracia = dias_gracia;
	}

	public Double getMonto() {
		return monto;
	}

	public void setMonto(Double monto) {
		this.monto = monto;
	}

	public Integer getPagos() {
		return pagos;
	}

	public void setPagos(Integer pagos) {
		this.pagos = pagos;
	}

	public Double getTasa() {
		return tasa;
	}

	public void setTasa(Double tasa) {
		this.tasa = tasa;
	}
	public Double getValor_cuota() {
		return valor_cuota;
	}

	public void setValor_cuota(Double valor_cuota) {
		this.valor_cuota = valor_cuota;
	}

	public Integer getMora() {
		return mora;
	}

	public void setMora(Integer mora) {
		this.mora = mora;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}
	
	public Double getValor_interes() {
		return valor_interes;
	}

	public void setValor_interes(Double valor_interes) {
		this.valor_interes = valor_interes;
	}

	public Double getTotalPagar() {
		return totalPagar;
	}

	public void setTotalPagar(Double totalPagar) {
		this.totalPagar = totalPagar;
	}

	public Date getUltimoPago() {
		return ultimoPago;
	}

	public void setUltimoPago(Date ultimoPago) {
		this.ultimoPago = ultimoPago;
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

	public String getMoneda() {
		return moneda;
	}

	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}

	public String getForma_pago() {
		return forma_pago;
	}

	public void setForma_pago(String forma_pago) {
		this.forma_pago = forma_pago;
	}

	public Carpeta getCarpeta() {
		return carpeta;
	}

	public void setCarpeta(Carpeta carpeta) {
		this.carpeta = carpeta;
	}

	public Cuenta getCuenta() {
		return cuenta;
	}

	public void setCuenta(Cuenta cuenta) {
		this.cuenta = cuenta;
	}

	@Override
	public String toString() {
		return "Prestamo [id=" + id + ", fecha=" + fecha + ", cliente=" + cliente + ", tipo=" + tipo
				+ ", tipo_prestamo=" + tipo_prestamo + ", dias_gracia=" + dias_gracia + ", monto=" + monto + ", pagos="
				+ pagos + ", tasa=" + tasa + ", forma_pago=" + forma_pago + ", valor_cuota=" + valor_cuota + ", mora="
				+ mora + ", observacion=" + observacion + ", balance=" + balance + ", valor_interes=" + valor_interes
				+ ", totalPagar=" + totalPagar + ", ultimoPago=" + ultimoPago + ", usuario=" + usuario + ", estado="
				+ estado + ", moneda=" + moneda + ", carpeta=" + carpeta + ", cuenta=" + cuenta + "]";
	}
}
