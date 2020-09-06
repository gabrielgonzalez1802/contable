package com.contable.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contable.model.Perfil;
import com.contable.model.Usuario;
import com.contable.repository.UsuariosRepository;
import com.contable.service.IUsuariosService;

@Service
public class UsuariosServiceJpa implements IUsuariosService{
	
	@Autowired
	private UsuariosRepository repo;

	@Override
	public Usuario buscarPorId(Integer id) {
		Optional<Usuario> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<Usuario> buscarPorPerfil(Perfil perfil) {
		return repo.findByPerfil(perfil);
	}

	@Override
	public List<Usuario> buscarPorEstado(Integer estatus) {
		return repo.findByEstatus(estatus);
	}

	@Override
	public List<Usuario> buscarPorPerfilEstado(Perfil perfil, Integer estatus) {
		return repo.findByPerfilAndEstatus(perfil, estatus);
	}

	@Override
	public void guardar(Usuario usurio) {
		repo.save(usurio);
	}

	@Override
	public void eliminar(Integer id) {
		Optional<Usuario> optional = repo.findById(id);
		if(optional.isPresent()) {
			repo.delete(optional.get());
		}
	}

}
