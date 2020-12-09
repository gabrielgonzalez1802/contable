package com.contable.service;

import java.util.List;

import com.contable.model.Compra;
import com.contable.model.Empresa;
import com.contable.model.EntradaDiario;
import com.contable.model.PagoTemp;
import com.contable.model.Prestamo;
import com.contable.model.Usuario;

public interface IPagosTempService {
	PagoTemp buscarPorId(Integer id);
	List<PagoTemp> buscarPorPrestamo(Prestamo prestamo);
	void guardar(PagoTemp pagoTemp);
	void eliminar(PagoTemp pagoTemp);
	void eliminar(List<PagoTemp> pagosTemp);
	List<PagoTemp> buscarPorEmpresa(Empresa empresa);
	List<PagoTemp> buscarPorEmpresaUsuario(Empresa empresa, Usuario usuario);
	List<PagoTemp> buscarPorEmpresaEntradaDiario(Empresa empresa, EntradaDiario entradaDiario);
	List<PagoTemp> buscarPorEmpresaEntradaDiarioUsuario(Empresa empresa, EntradaDiario entradaDiario, Usuario usuario);
	List<PagoTemp> buscarPorEmpresaCompra(Empresa empresa, Compra compra);
}
