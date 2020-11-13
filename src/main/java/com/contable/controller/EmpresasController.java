package com.contable.controller;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.contable.model.Empresa;
import com.contable.service.IEmpresasService;
import com.contable.util.Utileria;

@Controller
@RequestMapping("/empresas")
public class EmpresasController {
	
	@Autowired
	private IEmpresasService serviceEmpresas;
	
	@Value("${contable.ruta.imagenes}")
	private String ruta;
	
	@GetMapping("/listaEmpresas")
	public String listaEmpresas(Model model) {
		List<Empresa> empresas = serviceEmpresas.buscarTodas();
		model.addAttribute("empresas", empresas);
		return "empresas/listaEmpresas :: listaEmpresas";
	}
	
	@GetMapping("/agregarEmpresa")
	public String agregarEmpresa(Model model) {
		Empresa empresa = new Empresa();
		model.addAttribute("empresa", empresa);
		return "empresas/formEmpresas :: form";
	}
	
	@PostMapping("/crear")
	public ResponseEntity<String> crear(Empresa empresa){
		String response = "0";
		if(!empresa.getLogoTemp().isEmpty()) {
			String nombreImagen = Utileria.guardarArchivo(empresa.getLogoTemp(), ruta);
			if (nombreImagen != null) {
				empresa.setLogo(nombreImagen);
			}
		}
		serviceEmpresas.guardar(empresa);
		if(empresa.getId()!=null) {
			response = "1";
		}
		return new ResponseEntity<String>(response, HttpStatus.CREATED);
	}
	
	@PostMapping("/modificar")
	public ResponseEntity<String> modificar(Empresa empresa){
		String response = "0";
		
		Empresa originalEmpresa = serviceEmpresas.buscarPorId(empresa.getId());
		
		if(!empresa.getLogoTemp().isEmpty()) {
			//verificamos si el registro poseia foto
			if(!originalEmpresa.getLogo().isEmpty() || !originalEmpresa.getLogo().equals("")) {
				File imageFile = new File(ruta+ originalEmpresa.getLogo());
				imageFile.delete();
			}
			
			String nombreImagen = Utileria.guardarArchivo(empresa.getLogoTemp(), ruta);
			if (nombreImagen != null) {
				empresa.setLogo(nombreImagen);
			}
		}else {
			empresa.setLogo(originalEmpresa.getLogo());
		}
		
		serviceEmpresas.guardar(empresa);
		if(empresa.getId()!=null) {
			response = "1";
		}
		return new ResponseEntity<String>(response, HttpStatus.ACCEPTED);
	}
	
	@PostMapping("/change")
	public String change(Integer idEmpresa, Model model, HttpSession session) {
		List<Empresa> empresas = serviceEmpresas.buscarTodas();
		Empresa empresa = serviceEmpresas.buscarPorId(idEmpresa);
		model.addAttribute("empresas", empresas);
		model.addAttribute("empresa", empresa);
		session.setAttribute("empresa", empresa);
		return "index :: #selectCambioEmpresa";
	}
	
	@GetMapping("/modificarEmpresa/{id}")
	public String modificarEmpresa(Model model, @PathVariable("id") Integer idEmpresa) {
		Empresa empresa = serviceEmpresas.buscarPorId(idEmpresa);
		model.addAttribute("empresa", empresa);
		return "empresas/formEmpresas :: formEdit";
	}
}
