package com.contable.service;

import java.util.Date;
import java.util.List;

import com.contable.model.Carpeta;
import com.contable.model.Cliente;
import com.contable.model.Prestamo;

public interface IPrestamosService {
	Prestamo buscarPorId(Integer id);
	List<Prestamo> buscarPorEstado(Integer estado);
	List<Prestamo> buscarPorCliente(Cliente cliente);
	List<Prestamo> buscarPorCarpeta(Carpeta carpeta);
	List<Prestamo> buscarPorFecha(Date fecha);
	List<Prestamo> buscarPorClienteCarpetaPorFechaDesc(Cliente cliente, Carpeta carpeta);
	void guardar(Prestamo prestamo);
	void eliminar(Integer id);
}
