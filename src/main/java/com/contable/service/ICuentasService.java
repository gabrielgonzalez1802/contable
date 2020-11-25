package com.contable.service;

import java.util.List;

import com.contable.model.Carpeta;
import com.contable.model.Cuenta;
import com.contable.model.Empresa;

public interface ICuentasService {
	Cuenta buscarPorId(Integer id);
	List<Cuenta> buscarPorCarpeta(Carpeta carpeta);
	List<Cuenta> buscarPorCarpetaEmpresa(Carpeta carpeta, Empresa empresa);
	List<Cuenta> buscarPorCarpetaEmpresaMoneda(Carpeta carpeta, Empresa empresa, String moneda);
	void guardar(Cuenta cuenta);
	void eliminar(Cuenta cuenta);
}
