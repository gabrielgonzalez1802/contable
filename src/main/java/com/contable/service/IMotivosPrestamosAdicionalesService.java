package com.contable.service;

import java.util.List;

import com.contable.model.MotivoPrestamoAdicional;

public interface IMotivosPrestamosAdicionalesService {
	MotivoPrestamoAdicional buscarPorId(Integer id);
	MotivoPrestamoAdicional buscarPorMotivo(String motivo);
	List<MotivoPrestamoAdicional> buscarTodos();
	void guardar(MotivoPrestamoAdicional motivoPrestamoAdicional);
	void eliminar(MotivoPrestamoAdicional motivoPrestamoAdicional);
}
