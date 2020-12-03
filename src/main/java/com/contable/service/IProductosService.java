package com.contable.service;

import java.util.List;

import com.contable.model.Empresa;
import com.contable.model.Producto;

public interface IProductosService {
	Producto buscarPorId(Integer id);
	List<Producto> buscarPorEmpresa(Empresa empresa);
	List<Producto> buscarPorEmpresaActivoFijo(Empresa empresa, Integer activoFijo);
	void guardar(Producto producto);
	void eliminar(Producto producto);
}
