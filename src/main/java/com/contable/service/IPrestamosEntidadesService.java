package com.contable.service;

import java.util.List;

import com.contable.model.Empresa;
import com.contable.model.Inversionista;
import com.contable.model.PrestamoEntidad;

public interface IPrestamosEntidadesService {
	PrestamoEntidad buscarPorId(Integer id);
	List<PrestamoEntidad> buscarPorEmpresa(Empresa empresa);
	List<PrestamoEntidad> buscarPorEmpresaInversionista(Empresa empresa, Inversionista inversionista);
	void guardar(PrestamoEntidad prestamoEntidad);
	void eliminar(PrestamoEntidad prestamoEntidad);
}
