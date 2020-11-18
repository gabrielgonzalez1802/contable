package com.contable.service;

import java.util.List;

import com.contable.model.CuentaContable;
import com.contable.model.Diario;
import com.contable.model.EntradaDiario;

public interface IEntradasDiariosService {
	EntradaDiario buscarPorId(Integer id);
	List<EntradaDiario> buscarPorCuentaContable(CuentaContable cuentaContable);
	List<EntradaDiario> buscarPorDiario(Diario diario);
	List<EntradaDiario> buscarPorCuentaContableDiario(CuentaContable cuentaContable, Diario diario);
	void guardar(EntradaDiario entradaDiario);
	void eliminar(EntradaDiario entradaDiario);
}
