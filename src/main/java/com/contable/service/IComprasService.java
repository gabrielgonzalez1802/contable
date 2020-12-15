package com.contable.service;

import java.util.Date;
import java.util.List;

import com.contable.model.Compra;
import com.contable.model.CuentaContable;
import com.contable.model.Empresa;
import com.contable.model.Suplidor;

public interface IComprasService {
	Compra buscarPorId(Integer id);
	List<Compra> buscarPorEmpresa(Empresa empresa);
	List<Compra> buscarPorEmpresaTotalMayorque(Empresa empresa, double valor);
	List<Compra> buscarPorEmpresaCuentaContable(Empresa empresa, CuentaContable cuentaContable);
	List<Compra> buscarPorEmpresaCuentaContableSuplidor(Empresa empresa, CuentaContable cuentaContable, Suplidor suplidor);
	List<Compra> buscarPorEmpresaFechas(Empresa empresa, Date desde, Date hasta);
	List<Compra> buscarPorEmpresaCuentaContableFechas(Empresa empresa, CuentaContable cuentaContable, Date desde, Date hasta);
	void guardar(Compra compra);
	void eliminar(Compra compra);
}
