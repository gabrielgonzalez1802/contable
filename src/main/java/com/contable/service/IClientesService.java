package com.contable.service;

import java.util.List;

import com.contable.model.Cliente;
import com.contable.model.Empresa;

public interface IClientesService {
	Cliente buscarPorId(Integer id);
	List<Cliente> buscarTodos();
	List<Cliente> buscarPorEmpresa(Empresa empresa);
	List<Cliente> buscarPorEstado(Integer estado);
	Cliente buscarPorCedula(String cedula);
	Cliente buscarPorCedulaEmpresa(String item, Empresa empresa);
	Cliente buscarPorOtro(String pasaporte);
	Cliente buscarPorOtroEmpresa(String item, Empresa empresa);
	List<Cliente> buscarPorNombre(String nombre);
	List<Cliente> buscarPorNombreEmpresa(String nombre, Empresa empresa);
	List<Cliente> buscarPorEmpresaEstado(Empresa empresa, Integer estado);
	void guardar(Cliente cliente);
	void eliminar(Integer idCliente);
	void eliminar(Cliente cliente);
}