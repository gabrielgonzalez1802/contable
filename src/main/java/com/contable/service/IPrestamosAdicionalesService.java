package com.contable.service;

import java.util.List;

import com.contable.model.Prestamo;
import com.contable.model.PrestamoAdicional;
import com.contable.model.PrestamoDetalle;

public interface IPrestamosAdicionalesService {
	List<PrestamoAdicional> buscarPorPrestamo(Prestamo prestamo);
	List<PrestamoAdicional> buscarPorPrestamoDetalle(PrestamoDetalle prestamoDetalle);
	void guardar(PrestamoAdicional prestamoAdicional);
	void eliminar(PrestamoAdicional prestamoAdicional);
}
