package com.contable.service;

import java.util.Date;
import java.util.List;

import com.contable.model.Nota;
import com.contable.model.Prestamo;
import com.contable.model.PrestamoDetalle;

public interface INotasService {
	Nota buscarPorId(Integer id);
	List<Nota> buscarPorPrestamo(Prestamo prestamo);
	List<Nota> buscarPorPrestamoDetalle(PrestamoDetalle prestamoDetalle);
	List<Nota> buscarPorTipo(Integer tipo);
	List<Nota> buscarPorPrestamoFecha(Prestamo prestamo, Date fecha);
	void guardar(Nota nota);
	void eliminar(Nota nota);
}
