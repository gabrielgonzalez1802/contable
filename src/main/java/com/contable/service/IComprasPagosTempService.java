package com.contable.service;

import java.util.List;

import com.contable.model.Compra;
import com.contable.model.CompraPagoTemp;
import com.contable.model.Empresa;
import com.contable.model.Usuario;

public interface IComprasPagosTempService {
	CompraPagoTemp buscarPorId(Integer id);
	List<CompraPagoTemp> buscarPorEmpresaUsuarioCompra(Empresa empresa, Usuario usuario, Compra compra);
	List<CompraPagoTemp> buscarPorEmpresaCompra(Empresa empresa, Compra compra);
	void guardar(CompraPagoTemp compraPagoTemp);
	void eliminar(CompraPagoTemp compraPagoTemp);
	void eliminar(List<CompraPagoTemp> comprasPagosTemp);
}
