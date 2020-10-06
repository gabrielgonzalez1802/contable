package com.contable.service;

import java.util.List;

import com.contable.model.Carpeta;
import com.contable.model.Cuenta;

public interface ICuentasService {
	Cuenta buscarPorId(Integer id);
	List<Cuenta> buscarPorCarpeta(Carpeta carpeta);
	void guardar(Cuenta cuenta);
	void eliminar(Cuenta cuenta);
}
