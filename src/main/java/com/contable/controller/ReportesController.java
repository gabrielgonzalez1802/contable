package com.contable.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.contable.model.Abono;
import com.contable.model.Carpeta;
import com.contable.model.Cliente;
import com.contable.model.Empresa;
import com.contable.model.Prestamo;
import com.contable.model.Usuario;
import com.contable.service.IAbonosService;
import com.contable.service.ICarpetasService;
import com.contable.service.IClientesService;
import com.contable.service.IPrestamosService;
import com.contable.service.IUsuariosService;

@Controller
@RequestMapping("/reportes")
public class ReportesController {
	
	@Autowired
	private IPrestamosService servicePrestamos;
	
	@Autowired
	private ICarpetasService serviceCarpetas;
	
	@Autowired
	private IClientesService serviceClientes;
	
	@Autowired
	private IAbonosService serviceAbonos;
	
	@Autowired
	private IUsuariosService serviceUsuarios;
	
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    
    List<Prestamo> prestamos = new LinkedList<>();
    
    List<Abono> abonos = new LinkedList<>();

	@GetMapping("/listaReportes")
	public String listaReportes(Model model, HttpSession session) {
		List<Cliente> clientes = serviceClientes.buscarTodos();
		model.addAttribute("clientes", clientes);
		return "impresiones/listaReportes :: listaReportes";
	}
	
	@GetMapping("/listaPrestamos/{desde}/{hasta}")
	public String listaPrestamos(Model model, HttpSession session, @PathVariable("desde") String desde,
			@PathVariable("hasta") String hasta) throws ParseException {
		
		Integer idCarpeta = null;
		Carpeta carpeta = null;
		Date dateDesde = formatter.parse(desde);
		Date dateHasta = formatter.parse(hasta);
		
		if(session.getAttribute("carpeta") != null) {
			idCarpeta = (Integer) session.getAttribute("carpeta");
		}
		
		if(idCarpeta!=null) {
			carpeta = serviceCarpetas.buscarPorId(idCarpeta);
		}else {
			List<Carpeta> carpetas = serviceCarpetas.buscarTipoCarpetaEmpresa(1, (Empresa) session.getAttribute("empresa"));
			carpeta = carpetas.get(0);
		}
		
		prestamos = servicePrestamos.
				buscarPorCarpetaEmpresaFechasPorFechaDesc(carpeta,(Empresa) session.getAttribute("empresa"), dateDesde, dateHasta);
		model.addAttribute("prestamos", prestamos);
		return "impresiones/prestamos/listaPrestamos :: listaPrestamos";
	}

	@GetMapping("/listaPrestamos")
	public String listaPrestamos(Model model, HttpSession session) throws ParseException {
		model.addAttribute("empresa", (Empresa) session.getAttribute("empresa"));
		model.addAttribute("prestamos", prestamos);
		return "impresiones/prestamos/listaPrestamosImprimir :: #imprimirData";
	}
	
