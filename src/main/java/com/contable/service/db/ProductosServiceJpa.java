package com.contable.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contable.model.Empresa;
import com.contable.model.Producto;
import com.contable.repository.ProductosRepository;
import com.contable.service.IProductosService;

@Service
public class ProductosServiceJpa implements IProductosService{
	
	@Autowired
	private ProductosRepository repo;

	@Override
	public Producto buscarPorId(Integer id) {
		Optional<Producto> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<Producto> buscarPorEmpresa(Empresa empresa) {
		return repo.findByEmpresa(empresa);
	}

	@Override
	public List<Producto> buscarPorEmpresaActivoFijo(Empresa empresa, Integer activoFijo) {
		return repo.findByEmpresaAndActivoFijo(empresa, activoFijo);
	}

	@Override
	public void guardar(Producto producto) {
		repo.save(producto);
	}

	@Override
	public void eliminar(Producto producto) {
		repo.delete(producto);
	}

}
