package com.contable.service;

import java.util.List;

import com.contable.model.Compra;
import com.contable.model.Empresa;
import com.contable.model.EntradaDiario;
import com.contable.model.Pago;
import com.contable.model.Usuario;

public interface IPagosService {
	Pago buscarPorId(Integer id);
	List<Pago> buscarPorEmpresa(Empresa empresa);
	List<Pago> buscarPorEmpresaUsuario(Empresa empresa, Usuario usuario);
	List<Pago> buscarPorEmpresaEntradaDiario(Empresa empresa, EntradaDiario entradaDiario);
	List<Pago> buscarPorEmpresaEntradaDiarioUsuario(Empresa empresa, EntradaDiario entradaDiario, Usuario usuario);
	List<Pago> buscarPorEmpresaCompra(Empresa empresa, Compra compra);
	void guardar(Pago pago);
	void eliminar(Pago pago);
}
