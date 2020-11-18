package com.contable.controller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.contable.model.Empresa;
import com.contable.model.Token;
import com.contable.model.Usuario;
import com.contable.service.IEmpresasService;
import com.contable.service.ITokensService;
import com.contable.service.IUsuariosService;

@Controller
public class HomeController {
	
	@Autowired
	private IUsuariosService serviceUsuarios;
	
	@Autowired
	private IEmpresasService serviceEmpresas;
	
	@Autowired
	private ITokensService serviceTokens;
	
	@GetMapping("/")
	public String mostrarHome(Model model, HttpSession session, Authentication auth) {
		//Usuario logueado
		String userName = auth.getName();
		Usuario usuario = null;
		System.out.println("username: "+userName);
		usuario = serviceUsuarios.buscarPorUsername(userName);
		usuario.setPassword(null);
		//Agregamos el usuario a la sesion
		List<Empresa> empresas = serviceEmpresas.buscarTodas();
		session.setAttribute("usuario", usuario);
		if(session.getAttribute("empresa") == null) {
			Empresa empresa = serviceEmpresas.buscarPorId(1);
			model.addAttribute("empresa", empresa);
			session.setAttribute("empresa", empresa);
		}else {
			model.addAttribute("empresa", (Empresa) session.getAttribute("empresa"));
		}
		model.addAttribute("empresas", empresas);
		return "index";
	}
	
	@GetMapping("/login" )
	public String mostrarLogin(Model model) {
		List<Empresa> empresas = serviceEmpresas.buscarTodas();
		model.addAttribute("empresas", empresas);
		return "login";
	}
	
	@PostMapping("/verificarCodigo" )
	public ResponseEntity<String> verificarCodigo(Model model, String codigo) {
		String response  = "0";
		LocalDateTime dateAcct =  LocalDateTime.now();
		LocalDateTime fechaExpire = null;
		List<Token> tokens = serviceTokens.buscarToken(codigo);
		if(!tokens.isEmpty()) {
			Token token = tokens.get(0);
			if(token.getFechaExpiracion()!=null) {
				fechaExpire = convertToLocalDateTimeViaInstant(token.getFechaExpiracion());
				if(fechaExpire.isBefore(dateAcct)) {
					token.setEstado(0);
					serviceTokens.guardar(token);
				}else {
					response  = token.getToken();
				}
			}else {
				if(token.getEstado().intValue() == 1) {
					response  = token.getToken();
				}
			}
		}
		return new ResponseEntity<String>(response, HttpStatus.ACCEPTED);
	}
	
	@GetMapping("/registro" )
	public String registro(Model model) {
		List<Empresa> empresas = serviceEmpresas.buscarTodas();
		model.addAttribute("empresas", empresas);
		return "registro";
	}
	
	@PostMapping("/setEmpresa" )
	@ResponseBody
	public ResponseEntity<String> setEmpresa(Integer empresaId, HttpSession session) {
		String response = "0";
		Empresa empresa = serviceEmpresas.buscarPorId(empresaId);
		if(response!=null) {
			response = "1";
		}
		session.setAttribute("empresa", empresa);
		return new ResponseEntity<String>(response, HttpStatus.ACCEPTED);
	}
	
	public LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
	    return dateToConvert.toInstant()
	      .atZone(ZoneId.systemDefault())
	      .toLocalDateTime();
	}
	
	public Date convertToDateViaInstant(LocalDateTime dateToConvert) {
	    return java.util.Date
	      .from(dateToConvert.atZone(ZoneId.systemDefault())
	      .toInstant());
	}
	
}
