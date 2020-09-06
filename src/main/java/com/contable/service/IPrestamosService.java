package com.contable.service;

import java.util.Date;
import java.util.List;

import com.contable.model.Cliente;
import com.contable.model.Prestamo;

public interface IPrestamosService {
	Prestamo buscarPorId(Integer id);
	List<Prestamo> buscarPorEstado(Integer estado);
	List<Prestamo> buscarPorCliente(Cliente cliente);
	List<Prestamo> buscarPorFecha(Date fecha);
	void guardar(Prestamo prestamo);
	void eliminar(Integer id);
}
