package com.contable.model;

public class MoraDetalle {

	private Integer id;
	private Double mora;
	private Double descuento_mora;
	private Double mora_pagada;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Double getMora() {
		return mora;
	}
	public void setMora(Double mora) {
		this.mora = mora;
	}
	public Double getDescuento_mora() {
		return descuento_mora;
	}
	public void setDescuento_mora(Double descuento_mora) {
		this.descuento_mora = descuento_mora;
	}
	public Double getMora_pagada() {
		return mora_pagada;
	}
	public void setMora_pagada(Double mora_pagada) {
		this.mora_pagada = mora_pagada;
	}
	
	@Override
	public String toString() {
		return "MoraDetalle [id=" + id + ", mora=" + mora + ", descuento_mora=" + descuento_mora + ", mora_pagada="
				+ mora_pagada + "]";
	}
}
