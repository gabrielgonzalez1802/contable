package com.contable.service;

import java.util.Date;
import java.util.List;

import com.contable.model.Prestamo;
import com.contable.model.PrestamoInteresDetalle;

public interface IPrestamosInteresesDetallesService {
	PrestamoInteresDetalle buscarPorId(Integer id);
	List<PrestamoInteresDetalle> buscarPorPrestamo(Prestamo prestamo);
	List<PrestamoInteresDetalle> buscarPorPrestamoFecha(Prestamo prestamo, Date fecha);
	List<PrestamoInteresDetalle> buscarPorEstadoPagoYEstado(Integer estadoPago, Integer estado);
	List<PrestamoInteresDetalle> buscarPorEstado(Integer estado);
	void guardar(PrestamoInteresDetalle prestamoInteresDetalle);
	void eliminar(PrestamoInteresDetalle prestamoInteresDetalle);
}
