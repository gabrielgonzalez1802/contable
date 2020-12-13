package com.contable.service;

import java.util.List;

import com.contable.model.Compra;
import com.contable.model.CompraItbis;

public interface IComprasItbisService {
	CompraItbis buscarPorId(Integer id);
	List<CompraItbis> buscarPorCompra(Compra compra);
	void guardar(CompraItbis compraItbis);
	void eliminar(CompraItbis compraItbis);
}
