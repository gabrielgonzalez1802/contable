package com.contable.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/contabilidad")
public class ContabilidadController {

	@GetMapping("/mostrarContabilidad")
	public String mostrarContabilidad(Model model, HttpSession session) {
		
		return "contabilidad/contabilidad :: contabilidad";
	}
	
}
