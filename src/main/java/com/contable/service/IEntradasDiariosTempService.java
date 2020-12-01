package com.contable.service;

import java.util.List;

import com.contable.model.CuentaContable;
import com.contable.model.Empresa;
import com.contable.model.EntradaDiarioTemp;
import com.contable.model.Usuario;

public interface IEntradasDiariosTempService {
	EntradaDiarioTemp buscarPorId(Integer id);
	List<EntradaDiarioTemp> buscarPorEmpresaCuentaContable(Empresa empresa, CuentaContable cuentaContable);
	List<EntradaDiarioTemp> buscarPorCuentaContable(CuentaContable cuentaContable);
	List<EntradaDiarioTemp> buscarPorEmpresaUsuario(Empresa empresa, Usuario usuario);
	void guardar(EntradaDiarioTemp entradaDiarioTemp);
	void eliminar(EntradaDiarioTemp entradaDiarioTemp);
	void eliminar(List<EntradaDiarioTemp> entradasDiariosTemp);
}
