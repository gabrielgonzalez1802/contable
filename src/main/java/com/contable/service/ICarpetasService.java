package com.contable.service;

import java.util.List;

import com.contable.model.Carpeta;

public interface ICarpetasService {
	Carpeta buscarPorId(Integer id);
	Carpeta buscarPorNombre(String nombre);
	List<Carpeta> buscarTipoCarpeta(Integer id);
	void guardar(Carpeta carpeta);
	void eliminar(Carpeta carpeta);
}
