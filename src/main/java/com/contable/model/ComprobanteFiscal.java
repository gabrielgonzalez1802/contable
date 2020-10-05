package com.contable.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "comprobante_fiscales")
public class ComprobanteFiscal {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private Integer secuencia;
	private String nombre;
	private Integer paga_itbis;
	private String secuencia_inicial;
	private String secuencia_final;
	private String secuencia_actual;
	private Integer rnc;
	private String prefijo;
	private Integer incluye_itbis;
	private Integer valor_itbis;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getSecuencia() {
		return secuencia;
	}
	public void setSecuencia(Integer secuencia) {
		this.secuencia = secuencia;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public Integer getPaga_itbis() {
		return paga_itbis;
	}
	public void setPaga_itbis(Integer paga_itbis) {
		this.paga_itbis = paga_itbis;
	}
	public String getSecuencia_inicial() {
		return secuencia_inicial;
	}
	public void setSecuencia_inicial(String secuencia_inicial) {
		this.secuencia_inicial = secuencia_inicial;
	}
	public String getSecuencia_final() {
		return secuencia_final;
	}
	public void setSecuencia_final(String secuencia_final) {
		this.secuencia_final = secuencia_final;
	}
	public String getSecuencia_actual() {
		return secuencia_actual;
	}
	public void setSecuencia_actual(String secuencia_actual) {
		this.secuencia_actual = secuencia_actual;
	}
	public Integer getRnc() {
		return rnc;
	}
	public void setRnc(Integer rnc) {
		this.rnc = rnc;
	}
	public String getPrefijo() {
		return prefijo;
	}
	public void setPrefijo(String prefijo) {
		this.prefijo = prefijo;
	}
	public Integer getIncluye_itbis() {
		return incluye_itbis;
	}
	public void setIncluye_itbis(Integer incluye_itbis) {
		this.incluye_itbis = incluye_itbis;
	}
	public Integer getValor_itbis() {
		return valor_itbis;
	}
	public void setValor_itbis(Integer valor_itbis) {
		this.valor_itbis = valor_itbis;
	}
	@Override
	public String toString() {
		return "ComprobanteFiscal [id=" + id + ", secuencia=" + secuencia + ", nombre=" + nombre + ", paga_itbis="
				+ paga_itbis + ", secuencia_inicial=" + secuencia_inicial + ", secuencia_final=" + secuencia_final
				+ ", secuencia_actual=" + secuencia_actual + ", rnc=" + rnc + ", prefijo=" + prefijo
				+ ", incluye_itbis=" + incluye_itbis + ", valor_itbis=" + valor_itbis + "]";
	}
}
