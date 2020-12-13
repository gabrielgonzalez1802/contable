package com.contable.service;

import java.util.List;

import com.contable.model.Compra;
import com.contable.model.CompraPago;
import com.contable.model.Empresa;
import com.contable.model.Usuario;

public interface IComprasPagosService {
	CompraPago buscarPorId(Integer id);
	List<CompraPago> buscarPorEmpresa(Empresa empresa);
	List<CompraPago> buscarPorEmpresaUsuarioCompra(Empresa empresa, Usuario usuario, Compra compra);
	List<CompraPago> buscarPorEmpresaCompra(Empresa empresa, Compra compra);
	void guardar(CompraPago CompraPago);
	void eliminar(CompraPago CompraPago);
	void eliminar(List<CompraPago> comprasPagos);
}
