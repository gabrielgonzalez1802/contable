package com.contable.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.contable.model.Usuario;
import com.contable.service.IUsuariosService;

@Controller
public class HomeController {
	
	@Autowired
	private IUsuariosService serviceUsuarios;
	
	@GetMapping("/")
	public String mostrarHome(Model model, HttpSession session, Authentication auth) {
		//Usuario logueado
		String userName = auth.getName();
		Usuario usuario = null;
		System.out.println("username: "+userName);
		usuario = serviceUsuarios.buscarPorUsername(userName);
		usuario.setPassword(null);
		//Agregamos el usuario a la sesion
		session.setAttribute("usuario", usuario);
//		return "home";
		return "index";
	}
	
	@GetMapping("/login" )
	public String mostrarLogin() {
		return "login";
	}
	
}
