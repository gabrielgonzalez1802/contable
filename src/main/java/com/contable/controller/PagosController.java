package com.contable.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.contable.service.IPagosService;

@Controller
@RequestMapping("/pagos")
public class PagosController {
	
	@Autowired
	private IPagosService servicePagos;
	
	
	
}
