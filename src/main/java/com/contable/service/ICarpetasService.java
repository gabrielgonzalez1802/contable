package com.contable.service;

import java.util.List;

import com.contable.model.Carpeta;
import com.contable.model.Empresa;

public interface ICarpetasService {
	Carpeta buscarPorId(Integer id);
	Carpeta buscarPorNombre(String nombre);
	List<Carpeta> buscarTipoCarpeta(Integer id);
	Carpeta buscarPorNombreEmpresa(String nombre, Empresa empresa);
	List<Carpeta> buscarPorEmpresa(Empresa empresa);
	List<Carpeta> buscarTipoCarpetaEmpresa(Integer id, Empresa empresa);
	void guardar(Carpeta carpeta);
	void eliminar(Carpeta carpeta);
}
