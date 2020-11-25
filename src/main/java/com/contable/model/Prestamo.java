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
@Table(name = "prestamos")
public class Prestamo {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private Date fecha = new Date();
	
	@OneToOne
	@JoinColumn(name = "id_cliente")
	private Cliente cliente;
	
	@OneToOne
	@JoinColumn(name = "id_empresa")
	private Empresa empresa;
	
	@Transient
	private Integer idClienteTemp = 0;
	
	@Transient
	private Integer idCarpetaTemp = 0;
	
	@Transient
	private Integer idCuentaTemp = 0;
	
	@Transient
	private String fechaTemp;
	
	private String tipo;
	
	@OneToOne
	@JoinColumn(name = "id_prestamo_tipo")
	private PrestamoTipo prestamoTipo;
	
	private Integer dias_gracia = 0;
	private Double monto = 0.0;
	private Integer pagos = 0;
	private Double tasa = 0.0;
	private Integer codigo;
	
	private String forma_pago;
	
	private Double valor_cuota = 0.0;
	private Double mora;
	private String observacion;
	private Double balance = 0.0;
	
	private Double valor_interes= 0.0;
		
	@Column(name = "ultimo_pago")
	private Date ultimoPago;
	
	private Date fecha_cron;
	
	@Transient
	private Integer numeroNota =0;
	
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
	
	private Double total_cuota = 0.0;
	private Double total_capital = 0.0;
	private Double total_interes = 0.0;
	private Double total_neto = 0.0;
	private Double gastos_cierre = 0.0;
	
	private Integer cantidad_pagos = 0;
	
	@Column(name = "capital_pagado")
	private Double capitalPagado = 0.0;

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

	public Double getMora() {
		return mora;
	}

	public void setMora(Double mora) {
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

	public Integer getIdClienteTemp() {
		return idClienteTemp;
	}

	public void setIdClienteTemp(Integer idClienteTemp) {
		this.idClienteTemp = idClienteTemp;
	}

	public Integer getIdCarpetaTemp() {
		return idCarpetaTemp;
	}

	public void setIdCarpetaTemp(Integer idCarpetaTemp) {
		this.idCarpetaTemp = idCarpetaTemp;
	}

	public Integer getIdCuentaTemp() {
		return idCuentaTemp;
	}

	public void setIdCuentaTemp(Integer idCuentaTemp) {
		this.idCuentaTemp = idCuentaTemp;
	}
	
	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getFechaTemp() {
		return fechaTemp;
	}

	public void setFechaTemp(String fechaTemp) {
		this.fechaTemp = fechaTemp;
	}

	public Double getTotal_cuota() {
		return total_cuota;
	}

	public void setTotal_cuota(Double total_cuota) {
		this.total_cuota = total_cuota;
	}

	public Double getTotal_capital() {
		return total_capital;
	}

	public void setTotal_capital(Double total_capital) {
		this.total_capital = total_capital;
	}

	public Double getTotal_interes() {
		return total_interes;
	}

	public void setTotal_interes(Double total_interes) {
		this.total_interes = total_interes;
	}

	public Double getTotal_neto() {
		return total_neto;
	}

	public void setTotal_neto(Double total_neto) {
		this.total_neto = total_neto;
	}

	public Double getGastos_cierre() {
		return gastos_cierre;
	}

	public void setGastos_cierre(Double gastos_cierre) {
		this.gastos_cierre = gastos_cierre;
	}

	public Integer getCantidad_pagos() {
		return cantidad_pagos;
	}

	public void setCantidad_pagos(Integer cantidad_pagos) {
		this.cantidad_pagos = cantidad_pagos;
	}

	public Date getFecha_cron() {
		return fecha_cron;
	}

	public void setFecha_cron(Date fecha_cron) {
		this.fecha_cron = fecha_cron;
	}

	public Double getCapitalPagado() {
		return capitalPagado;
	}

	public void setCapitalPagado(Double capitalPagado) {
		this.capitalPagado = capitalPagado;
	}

	public Integer getNumeroNota() {
		return numeroNota;
	}

	public void setNumeroNota(Integer numeroNota) {
		this.numeroNota = numeroNota;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public PrestamoTipo getPrestamoTipo() {
		return prestamoTipo;
	}

	public void setPrestamoTipo(PrestamoTipo prestamoTipo) {
		this.prestamoTipo = prestamoTipo;
	}

	@Override
	public String toString() {
		return "Prestamo [id=" + id + ", fecha=" + fecha + ", cliente=" + cliente + ", empresa=" + empresa
				+ ", idClienteTemp=" + idClienteTemp + ", idCarpetaTemp=" + idCarpetaTemp + ", idCuentaTemp="
				+ idCuentaTemp + ", fechaTemp=" + fechaTemp + ", tipo=" + tipo + ", prestamoTipo=" + prestamoTipo
				+ ", dias_gracia=" + dias_gracia + ", monto=" + monto + ", pagos=" + pagos + ", tasa=" + tasa
				+ ", codigo=" + codigo + ", forma_pago=" + forma_pago + ", valor_cuota=" + valor_cuota + ", mora="
				+ mora + ", observacion=" + observacion + ", balance=" + balance + ", valor_interes=" + valor_interes
				+ ", ultimoPago=" + ultimoPago + ", fecha_cron=" + fecha_cron + ", numeroNota=" + numeroNota
				+ ", usuario=" + usuario + ", estado=" + estado + ", moneda=" + moneda + ", carpeta=" + carpeta
				+ ", cuenta=" + cuenta + ", total_cuota=" + total_cuota + ", total_capital=" + total_capital
				+ ", total_interes=" + total_interes + ", total_neto=" + total_neto + ", gastos_cierre=" + gastos_cierre
				+ ", cantidad_pagos=" + cantidad_pagos + ", capitalPagado=" + capitalPagado + "]";
	}
}