	@PostMapping("/cuadreCaja")
	public String cuadreCaja(Model model, String fecha, Integer userId,
			HttpSession session) throws ParseException {
		
		SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
		LocalDateTime fechaBusqueda = null;
		
		if(fecha.equals("")) {
			fechaBusqueda = convertToLocalDateTimeViaInstant(new Date());
		}else {
			fechaBusqueda = convertToLocalDateTimeViaInstant(formato.parse(fecha));
		}
		
		Date fechaTemp = convertToDateViaInstant(fechaBusqueda);
		
		Carpeta carpeta = null;
		if(session.getAttribute("carpeta")!=null) {
			carpeta = serviceCarpetas.buscarPorId((Integer) session.getAttribute("carpeta"));
		}else {
			List<Carpeta> carpetas = serviceCarpetas.buscarTipoCarpetaEmpresa(1, (Empresa) session.getAttribute("empresa"));
			carpeta = carpetas.get(0);
		}
		
		List<Abono> abonos = new LinkedList<>();
		double sumaEfectivo = 0;
		double sumaCheque = 0;
		double sumaDepositoTransferencia = 0;
		
		List<Abono> abonosTemp = new LinkedList<>();
		
		List<Prestamo> prestamos = servicePrestamos.buscarPorCarpeta(carpeta);
		if(userId>0) {
			Usuario usuarioTemp = serviceUsuarios.buscarPorId(userId);
			abonosTemp = serviceAbonos.buscarPorPrestamosUsuario(prestamos, usuarioTemp);
		}else {
			abonosTemp = serviceAbonos.buscarPorPrestamos(prestamos);
		}
		List<Abono> abonosDepured = new LinkedList<>();
		for (Abono abono : abonosTemp) {
			LocalDateTime fechaAbono = convertToLocalDateTimeViaInstant(abono.getFecha());
			if(fechaBusqueda.getYear() == fechaAbono.getYear() && 
					fechaBusqueda.getMonthValue() == fechaAbono.getMonthValue() && 
							fechaBusqueda.getDayOfMonth() == fechaAbono.getDayOfMonth()) {
				
				abonosDepured.add(abono);
			}
		}
		for (Abono abonoDepured : abonosDepured) {
			sumaEfectivo += abonoDepured.getEfectivo();
			sumaCheque += abonoDepured.getCheque();
			sumaDepositoTransferencia += abonoDepured.getTransferencia_deposito();
			abonos.add(abonoDepured);
		}
		
		List<Usuario> usuarios = serviceUsuarios.buscarPorEstado(1);
		model.addAttribute("usuarios", usuarios);
		model.addAttribute("carpeta", carpeta);
		model.addAttribute("sumaEfectivo", formato2d(sumaEfectivo));
		model.addAttribute("sumaCheque", formato2d(sumaCheque));
		model.addAttribute("sumaDepositoTransferencia", formato2d(sumaDepositoTransferencia));
		model.addAttribute("abonos", abonos);
		model.addAttribute("fecha", fechaTemp);
		model.addAttribute("userAcct", userId);
		model.addAttribute("empresa", (Empresa) session.getAttribute("empresa"));
		return "impresiones/caja/cuadreCaja :: #imprimirData";
	}
	
	@PostMapping("/listaAbonos")
	public String listaAbonos(Model model, HttpSession session, String desde,
			String hasta, Integer idCliente) throws ParseException {
		
//		Integer idCarpeta = null;
//		Carpeta carpeta = null;
		
		Date dateDesde = null;
		Date dateHasta = null;
		
		if(!desde.equals("") && !hasta.equals("")) {
			dateDesde = formatter.parse(desde);
			dateHasta = formatter.parse(hasta);
		}
		
//		if(session.getAttribute("carpeta") != null) {
//			idCarpeta = (Integer) session.getAttribute("carpeta");
//		}
//		
//		if(idCarpeta!=null) {
//			carpeta = serviceCarpetas.buscarPorId(idCarpeta);
//		}else {
//			List<Carpeta> carpetas = serviceCarpetas.buscarTipoCarpetaEmpresa(1, (Empresa) session.getAttribute("empresa"));
//			carpeta = carpetas.get(0);
//		}
		
		if((dateDesde == null || dateHasta == null) && idCliente == 0 ) {
			abonos = serviceAbonos.buscarPorEmpresa((Empresa) session.getAttribute("empresa"));
		}else if((dateDesde == null || dateHasta == null) && idCliente > 0) {
			abonos = serviceAbonos.buscarPorClienteEmpresa(serviceClientes.buscarPorId(idCliente), (Empresa) session.getAttribute("empresa"));
		}else if((dateDesde != null && dateHasta != null) && idCliente == 0) {
			abonos = serviceAbonos.buscarPorEmpresaFechas((Empresa) session.getAttribute("empresa"), dateDesde, dateHasta);
		}else if((dateDesde != null && dateHasta != null) && idCliente > 0) {
			abonos = serviceAbonos.buscarPorClienteEmpresaFechas(serviceClientes.buscarPorId(idCliente), (Empresa) session.getAttribute("empresa"), dateDesde, dateHasta);
		}else {
			abonos = new LinkedList<>();
		}
		
		model.addAttribute("empresa", (Empresa) session.getAttribute("empresa"));
		model.addAttribute("abonos", abonos);
		return "impresiones/caja/listaAbonos :: #imprimirData";
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
	
	public double formato2d(double number) {
		number = Math.round(number * 100);
		number = number/100;
		return number;
	}
	
	@InitBinder
	public void initBinder(WebDataBinder webDataBinder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		webDataBinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
	}
}
