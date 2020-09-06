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
	
	private Integer dias_gracia;
	private Double monto = 0.0;
	private Integer pagos;
	private Double tasa = 0.0;
	
	private Integer id_forma_pago; //Luego se llevara a instancia de la clase FormaPago
	
	private Double valor_cuota = 0.0;
	private Integer mora;
	private String observacion;
	private Double balance = 0.0;
	
	@Column(name = "valor_interes")
	private Double interes= 0.0;
	
	@Column(name = "total_a_pagar")
	private Double totalPagar;
	
	@Column(name = "ultimo_pago")
	private Date ultimoPago;
	
	@OneToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;
	
	private Integer estado;
	private String moneda;
	private Integer carpeta;
	
	private Integer id_cuenta; //Se llevara a objeto de la clase Cuenta

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

	public Integer getId_forma_pago() {
		return id_forma_pago;
	}

	public void setId_forma_pago(Integer id_forma_pago) {
		this.id_forma_pago = id_forma_pago;
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

	public Double getInteres() {
		return interes;
	}

	public void setInteres(Double interes) {
		this.interes = interes;
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

	public Integer getCarpeta() {
		return carpeta;
	}

	public void setCarpeta(Integer carpeta) {
		this.carpeta = carpeta;
	}

	public Integer getId_cuenta() {
		return id_cuenta;
	}

	public void setId_cuenta(Integer id_cuenta) {
		this.id_cuenta = id_cuenta;
	}
}
