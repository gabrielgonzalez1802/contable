package com.contable.service;

import java.util.Date;
import java.util.List;

import com.contable.model.Carpeta;
import com.contable.model.Cliente;
import com.contable.model.Empresa;
import com.contable.model.Prestamo;

public interface IPrestamosService {
	Prestamo buscarPorId(Integer id);
	List<Prestamo> buscarPorEmpresaEstado(Empresa empresa, Integer estado);
	List<Prestamo> buscarPorEstado(Integer estado);
	List<Prestamo> buscarPorEstadoNotIn(List<Integer> estados);
	List<Prestamo> buscarPorEmpresaEstadoNotIn(Empresa empresa, List<Integer> estados);
	List<Prestamo> buscarPorEmpresaMonedaEstadoNotIn(Empresa empresa, String moneda, List<Integer> estados);
	List<Prestamo> buscarPorCliente(Cliente cliente);
	List<Prestamo> buscarPorCarpeta(Carpeta carpeta);
	List<Prestamo> buscarPorCarpetaEmpresa(Carpeta carpeta, Empresa empresa);
	List<Prestamo> buscarPorCarpetaEmpresaEstado(Carpeta carpeta, Empresa empresa, Integer estado);
	List<Prestamo> buscarPorCarpetaEmpresaMonedaEstadoNotIn(Carpeta carpeta, Empresa empresa, String moneda, List<Integer> estados);
	List<Prestamo> buscarPorClienteCarpetaEmpresaEstado(Cliente cliente, Carpeta carpeta, Empresa empresa, Integer estado);
	List<Prestamo> buscarPorClienteCarpetaEmpresaMonedaEstadoNotIn(Cliente cliente, Carpeta carpeta, Empresa empresa, String moneda, List<Integer> estados);
	List<Prestamo> buscarPorFecha(Date fecha);
	List<Prestamo> buscarPorTipo(String tipo);
	List<Prestamo> buscarPorClienteCarpetaPorFechaDesc(Cliente cliente, Carpeta carpeta);
	List<Prestamo> buscarPorClienteCarpetaEmpresaPorFechaDesc(Cliente cliente, Carpeta carpeta, Empresa empresa);
	List<Prestamo> buscarPorClienteCarpetaEmpresaFechasPorFechaDesc(Cliente cliente, Carpeta carpeta, Empresa empresa, Date desde, Date hasta);
	List<Prestamo> buscarPorCarpetaEmpresaFechasPorFechaDesc(Carpeta carpeta, Empresa empresa, Date desde, Date hasta);
	List<Prestamo> buscarPorCarpetaFechaDesc(Carpeta carpeta);
	List<Prestamo> buscarPorCarpetaFecha(Carpeta carpeta, Date date);
	void guardar(Prestamo prestamo);
	void eliminar(Integer id);
}
