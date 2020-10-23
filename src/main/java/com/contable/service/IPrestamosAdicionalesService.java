package com.contable.service;

import java.util.List;

import com.contable.model.Prestamo;
import com.contable.model.PrestamoAdicional;
import com.contable.model.PrestamoDetalle;

public interface IPrestamosAdicionalesService {
	PrestamoAdicional buscarPorId(Integer id);
	List<PrestamoAdicional> buscarPorPrestamo(Prestamo prestamo);
	List<PrestamoAdicional> buscarPorPrestamoDetalle(PrestamoDetalle prestamoDetalle);
	List<PrestamoAdicional> buscarPorPrestamoEstado(Prestamo prestamo, Integer estado);
	List<PrestamoAdicional> buscarPorPrestamoNumeroCuota(Prestamo prestamo, Integer numeroCuota);
	void guardar(PrestamoAdicional prestamoAdicional);
	void eliminar(PrestamoAdicional prestamoAdicional);
}
