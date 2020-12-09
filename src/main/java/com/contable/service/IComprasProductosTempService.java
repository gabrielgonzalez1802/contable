package com.contable.service;

import java.util.List;

import com.contable.model.CompraProductoTemp;
import com.contable.model.Empresa;
import com.contable.model.Producto;
import com.contable.model.Usuario;

public interface IComprasProductosTempService {
	CompraProductoTemp buscarPorId(Integer id);
	List<CompraProductoTemp> buscarPorEmpresaUsuario(Empresa empresa, Usuario usuario);
	List<CompraProductoTemp> buscarPorEmpresaUsuarioProducto(Empresa empresa, Usuario usuario, Producto producto);
	List<CompraProductoTemp> buscarPorEmpresa(Empresa empresa);
	void guardar(CompraProductoTemp compraProductoTemp);
	void eliminar(CompraProductoTemp compraProductoTemp);
	void eliminar(List<CompraProductoTemp> comprasProductosTemp);
}
