package com.contable.service;

import java.util.List;

import com.contable.model.Prestamo;
import com.contable.model.PrestamoDetalle;

public interface IPrestamosDetallesService {
	PrestamoDetalle buscarPorId(Integer id);
	List<PrestamoDetalle> buscarPorPrestamo(Prestamo prestamo);
	void guardar(PrestamoDetalle prestamoDetalle);
	void eliminar(Integer id);
	void eliminar(List<PrestamoDetalle> prestamoDetalles);
}
