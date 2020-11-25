package com.contable.service;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.contable.model.Carpeta;
import com.contable.model.Cliente;
import com.contable.model.DescuentoDetalle;
import com.contable.model.Empresa;
import com.contable.model.Prestamo;

public interface IDescuentosDetallesService {
	DescuentoDetalle buscarPorId(Integer id);
	List<DescuentoDetalle> buscarPorEmpresaCarpeta(Empresa empresa, Carpeta carpeta);
	List<DescuentoDetalle> buscarPorEmpresaCarpetaCliente(Empresa empresa, Carpeta carpeta, Cliente cliente);
	List<DescuentoDetalle> buscarPorEmpresaCarpetaFechas(Empresa empresa, Carpeta carpeta, Date desde, Date hasta);
	List<DescuentoDetalle> buscarPorEmpresaCarpetaPrestamos(Empresa empresa, Carpeta carpeta, LinkedList<Prestamo> prestamos);
	void guardar(DescuentoDetalle descuentoDetalle);
	void eliminar(DescuentoDetalle descuentoDetalle);
}
