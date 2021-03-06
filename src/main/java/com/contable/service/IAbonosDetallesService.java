package com.contable.service;

import java.util.List;

import com.contable.model.Abono;
import com.contable.model.AbonoDetalle;

public interface IAbonosDetallesService {
	AbonoDetalle buscarPorId(Integer id);
	List<AbonoDetalle> buscarPorAbono(Abono abono);
	List<AbonoDetalle> buscarPorAbonoOrderByCuota(Abono abono);
	List<AbonoDetalle> buscarPorAbonoConcepto(Abono abono, String concepto);
	void guardar(AbonoDetalle abonoDetalle);
	void eliminar(AbonoDetalle abonoDetalle);
}
