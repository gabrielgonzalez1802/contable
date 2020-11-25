package com.contable.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contable.model.MotivoPrestamoAdicional;
import com.contable.repository.MotivosPrestamosAdicionalesRepository;
import com.contable.service.IMotivosPrestamosAdicionalesService;

@Service
public class MotivosPrestamosAdicionalesServiceJpa implements IMotivosPrestamosAdicionalesService{

	@Autowired
	private MotivosPrestamosAdicionalesRepository repo;
	
	@Override
	public MotivoPrestamoAdicional buscarPorId(Integer id) {
		Optional<MotivoPrestamoAdicional> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public MotivoPrestamoAdicional buscarPorMotivo(String motivo) {
		return repo.findByMotivo(motivo);
	}

	@Override
	public List<MotivoPrestamoAdicional> buscarTodos() {
		return repo.findAll();
	}

	@Override
	public void guardar(MotivoPrestamoAdicional motivoPrestamoAdicional) {
		repo.save(motivoPrestamoAdicional);
	}

	@Override
	public void eliminar(MotivoPrestamoAdicional motivoPrestamoAdicional) {
		repo.delete(motivoPrestamoAdicional);
	}

}
