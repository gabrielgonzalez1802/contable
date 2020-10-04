package com.contable.service;

import java.util.List;

import com.contable.model.Cliente;

public interface IClientesService {
	Cliente buscarPorId(Integer id);
	List<Cliente> buscarTodos();
	List<Cliente> buscarPorEstado(Integer estado);
	List<Cliente> buscarPorCedula(String cedula);
	List<Cliente> buscarPorPasaporte(String pasaporte);
	void guardar(Cliente cliente);
	void eliminar(Integer idCliente);
	void eliminar(Cliente cliente);
}