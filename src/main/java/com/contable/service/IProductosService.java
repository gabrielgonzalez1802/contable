package com.contable.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.contable.model.CuentaContable;
import com.contable.model.Empresa;
import com.contable.model.Producto;

public interface IProductosService {
	Producto buscarPorId(Integer id);
	List<Producto> buscarPorEmpresa(Empresa empresa);
	Page<Producto> buscarPorEmpresa(Empresa empresa, Pageable pageable);
	List<Producto> buscarPorEmpresaCuentaContable(Empresa empresa, CuentaContable cuentaContable);
	Page<Producto> buscarPorEmpresaContainingOrderByNombre(Empresa empresa, String nombre, Pageable pageable);
	Page<Producto> buscarPorEmpresaNotInActivosFijosContainingOrderByNombre(Empresa empresa, List<Integer> activosFijos, String nombre, Pageable pageable);
	List<Producto> buscarPorEmpresaActivoFijo(Empresa empresa, Integer activoFijo);
	List<Producto> buscarPorEmpresaActivosFijos(Empresa empresa, List<Integer> activoFijo);
	Page<Producto> buscarPorEmpresaListActivosFijos(Empresa empresa, List<Integer> activoFijo, Pageable pageable);
	Page<Producto> buscarPorEmpresaActivoFijo(Empresa empresa, Integer activoFijo, Pageable pageable);
	Page<Producto> buscarPorEmpresaActivoFijoContainingNombre(Empresa empresa, Integer activoFijo, String nombre, Pageable pageable);
	void guardar(Producto producto);
	void eliminar(Producto producto);
}
