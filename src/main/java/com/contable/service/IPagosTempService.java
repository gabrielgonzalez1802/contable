package com.contable.service;

import java.util.List;

import com.contable.model.PagoTemp;
import com.contable.model.Prestamo;

public interface IPagosTempService {
	PagoTemp buscarPorId(Integer id);
	List<PagoTemp> buscarPorPrestamo(Prestamo prestamo);
	void guardar(PagoTemp pagoTemp);
	void eliminar(PagoTemp pagoTemp);
	void eliminar(List<PagoTemp> pagosTemp);
}
