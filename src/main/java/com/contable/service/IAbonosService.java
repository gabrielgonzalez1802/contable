package com.contable.service;

import java.util.List;

import com.contable.model.Abono;
import com.contable.model.Prestamo;

public interface IAbonosService {
	Abono buscarPorId(Integer id);
	List<Abono> buscarPorPrestamo(Prestamo prestamo);
	List<Abono> buscarPorPrestamoEstado(Prestamo prestamo, Integer estado);
	List<Abono> buscarPorPrestamosOrderByAbono(List<Prestamo> prestamos);
	void guardar(Abono abono);
	void eliminar(Abono abono);
}
